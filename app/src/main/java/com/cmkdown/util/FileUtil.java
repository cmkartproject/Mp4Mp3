package com.cmkdown.util;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {
    private static final String CMKDOWN_DIR = "CMKDOWN";

    public static File getDownloadsDirectory() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), CMKDOWN_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String generateFileName(String title, String format) {
        String sanitized = title.replaceAll("[^a-zA-Z0-9.-]", "_");
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return sanitized + "_" + timestamp + "." + format;
    }

    public static String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups),
                units[digitGroups]);
    }

    public static String getExtension(String mimeType) {
        if (mimeType == null) return "mp4";
        if (mimeType.contains("audio")) return "mp3";
        if (mimeType.contains("video")) return "mp4";
        return "mp4";
    }
}
