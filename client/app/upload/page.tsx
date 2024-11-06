"use client";
import React, { useState } from "react";
import axios from "axios";
import crypto from "crypto"; // You can use this, but there are better ways in the browser.

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

    try {
      // Fetch the upload ID (just once, outside the loop)
      const response = await axios.get(
        `${process.env.NEXT_PUBLIC_BACKEND_IP}/api/upload-id`
      );
      const uploadId = response.data;
      if (!uploadId) {
        console.error("No upload ID found");
        return;
      }

      // Iterate over the chunks
      const uploadPromises = [];
      for (let i = 0; i < totalChunks; i++) {
        const start = i * chunkSize;
        const end = Math.min(start + chunkSize, file.size);
        const chunk = file.slice(start, end);

        // Hash the current chunk (no need to hash the full file upfront)
        const chunkHashHex = await getChunkHash(chunk);

        const formData = new FormData();
        formData.append("chunk", chunk);
        formData.append("chunkNumber", `${i}`);
        formData.append("totalChunks", `${totalChunks}`);
        formData.append("fileName", file.name);
        formData.append("uploadId", uploadId);
        formData.append("chunkHash", chunkHashHex);

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

  // Helper function to calculate the hash of a chunk
  const getChunkHash = (chunk: Blob): Promise<string> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const arrayBuffer = reader.result as ArrayBuffer;
        const uint8Array = new Uint8Array(arrayBuffer);
        const hash = crypto.createHash("sha256");
        hash.update(uint8Array);
        resolve(hash.digest("hex"));
      };
      reader.onerror = (error) => reject(error);
      reader.readAsArrayBuffer(chunk);
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="file" onChange={handleFileChange} />
      <button type="submit">Upload Video</button>
    </form>
  );
}
