"use client";
import React, { useState } from "react";
import axios from "axios";
import crypto from "crypto";

var uploadId: string | null = null;
export default function Page() {
  const [file, setFile] = useState<File | null>(null);
  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
    }
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!file) {
      console.error("No file selected");
      return;
    }

    const chunkSize = 5 * 1024 * 1024; // 5MB chunks
    const totalChunks = Math.ceil(file.size / chunkSize);

    // Calculate SHA-256 checksum of the file
    const fileHash = crypto.createHash("sha256");
    const reader = new FileReader();
    reader.onload = async () => {
      const arrayBuffer = reader.result as ArrayBuffer;
      const uint8Array = new Uint8Array(arrayBuffer);
      fileHash.update(uint8Array);
      const hash = fileHash.digest("hex");

      // Fetch the upload ID
      try {
        const response = await axios.get(
          `${process.env.NEXT_PUBLIC_BACKEND_IP}/api/upload-id`
        );
        uploadId = response.data;

        if (!uploadId) {
          console.error("No upload ID found");
          return;
        }

        await axios.post(
          `${process.env.NEXT_PUBLIC_BACKEND_IP}/api/hashValue`,
          null,
          {
            params: { fileHash: hash, uploadId },
          }
        );

        const uploadPromises = [];

        for (let i = 0; i < totalChunks; i++) {
          const start = i * chunkSize;
          const end = Math.min(start + chunkSize, file.size);
          const chunk = file.slice(start, end);

          const formData = new FormData();
          formData.append("chunk", chunk);
          formData.append("chunkNumber", `${i}`);
          formData.append("totalChunks", `${totalChunks}`);
          formData.append("fileName", file.name);
          formData.append("uploadId", uploadId!);

          // Add the chunk upload promise to the array
          uploadPromises.push(
            axios.post(
              `${process.env.NEXT_PUBLIC_BACKEND_IP}/api/upload-chunk`,
              formData,
              {
                headers: { "Content-Type": "multipart/form-data" },
              }
            )
          );
        }

        // Wait for all chunk uploads to complete
        const responses = await Promise.all(uploadPromises);
        responses.forEach((response) => {
          console.log(response.data);
        });

        console.log("All chunks uploaded successfully");
      } catch (error) {
        console.error("Error uploading file:", error);
      }
    };

    reader.onerror = (error) => {
      console.error("Error reading file:", error);
    };

    reader.readAsArrayBuffer(file);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="file" onChange={handleFileChange} />
      <button type="submit">Upload Video</button>
    </form>
  );
}
