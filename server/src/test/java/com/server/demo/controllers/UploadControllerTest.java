package com.server.demo.controllers;

import static com.server.demo.helpers.ByteHelper.createRandomByteArray;
import static com.server.demo.helpers.HashHelper.getSha256Hash;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.MessageDigest;
import java.util.Random;

@SpringBootTest
@AutoConfigureMockMvc
class UploadControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_Get_UploadId() throws Exception {
        this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk());
    }

    @Test
    void should_Get_HashValue_On_Correct_id() throws Exception {
        MvcResult r = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();
        this.mockMvc.perform(post("/api/hashValue").param("fileHash", "123").param("uploadId", r.getResponse().getContentAsString())).andExpect(status().isOk());
    }

    @Test
    void should_Not_Get_HashValue_On_Wrong_Id() throws Exception {
        MvcResult r = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();
        this.mockMvc.perform(post("/api/hashValue").param("fileHash", "123").param("uploadId", "wrong-id")).andExpect(status().isBadRequest());
    }

    @Test
    void should_Upload_Chunk_On_Correct_Id() throws Exception {
        MvcResult uploadId = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();

        byte[] mp4FileContent = createRandomByteArray(1024 * 1024);
        MockMultipartFile mockFile = new MockMultipartFile("chunk", "video.mp4", "video/mp4", mp4FileContent);

        String fileHash = getSha256Hash(mockFile.getBytes());
        assertNotNull(fileHash);

        this.mockMvc.perform(post("/api/hashValue").param("fileHash", fileHash).param("uploadId", uploadId.getResponse()
                .getContentAsString()).param("chunkHash", fileHash))
                .andExpect(status().isOk());
        this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", "0").param("totalChunks", "1")
                .param("fileName", "test.txt").param("uploadId", uploadId.getResponse().getContentAsString())
                .param("chunkHash", fileHash))
                .andExpect(status().isOk());
    }

    @Test
    void should_Not_Upload_Chunk_On_Wrong_Id() throws Exception {
        MvcResult uploadId = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();

        byte[] mp4FileContent = createRandomByteArray(1024 * 1024);
        MockMultipartFile mockFile = new MockMultipartFile("chunk", "video.mp4", "video/mp4", mp4FileContent);

        String fileHash = getSha256Hash(mockFile.getBytes());
        assertNotNull(fileHash);

        this.mockMvc.perform(post("/api/hashValue").param("fileHash", fileHash).param("wrong-id", uploadId.getResponse()
                        .getContentAsString()))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", "0").param("totalChunks", "1")
                .param("fileName", "test.txt").param("wrong-id", uploadId.getResponse().getContentAsString())).andExpect(status().isBadRequest());
    }

    @Test
    void should_Upload_Chunk_On_Correct_Id_100MB() throws Exception {
        MvcResult uploadId = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();

        int chunkSize = 100 * 1024 * 1024; // 10MB
        int totalChunks = 10;
        byte[] mp4FileContent = createRandomByteArray(chunkSize);

        MockMultipartFile mockFileFull = new MockMultipartFile("chunk", "video.mp4", "video/mp4", mp4FileContent);

        String fileHash = getSha256Hash(mockFileFull.getBytes());
        assertNotNull(fileHash);

        this.mockMvc.perform(post("/api/hashValue").param("fileHash", fileHash).param("uploadId", uploadId.getResponse()
                        .getContentAsString()))
                .andExpect(status().isOk());

        int chunkLength = chunkSize / totalChunks;
        byte[] chunk = new byte[chunkLength];
        for (int i = 0; i < totalChunks; i++) {
            System.arraycopy(mp4FileContent, i * chunkLength, chunk, 0, chunkLength);
            MockMultipartFile mockFile = new MockMultipartFile("chunk", "video.mp4", "video/mp4", chunk);

            String fileHash2 = getSha256Hash(mockFile.getBytes());
            assertNotNull(fileHash2);

            if(i < totalChunks-1){
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                        .param("fileName", "video.mp4").param("uploadId", uploadId.getResponse().getContentAsString()).param("chunkHash", fileHash2))
                        .andExpect(status().isOk());
            }else{
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                                .param("fileName", "video.mp4").param("uploadId", uploadId.getResponse().getContentAsString()).param("chunkHash", fileHash2))
                        .andExpect(status().isOk())
                        .andExpect(content().string("File uploaded successfully"))
                        .andReturn();
            }
        }
    }

    @Test
    void should_Return_Bad_Request_On_Wrong_Chunk_Hash_100MB() throws Exception {
        MvcResult uploadId = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();

        int chunkSize = 100 * 1024 * 1024; // 10MB
        int totalChunks = 10;

        byte[] mp4FileContent = createRandomByteArray(chunkSize);

        MockMultipartFile mockFileFull = new MockMultipartFile("chunk", "video.mp4", "video/mp4", mp4FileContent);

        String fileHash = getSha256Hash(mockFileFull.getBytes());
        assertNotNull(fileHash);

        this.mockMvc.perform(post("/api/hashValue").param("fileHash", fileHash).param("uploadId", uploadId.getResponse()
                        .getContentAsString()))
                .andExpect(status().isOk());

        int chunkLength = chunkSize / totalChunks;
        byte[] chunk = new byte[chunkLength];

        for (int i = 0; i < totalChunks; i++) {
            System.arraycopy(mp4FileContent, i * chunkLength, chunk, 0, chunkLength);
            MockMultipartFile mockFile = new MockMultipartFile("chunk", "video.mp4", "video/mp4", chunk);

            String fileHash2 = getSha256Hash(mockFile.getBytes());
            assertNotNull(fileHash2);

            if(i < totalChunks-1){
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                        .param("fileName", "video.mp4").param("uploadId", uploadId.getResponse().getContentAsString()).param("chunkHash", fileHash2)).andExpect(status().isOk());
            }else{
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                                .param("fileName", "video.mp4").param("uploadId", uploadId.getResponse().getContentAsString()).param("chunkHash", anyString()))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("Chunk hash mismatch. Please re-upload chunk " + i))
                        .andReturn();
            }
        }
    }

    @Test
    void should_ReUpload_On_Wrong_Chunk_Hash_100MB() throws Exception {
        MvcResult uploadId = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();

        int chunkSize = 100 * 1024 * 1024; // 10MB
        int totalChunks = 10;
        byte[] mp4FileContent = createRandomByteArray(chunkSize);

        MockMultipartFile mockFileFull = new MockMultipartFile("chunk", "video.mp4", "video/mp4", mp4FileContent);

        String fileHash = getSha256Hash(mockFileFull.getBytes());
        assertNotNull(fileHash);

        this.mockMvc.perform(post("/api/hashValue").param("fileHash", fileHash).param("uploadId", uploadId.getResponse()
                        .getContentAsString()))
                .andExpect(status().isOk());

        int chunkLength = chunkSize / totalChunks;
        byte[] chunk = new byte[chunkLength];
        for (int i = 0; i < totalChunks; i++) {
            System.arraycopy(mp4FileContent, i * chunkLength, chunk, 0, chunkLength);
            MockMultipartFile mockFile = new MockMultipartFile("chunk", "video.mp4", "video/mp4", chunk);

            String fileHash2 = getSha256Hash(mockFile.getBytes());
            assertNotNull(fileHash2);

            if(i < totalChunks-1){
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                                .param("fileName", "video.mp4").param("uploadId", uploadId.getResponse().getContentAsString()).param("chunkHash", fileHash2))
                        .andExpect(status().isOk());
            }else{
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                                .param("fileName", "video.mp4").param("uploadId", uploadId.getResponse().getContentAsString()).param("chunkHash", anyString()))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("Chunk hash mismatch. Please re-upload chunk " + i))
                        .andReturn();

                // re-upload chunk
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                                .param("fileName", "video.mp4").param("uploadId", uploadId.getResponse().getContentAsString()).param("chunkHash", fileHash2))
                        .andExpect(status().isOk())
                        .andExpect(content().string("File uploaded successfully"))
                        .andReturn();
            }
        }
    }

    @Test
    void should_Not_Upload_On_Wrong_Id_100MB() throws Exception {
        MvcResult uploadId = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();

        int chunkSize = 100 * 1024 * 1024; // 10MB
        int totalChunks = 10;
        byte[] mp4FileContent = createRandomByteArray(chunkSize);

        MockMultipartFile mockFileFull = new MockMultipartFile("chunk", "video.mp4", "video/mp4", mp4FileContent);

        String fileHash = getSha256Hash(mockFileFull.getBytes());
        assertNotNull(fileHash);

        this.mockMvc.perform(post("/api/hashValue").param("fileHash", fileHash).param("uploadId", uploadId.getResponse()
                        .getContentAsString()))
                .andExpect(status().isOk());

        int chunkLength = chunkSize / totalChunks;
        byte[] chunk = new byte[chunkLength];
        for (int i = 0; i < totalChunks; i++) {
            System.arraycopy(mp4FileContent, i * chunkLength, chunk, 0, chunkLength);
            MockMultipartFile mockFile = new MockMultipartFile("chunk", "video.mp4", "video/mp4", chunk);

            if(i < totalChunks-1){
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                        .param("fileName", "video.mp4").param("wrong-id", uploadId.getResponse().getContentAsString())).andExpect(status().isBadRequest());
            }else{
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                                .param("fileName", "video.mp4").param("wrong-id", uploadId.getResponse().getContentAsString()))
                        .andExpect(status().isBadRequest())
                        .andReturn();
            }
        }
    }

    @Test
    void should_Upload_Chunk_On_Correct_Id_10GB() throws Exception {
        MvcResult uploadId = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();

        int chunkSize = 1000 * 1024 * 1024; // 10MB
        int totalChunks = 100;

        byte[] mp4FileContent = createRandomByteArray(chunkSize);

        MockMultipartFile mockFileFull = new MockMultipartFile("chunk", "video.mp4", "video/mp4", mp4FileContent);

        String fileHash = getSha256Hash(mockFileFull.getBytes());
        assertNotNull(fileHash);

        this.mockMvc.perform(post("/api/hashValue").param("fileHash", fileHash).param("uploadId", uploadId.getResponse()
                        .getContentAsString()))
                .andExpect(status().isOk());

        int chunkLength = chunkSize / totalChunks;
        byte[] chunk = new byte[chunkLength];
        for (int i = 0; i < totalChunks; i++) {
            System.arraycopy(mp4FileContent, i * chunkLength, chunk, 0, chunkLength);
            MockMultipartFile mockFile = new MockMultipartFile("chunk", "video.mp4", "video/mp4", chunk);

            String fileHash2 = getSha256Hash(mockFile.getBytes());
            assertNotNull(fileHash2);

            if(i < totalChunks-1){
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                        .param("fileName", "video.mp4").param("uploadId", uploadId.getResponse().getContentAsString()).param("chunkHash", fileHash2))
                        .andExpect(status().isOk());
            }else{
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                                .param("fileName", "video.mp4").param("uploadId", uploadId.getResponse().getContentAsString()).param("chunkHash", fileHash2))
                        .andExpect(status().isOk())
                        .andExpect(content().string("File uploaded successfully"))
                        .andReturn();
            }
        }
    }

    @Test
    void should_Not_Upload_Chunk_On_Wrong_Id_10GB() throws Exception {
        MvcResult uploadId = this.mockMvc.perform(get("/api/upload-id")).andExpect(status().isOk()).andReturn();

        int chunkSize = 1000 * 1024 * 1024; // 10MB
        int totalChunks = 100;
        byte[] mp4FileContent = createRandomByteArray(chunkSize);

        MockMultipartFile mockFileFull = new MockMultipartFile("chunk", "video.mp4", "video/mp4", mp4FileContent);

        String fileHash = getSha256Hash(mockFileFull.getBytes());
        assertNotNull(fileHash);

        this.mockMvc.perform(post("/api/hashValue").param("fileHash", fileHash).param("uploadId", uploadId.getResponse()
                        .getContentAsString()))
                .andExpect(status().isOk());

        int chunkLength = chunkSize / totalChunks;
        byte[] chunk = new byte[chunkLength];
        for (int i = 0; i < totalChunks; i++) {
            System.arraycopy(mp4FileContent, i * chunkLength, chunk, 0, chunkLength);
            MockMultipartFile mockFile = new MockMultipartFile("chunk", "video.mp4", "video/mp4", chunk);

            if(i < totalChunks-1){
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                        .param("fileName", "video.mp4").param("wrong-id", uploadId.getResponse().getContentAsString())).andExpect(status().isBadRequest());
            }else{
                this.mockMvc.perform(multipart("/api/upload-chunk").file(mockFile).param("chunkNumber", String.valueOf(i)).param("totalChunks", String.valueOf(totalChunks))
                                .param("fileName", "video.mp4").param("wrong-id", uploadId.getResponse().getContentAsString()))
                        .andExpect(status().isBadRequest())
                        .andReturn();
            }
        }
    }
}