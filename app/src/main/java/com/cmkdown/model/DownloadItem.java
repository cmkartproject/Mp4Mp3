package com.cmkdown.model;

import java.io.Serializable;

public class DownloadItem implements Serializable {
    private String id;
    private String title;
    private String url;
    private String source; // youtube, facebook, instagram
    private String format; // mp4, mp3
    private String filePath;
    private String fileSize;
    private long timestamp;
    private int status; // 0: pending, 1: downloading, 2: complete, 3: failed
    private String thumbnail;
    private double progress;

    public DownloadItem(String url, String source, String format) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.url = url;
        this.source = source;
        this.format = format;
        this.timestamp = System.currentTimeMillis();
        this.status = 0;
        this.progress = 0.0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }

    public String getStatusString() {
        switch (status) {
            case 0: return "Pending";
            case 1: return "Downloading";
            case 2: return "Complete";
            case 3: return "Failed";
            default: return "Unknown";
        }
    }
}
