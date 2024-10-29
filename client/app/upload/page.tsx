"use client";
import React, { useState } from "react";
import axios from "axios";
import crypto from "crypto";

export default function Page() {
  const [file, setFile] = useState<File | null>(null);
  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
    }
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!file) {
      console.error("No file selected");
      return;
    }

    const chunkSize = 5 * 1024 * 1024; // 5MB chunks
    const totalChunks = Math.ceil(file.size / chunkSize);

    // Calculate SHA-256 checksum of the file
    // Calculate SHA-256 checksum of the file
    const fileHash = await new Promise<string>((resolve, reject) => {
      const hash = crypto.createHash("sha256");
      const reader = new FileReader();
      reader.onload = () => {
        const arrayBuffer = reader.result as ArrayBuffer;
        const uint8Array = new Uint8Array(arrayBuffer);
        hash.update(uint8Array);
        resolve(hash.digest("hex"));
      };
      reader.onerror = reject;
      reader.readAsArrayBuffer(file);
    });

    for (let i = 0; i < totalChunks; i++) {
      const start = i * chunkSize;
      const end = Math.min(start + chunkSize, file.size);
      const chunk = file.slice(start, end);

      const formData = new FormData();
      formData.append("chunk", chunk);
      formData.append("chunkNumber", `${i}`);
      formData.append("totalChunks", `${totalChunks}`);
      formData.append("fileName", file.name);
      formData.append("fileHash", fileHash); // Send the file hash to the backend

      try {
        const response = await axios.post(
          `${process.env.NEXT_PUBLIC_BACKEND_IP}/api/upload-chunk`,
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );
        console.log("Chunk uploaded successfully:", response.data);
      } catch (error) {
        console.error("Error uploading chunk:", error);
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="file" onChange={handleFileChange} />
      <button type="submit">Upload Video</button>
    </form>
  );
}
