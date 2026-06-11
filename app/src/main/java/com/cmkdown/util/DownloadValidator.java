package com.cmkdown.util;

import java.util.regex.Pattern;

public class DownloadValidator {

    private static final Pattern YOUTUBE_URL = Pattern.compile(
            "(?:https?://)?(?:www\\.)?(?:youtube|youtu|youtube-nocookie)\\.(?:com|be)/?(.+)"
    );

    private static final Pattern FACEBOOK_URL = Pattern.compile(
            "(?:https?://)?(?:www\\.)?facebook\\.com/(.+)"
    );

    private static final Pattern INSTAGRAM_URL = Pattern.compile(
            "(?:https?://)?(?:www\\.)?instagram\\.com/(.+)"
    );

    public static boolean isYouTubeUrl(String url) {
        return YOUTUBE_URL.matcher(url).find();
    }

    public static boolean isFacebookUrl(String url) {
        return FACEBOOK_URL.matcher(url).find();
    }

    public static boolean isInstagramUrl(String url) {
        return INSTAGRAM_URL.matcher(url).find();
    }

    public static String getSource(String url) {
        if (isYouTubeUrl(url)) return "youtube";
        if (isFacebookUrl(url)) return "facebook";
        if (isInstagramUrl(url)) return "instagram";
        return "unknown";
    }
}
