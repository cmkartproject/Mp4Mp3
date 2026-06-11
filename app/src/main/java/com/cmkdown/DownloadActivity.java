package com.cmkdown;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmkdown.service.DownloadService;

public class DownloadActivity extends AppCompatActivity {

    private String url;
    private String format;
    private ProgressBar progressBar;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);

        url = getIntent().getStringExtra("url");
        format = getIntent().getStringExtra("format");

        if (url != null && format != null) {
            startDownload();
        } else {
            Toast.makeText(this, "Error: Missing URL or format", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startDownload() {
        Intent serviceIntent = new Intent(this, DownloadService.class);
        serviceIntent.putExtra("url", url);
        serviceIntent.putExtra("format", format);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        statusText.setText("Starting " + format.toUpperCase() + " download...");
        progressBar.setIndeterminate(true);
    }
}
