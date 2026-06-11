package com.cmkdown.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmkdown.R;
import com.cmkdown.model.DownloadItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DownloadHistoryAdapter extends ArrayAdapter<DownloadItem> {
    private final LayoutInflater inflater;

    public DownloadHistoryAdapter(Context context, List<DownloadItem> downloads) {
        super(context, 0, downloads);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.download_history_item, parent, false);
        }

        DownloadItem item = getItem(position);
        if (item == null) return convertView;

        TextView titleText = convertView.findViewById(R.id.titleText);
        TextView sourceText = convertView.findViewById(R.id.sourceText);
        TextView statusText = convertView.findViewById(R.id.statusText);
        TextView sizeText = convertView.findViewById(R.id.sizeText);
        TextView timeText = convertView.findViewById(R.id.timeText);
        ProgressBar progressBar = convertView.findViewById(R.id.itemProgressBar);
        ImageView statusIcon = convertView.findViewById(R.id.statusIcon);

        // Set title
        titleText.setText(item.getTitle() != null ? item.getTitle() : "Unknown");

        // Set source
        sourceText.setText("From: " + item.getSource().toUpperCase() + " (" + item.getFormat().toUpperCase() + ")");

        // Set status
        statusText.setText(item.getStatusString());
        int statusColor = getStatusColor(item.getStatus());
        statusText.setTextColor(statusColor);

        // Set file size
        sizeText.setText(item.getFileSize() != null ? item.getFileSize() : "Unknown size");

        // Set time
        String timeString = formatTime(item.getTimestamp());
        timeText.setText(timeString);

        // Set progress bar visibility
        if (item.getStatus() == 1) { // Downloading
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress((int) item.getProgress());
        } else {
            progressBar.setVisibility(View.GONE);
        }

        // Set status icon
        switch (item.getStatus()) {
            case 2: // Complete
                statusIcon.setImageResource(android.R.drawable.ic_menu_info_details);
                break;
            case 3: // Failed
                statusIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                break;
            case 1: // Downloading
                statusIcon.setImageResource(android.R.drawable.ic_dialog_info);
                break;
            default:
                statusIcon.setImageResource(android.R.drawable.ic_menu_view);
        }

        return convertView;
    }

    private int getStatusColor(int status) {
        switch (status) {
            case 2: return 0xFF4CAF50; // Green - Complete
            case 3: return 0xFFF44336; // Red - Failed
            case 1: return 0xFF2196F3; // Blue - Downloading
            default: return 0xFF9E9E9E; // Gray - Pending
        }
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
