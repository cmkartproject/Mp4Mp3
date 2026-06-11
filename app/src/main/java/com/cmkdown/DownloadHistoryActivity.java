package com.cmkdown;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmkdown.adapter.DownloadHistoryAdapter;
import com.cmkdown.model.DownloadItem;
import com.cmkdown.util.DatabaseHelper;

import java.util.List;

public class DownloadHistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private DatabaseHelper dbHelper;
    private DownloadHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_history);

        historyListView = findViewById(R.id.historyListView);
        dbHelper = new DatabaseHelper(this);

        loadDownloadHistory();
    }

    private void loadDownloadHistory() {
        List<DownloadItem> downloads = dbHelper.getAllDownloads();
        adapter = new DownloadHistoryAdapter(this, downloads);
        historyListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDownloadHistory();
    }
}
