package net.knoddy.skeletonscience;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileNameGenerator {
    public static String generateTimestampedFileName(String baseName, String extension) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = now.format(formatter);
        return baseName + "_" + timestamp + "." + extension;
    }
}

