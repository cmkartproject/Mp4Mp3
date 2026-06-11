package com.cmkdown.downloader;

import android.util.Log;

import com.cmkdown.util.FileUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FacebookDownloader {
    private static final String TAG = "FacebookDownloader";
    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://www.facebook.com/watch";

    public void downloadVideo(String url, File outputDir, DownloadProgressListener listener) throws Exception {
        listener.onProgress(10, "Fetching Facebook video info...");
        
        String videoId = extractVideoId(url);
        if (videoId == null) {
            throw new Exception("Invalid Facebook URL");
        }

        try {
            String videoInfo = getVideoInfo(url);
            String title = extractTitle(videoInfo);
            String fileName = FileUtil.generateFileName(title != null ? title : "facebook_video", "mp4");
            File outputFile = new File(outputDir, fileName);

            listener.onProgress(30, "Processing video...");
            
            // Extract download URL from Facebook page
            String downloadUrl = extractDownloadUrl(videoInfo);
            
            if (downloadUrl != null) {
                downloadFile(downloadUrl, outputFile, listener);
                listener.onSuccess(outputFile.getAbsolutePath());
            } else {
                throw new Exception("Could not extract download link");
            }
        } catch (Exception e) {
            Log.e(TAG, "Download failed", e);
            listener.onError("Download failed: " + e.getMessage());
        }
    }

    private String getVideoInfo(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build();
        
        Response response = client.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body().string();
        }
        return null;
    }

    private String extractVideoId(String url) {
        Pattern pattern = Pattern.compile("(?:facebook\\.com/watch/\\?v=|video\\.)(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractTitle(String html) {
        try {
            Pattern pattern = Pattern.compile("<meta property=\"og:title\" content=\"([^\"]*)");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting title", e);
        }
        return null;
    }

    private String extractDownloadUrl(String html) {
        try {
            // Extract video URL from HTML
            Pattern pattern = Pattern.compile("\"hdSrc\":\"([^\"]*)");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                String url = matcher.group(1);
                return url.replace("\\\\", "");
            }
            
            // Alternative pattern
            pattern = Pattern.compile("src=\"([^\"]*facebook[^\"]*\\.mp4)");
            matcher = pattern.matcher(html);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting download URL", e);
        }
        return null;
    }

    private void downloadFile(String downloadUrl, File outputFile,
                             DownloadProgressListener listener) throws Exception {
        Request request = new Request.Builder()
                .url(downloadUrl)
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
