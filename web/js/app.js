// Download history data
const downloadHistory = JSON.parse(localStorage.getItem('cmkdown_history')) || [];

// Platform detection
const getPlatform = (url) => {
    if (url.includes('youtube.com') || url.includes('youtu.be')) return 'YouTube';
    if (url.includes('facebook.com')) return 'Facebook';
    if (url.includes('instagram.com')) return 'Instagram';
    return 'Unknown';
};

// Extract video ID
const getVideoId = (url) => {
    const youtubeRegex = /(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([^&\n?#]+)/;
    const match = url.match(youtubeRegex);
    return match ? match[1] : null;
};

// Get thumbnail for YouTube
const getThumbnail = (platform, videoId) => {
    if (platform === 'YouTube' && videoId) {
        return `https://img.youtube.com/vi/${videoId}/maxresdefault.jpg`;
    }
    return 'https://via.placeholder.com/400x300?text=Video+Thumbnail';
};

// Simulate download
const simulateDownload = (url, format, callback) => {
    let progress = 0;
    const interval = setInterval(() => {
        progress += Math.random() * 30;
        if (progress > 100) progress = 100;
        
        callback({
            progress: Math.floor(progress),
            status: progress < 100 ? 'Downloading...' : 'Download Complete!'
        });

        if (progress >= 100) {
            clearInterval(interval);
        }
    }, 500);
};

// Format title for display
const formatTitle = (url) => {
    const platform = getPlatform(url);
    const timestamp = new Date().toLocaleTimeString();
    return `${platform} Video - ${timestamp}`;
};

// Tab switching
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const tabName = btn.dataset.tab;
        
        // Hide all tabs
        document.querySelectorAll('.tab-content').forEach(tab => {
            tab.classList.remove('active');
        });
        
        // Remove active class from all buttons
        document.querySelectorAll('.tab-btn').forEach(b => {
            b.classList.remove('active');
        });
        
        // Show selected tab
        document.getElementById(tabName).classList.add('active');
        btn.classList.add('active');
        
        // Load history if history tab
        if (tabName === 'history') {
            loadHistory();
        }
    });
});

// Download button handler
document.getElementById('downloadBtn').addEventListener('click', () => {
    const url = document.getElementById('urlInput').value.trim();
    const format = document.querySelector('input[name="format"]:checked').value;
    
    if (!url) {
        alert('Please enter a URL');
        return;
    }
    
    const platform = getPlatform(url);
    if (platform === 'Unknown') {
        alert('Invalid URL. Please use YouTube, Facebook, or Instagram link.');
        return;
    }
    
    // Show video info
    const videoInfo = document.getElementById('videoInfo');
    videoInfo.classList.remove('hidden');
    
    // Get video details
    const videoId = getVideoId(url);
    const thumbnail = getThumbnail(platform, videoId);
    const title = formatTitle(url);
    
    // Update video info display
    document.getElementById('videoTitle').textContent = title;
    document.getElementById('videoSource').textContent = `Source: ${platform}`;
    document.getElementById('thumbnail').src = thumbnail;
    document.getElementById('statusText').textContent = 'Starting download...';
    document.getElementById('progressFill').style.width = '0%';
    document.getElementById('progressText').textContent = '0%';
    
    // Disable button
    const btn = document.getElementById('downloadBtn');
    btn.disabled = true;
    
    // Simulate download
    simulateDownload(url, format, (data) => {
        document.getElementById('progressFill').style.width = data.progress + '%';
        document.getElementById('progressText').textContent = data.progress + '%';
        document.getElementById('statusText').textContent = data.status;
        
        if (data.progress >= 100) {
            btn.disabled = false;
            
            // Add to history
            const historyItem = {
                id: Date.now(),
                title: title,
                platform: platform,
                format: format,
                url: url,
                timestamp: new Date().toLocaleString(),
                status: 'Complete'
            };
            
            downloadHistory.unshift(historyItem);
            localStorage.setItem('cmkdown_history', JSON.stringify(downloadHistory));
            
            // Show success message
            setTimeout(() => {
                alert(`✅ Downloaded as ${format.toUpperCase()}!\n\nIn the Android app, files are saved to: Downloads/CMKDOWN/`);
            }, 1000);
        }
    });
});

// Load and display history
const loadHistory = () => {
    const historyList = document.getElementById('historyList');
    
    if (downloadHistory.length === 0) {
        historyList.innerHTML = '<p class="empty-message">No downloads yet. Start downloading to see history.</p>';
        return;
    }
    
    historyList.innerHTML = downloadHistory.map(item => `
        <div class="history-item">
            <div class="history-item-title">📹 ${item.title}</div>
            <div class="history-item-info">
                <span><strong>Platform:</strong> ${item.platform}</span>
                <span><strong>Format:</strong> <span class="history-item-badge">${item.format.toUpperCase()}</span></span>
                <span><strong>Status:</strong> ${item.status}</span>
                <span><strong>Time:</strong> ${item.timestamp}</span>
            </div>
        </div>
    `).join('');
};

// Initialize
window.addEventListener('load', () => {
    console.log('CMKDOWN Web App Loaded');
    console.log(`Total downloads: ${downloadHistory.length}`);
});
