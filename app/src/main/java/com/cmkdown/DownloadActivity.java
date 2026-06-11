package com.cmkdown;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmkdown.service.DownloadService;

public class DownloadActivity extends AppCompatActivity {

    private String url;
    private ProgressBar progressBar;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);

        url = getIntent().getStringExtra("url");

        if (url != null) {
            startDownload();
        }
    }

    private void startDownload() {
        Intent serviceIntent = new Intent(this, DownloadService.class);
        serviceIntent.putExtra("url", url);
        startService(serviceIntent);

        statusText.setText("Starting download...");
        progressBar.setIndeterminate(true);
    }
}
