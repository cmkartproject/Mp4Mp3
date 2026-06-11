package com.cmkdown.downloader;

import android.util.Log;

import com.cmkdown.util.FileUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InstagramDownloader {
    private static final String TAG = "InstagramDownloader";
    private final OkHttpClient client = new OkHttpClient();

    public void downloadVideo(String url, File outputDir, DownloadProgressListener listener) throws Exception {
        listener.onProgress(10, "Fetching Instagram video info...");
        
        String postId = extractPostId(url);
        if (postId == null) {
            throw new Exception("Invalid Instagram URL");
        }

        try {
            String pageContent = getPageContent(url);
            String title = extractCaption(pageContent);
            String fileName = FileUtil.generateFileName(
                    title != null ? title : "instagram_video", "mp4");
            File outputFile = new File(outputDir, fileName);

            listener.onProgress(30, "Processing video...");
            
            // Extract video URL
            String videoUrl = extractVideoUrl(pageContent);
            
            if (videoUrl != null) {
                downloadFile(videoUrl, outputFile, listener);
                listener.onSuccess(outputFile.getAbsolutePath());
            } else {
                throw new Exception("Could not extract video link");
            }
        } catch (Exception e) {
            Log.e(TAG, "Download failed", e);
            listener.onError("Download failed: " + e.getMessage());
        }
    }

    private String getPageContent(String url) throws Exception {
        // Append ?__a=1 to get JSON response from older Instagram API
        String apiUrl = url.endsWith("/") ? url + "?__a=1" : url + "/?__a=1";
        
        Request request = new Request.Builder()
                .url(apiUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build();
        
        Response response = client.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body().string();
        }
        return null;
    }

    private String extractPostId(String url) {
        Pattern pattern = Pattern.compile("(?:instagram\\.com/p/|instagram\\.com/reel/)([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractCaption(String json) {
        try {
            Pattern pattern = Pattern.compile("\"caption\":\"([^\"]*)");
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                String caption = matcher.group(1);
                // Truncate if too long
                return caption.length() > 50 ? caption.substring(0, 50) : caption;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting caption", e);
        }
        return null;
    }

    private String extractVideoUrl(String json) {
        try {
            // Try to find video_url in JSON
            Pattern pattern = Pattern.compile("\"video_url\":\"([^\"]*)");
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                String url = matcher.group(1);
                return url.replace("\\\\", "").replace("\\\"", "\"");
            }
            
            // Alternative: look for media objects
            pattern = Pattern.compile("\"media_type\":2.*?\"url\":\"([^\"]*)");
            matcher = pattern.matcher(json);
            if (matcher.find()) {
                return matcher.group(1).replace("\\\\", "");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting video URL", e);
        }
        return null;
    }

    private void downloadFile(String downloadUrl, File outputFile,
                             DownloadProgressListener listener) throws Exception {
        Request request = new Request.Builder()
                .url(downloadUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build();
        
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("Failed to download video");
        }

        long totalSize = response.body().contentLength();
        byte[] buffer = new byte[4096];
        int bytesRead;
        long downloadedSize = 0;

        try (java.io.InputStream input = response.body().byteStream();
             java.io.FileOutputStream output = new java.io.FileOutputStream(outputFile)) {
            
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                downloadedSize += bytesRead;
                int progress = (int) ((downloadedSize * 100) / totalSize);
                listener.onProgress(30 + (progress * 70 / 100), "Downloading: " + progress + "%");
            }
        }
    }

    public interface DownloadProgressListener {
        void onProgress(int progress, String message);
        void onSuccess(String filePath);
        void onError(String error);
    }
}
