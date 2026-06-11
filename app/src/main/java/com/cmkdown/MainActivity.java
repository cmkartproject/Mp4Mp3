package com.cmkdown;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmkdown.util.DownloadValidator;

public class MainActivity extends AppCompatActivity {

    private EditText urlInput;
    private RadioGroup formatGroup;
    private Button downloadButton;
    private Button historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.urlInput);
        formatGroup = findViewById(R.id.formatGroup);
        downloadButton = findViewById(R.id.downloadButton);
        historyButton = findViewById(R.id.historyButton);

        downloadButton.setOnClickListener(v -> handleDownload());
        historyButton.setOnClickListener(v -> openHistory());
    }

    private void handleDownload() {
        String url = urlInput.getText().toString().trim();
        int selectedFormat = formatGroup.getCheckedRadioButtonId();

        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate URL
        String source = DownloadValidator.getSource(url);
        if (source.equals("unknown")) {
            Toast.makeText(this, "Invalid URL. Please use YouTube, Facebook, or Instagram link",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFormat == -1) {
            Toast.makeText(this, "Please select a format", Toast.LENGTH_SHORT).show();
            return;
        }

        String format = getSelectedFormat(selectedFormat);

        // Start download activity
        Intent intent = new Intent(this, DownloadActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("format", format);
        startActivity(intent);
    }

    private String getSelectedFormat(int id) {
        RadioButton radioButton = findViewById(id);
        String text = radioButton.getText().toString();
        return text.toLowerCase().contains("mp3") ? "mp3" : "mp4";
    }

    private void openHistory() {
        Intent intent = new Intent(this, DownloadHistoryActivity.class);
        startActivity(intent);
    }
}
