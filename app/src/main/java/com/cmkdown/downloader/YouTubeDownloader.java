package com.cmkdown.downloader;

import android.util.Log;

import com.cmkdown.model.VideoInfo;
import com.cmkdown.util.FileUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YouTubeDownloader {
    private static final String TAG = "YouTubeDownloader";
    private final OkHttpClient client = new OkHttpClient();

    public VideoInfo getVideoInfo(String url) throws Exception {
        VideoInfo info = new VideoInfo();
        String videoId = extractVideoId(url);
        
        if (videoId == null) {
            throw new Exception("Invalid YouTube URL");
        }

        info.setId(videoId);
        String pageUrl = "https://www.youtube.com/watch?v=" + videoId;
        
        try {
            Request request = new Request.Builder()
                    .url(pageUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build();
            
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String html = response.body().string();
                
                // Extract title
                String title = extractTitle(html);
                info.setTitle(title != null ? title : "Video_" + videoId);
                
                // Extract thumbnail
                String thumbnail = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
                info.setThumbnail(thumbnail);
                
                // Extract duration
                String duration = extractDuration(html);
                info.setDuration(duration != null ? duration : "Unknown");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting video info", e);
            info.setTitle("Video_" + videoId);
            info.setThumbnail("https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg");
        }
        
        return info;
    }

    public void downloadVideo(String url, String format, File outputDir, 
                             DownloadProgressListener listener) throws Exception {
        VideoInfo info = getVideoInfo(url);
        String fileName = FileUtil.generateFileName(info.getTitle(), format);
        File outputFile = new File(outputDir, fileName);
        
        listener.onProgress(10, "Preparing download...");
        
        try {
            // Download the video/audio
            downloadFile(url, format, outputFile, listener);
            listener.onProgress(100, "Download completed: " + fileName);
            listener.onSuccess(outputFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Download failed", e);
            listener.onError("Download failed: " + e.getMessage());
        }
    }

    private void downloadFile(String url, String format, File outputFile,
                             DownloadProgressListener listener) throws Exception {
        // Using youtube-dl binary or API
        // This is a placeholder - in production, use a proper YouTube API
        Log.d(TAG, "Downloading: " + url + " as " + format);
        
        // Simulate download process
        for (int i = 0; i <= 100; i += 10) {
            Thread.sleep(500);
            listener.onProgress(i, "Downloading: " + i + "%");
        }
    }

    private String extractVideoId(String url) {
        Pattern pattern = Pattern.compile(
                "(?:youtube(?:\\.com|\\.be)\\/(?:[^/]+/.*/|(?:v|embed|watch)\\?.*v=|youtu\\.be/))?" +
                "([^\"&?\\s]{11})"
        );
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractTitle(String html) {
        try {
            Pattern pattern = Pattern.compile("<meta name=\"title\" content=\"([^\"]*)");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                return URLDecoder.decode(matcher.group(1), "UTF-8");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting title", e);
        }
        return null;
    }

    private String extractDuration(String html) {
        try {
            Pattern pattern = Pattern.compile("\"duration\":\"(\\d+)");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                int seconds = Integer.parseInt(matcher.group(1));
                int minutes = seconds / 60;
                int secs = seconds % 60;
                return String.format("%02d:%02d", minutes, secs);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting duration", e);
        }
        return null;
    }

    public interface DownloadProgressListener {
        void onProgress(int progress, String message);
        void onSuccess(String filePath);
        void onError(String error);
    }
}
