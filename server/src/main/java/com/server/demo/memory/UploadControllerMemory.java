package com.server.demo.memory;

import java.util.HashMap;
import java.util.Map;

public class UploadControllerMemory {
    public static String uploadDir = "uploads/videos/";

    public static final Map<String, String> uploadIdHashValueMap = new HashMap<>();
    public static final Map<String, Integer> uploadIdChunkCountMap = new HashMap<>();
}
