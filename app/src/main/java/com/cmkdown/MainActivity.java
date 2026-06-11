package com.cmkdown;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText urlInput;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.urlInput);
        downloadButton = findViewById(R.id.downloadButton);

        downloadButton.setOnClickListener(v -> handleDownload());
    }

    private void handleDownload() {
        String url = urlInput.getText().toString().trim();

        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate URL
        if (!isValidUrl(url)) {
            Toast.makeText(this, "Invalid URL format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Start download activity
        Intent intent = new Intent(this, DownloadActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private boolean isValidUrl(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be") ||
               url.contains("facebook.com") || url.contains("instagram.com");
    }
}
