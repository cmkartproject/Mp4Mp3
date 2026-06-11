package com.cmkdown.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cmkdown.downloader.FacebookDownloader;
import com.cmkdown.downloader.InstagramDownloader;
import com.cmkdown.downloader.YouTubeDownloader;
import com.cmkdown.model.DownloadItem;
import com.cmkdown.util.DatabaseHelper;
import com.cmkdown.util.DownloadValidator;
import com.cmkdown.util.FileUtil;

import java.io.File;

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";
    private DatabaseHelper dbHelper;
    private YouTubeDownloader youtubeDownloader;
    private FacebookDownloader facebookDownloader;
    private InstagramDownloader instagramDownloader;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
        youtubeDownloader = new YouTubeDownloader();
        facebookDownloader = new FacebookDownloader();
        instagramDownloader = new InstagramDownloader();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String url = intent.getStringExtra("url");
            String format = intent.getStringExtra("format");
            
            if (url != null && format != null) {
                new Thread(() -> downloadContent(url, format)).start();
            }
        }
        return START_STICKY;
    }

    private void downloadContent(String url, String format) {
        try {
            Log.d(TAG, "Starting download: " + url + " as " + format);

            // Create downloads directory
            File downloadsDir = FileUtil.getDownloadsDirectory();

            // Create download item
            String source = DownloadValidator.getSource(url);
            DownloadItem downloadItem = new DownloadItem(url, source, format);
            downloadItem.setStatus(0); // Pending
            dbHelper.addDownload(downloadItem);

            // Download based on source
            if (source.equals("youtube")) {
                downloadFromYouTube(url, format, downloadsDir, downloadItem);
            } else if (source.equals("facebook")) {
                downloadFromFacebook(url, downloadsDir, downloadItem);
            } else if (source.equals("instagram")) {
                downloadFromInstagram(url, downloadsDir, downloadItem);
            } else {
                throw new Exception("Unsupported URL source");
            }

            Log.d(TAG, "Download completed");
            downloadItem.setStatus(2); // Complete
            dbHelper.updateDownloadStatus(downloadItem.getId(), 2);
        } catch (Exception e) {
            Log.e(TAG, "Download error", e);
        }
    }

    private void downloadFromYouTube(String url, String format, File downloadsDir,
                                     DownloadItem downloadItem) throws Exception {
        youtubeDownloader.downloadVideo(url, format, downloadsDir,
                new YouTubeDownloader.DownloadProgressListener() {
                    @Override
                    public void onProgress(int progress, String message) {
                        downloadItem.setProgress(progress);
                        Log.d(TAG, "YouTube Progress: " + progress + "% - " + message);
                    }

                    @Override
                    public void onSuccess(String filePath) {
                        downloadItem.setStatus(2); // Complete
                        downloadItem.setFilePath(filePath);
                        dbHelper.updateDownloadProgress(downloadItem.getId(), filePath,
                                FileUtil.formatFileSize(new File(filePath).length()));
                        Log.d(TAG, "Download successful: " + filePath);
                    }

                    @Override
                    public void onError(String error) {
                        downloadItem.setStatus(3); // Failed
                        dbHelper.updateDownloadStatus(downloadItem.getId(), 3);
                        Log.e(TAG, error);
                    }
                });
    }

    private void downloadFromFacebook(String url, File downloadsDir,
                                      DownloadItem downloadItem) throws Exception {
        facebookDownloader.downloadVideo(url, downloadsDir,
                new FacebookDownloader.DownloadProgressListener() {
                    @Override
                    public void onProgress(int progress, String message) {
                        downloadItem.setProgress(progress);
                        Log.d(TAG, "Facebook Progress: " + progress + "% - " + message);
                    }

                    @Override
                    public void onSuccess(String filePath) {
                        downloadItem.setStatus(2); // Complete
                        downloadItem.setFilePath(filePath);
                        dbHelper.updateDownloadProgress(downloadItem.getId(), filePath,
                                FileUtil.formatFileSize(new File(filePath).length()));
                        Log.d(TAG, "Download successful: " + filePath);
                    }

                    @Override
                    public void onError(String error) {
                        downloadItem.setStatus(3); // Failed
                        dbHelper.updateDownloadStatus(downloadItem.getId(), 3);
                        Log.e(TAG, error);
                    }
                });
    }

    private void downloadFromInstagram(String url, File downloadsDir,
                                       DownloadItem downloadItem) throws Exception {
        instagramDownloader.downloadVideo(url, downloadsDir,
                new InstagramDownloader.DownloadProgressListener() {
                    @Override
                    public void onProgress(int progress, String message) {
                        downloadItem.setProgress(progress);
                        Log.d(TAG, "Instagram Progress: " + progress + "% - " + message);
                    }

                    @Override
                    public void onSuccess(String filePath) {
                        downloadItem.setStatus(2); // Complete
                        downloadItem.setFilePath(filePath);
                        dbHelper.updateDownloadProgress(downloadItem.getId(), filePath,
                                FileUtil.formatFileSize(new File(filePath).length()));
                        Log.d(TAG, "Download successful: " + filePath);
                    }

                    @Override
                    public void onError(String error) {
                        downloadItem.setStatus(3); // Failed
                        dbHelper.updateDownloadStatus(downloadItem.getId(), 3);
                        Log.e(TAG, error);
                    }
                });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
