package com.tanzeem.image.enums;

public enum ContentCategory {
    IMAGE("Images", "image/"),
    DOCUMENT("Documents", "application/"),
    VIDEO("Videos", "video/"),
    AUDIO("Audio", "audio/"),
    ARCHIVE("Archives", "application/"),
    TEXT("Text Files", "text/"),
    OTHER("Other", "");

    private final String displayName;
    private final String mimeTypePrefix;

    ContentCategory(String displayName, String mimeTypePrefix) {
        this.displayName = displayName;
        this.mimeTypePrefix = mimeTypePrefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMimeTypePrefix() {
        return mimeTypePrefix;
    }

    public static ContentCategory fromMimeType(String mimeType) {
        if (mimeType == null) return OTHER;

        String lowerMimeType = mimeType.toLowerCase();

        if (lowerMimeType.startsWith("image/")) {
            return IMAGE;
        } else if (lowerMimeType.startsWith("video/")) {
            return VIDEO;
        } else if (lowerMimeType.startsWith("audio/")) {
            return AUDIO;
        } else if (lowerMimeType.startsWith("text/")) {
            return TEXT;
        } else if (lowerMimeType.startsWith("application/")) {
            // Further categorize application types
            if (lowerMimeType.contains("pdf") ||
                    lowerMimeType.contains("word") ||
                    lowerMimeType.contains("excel") ||
                    lowerMimeType.contains("powerpoint") ||
                    lowerMimeType.contains("officedocument")) {
                return DOCUMENT;
            } else if (lowerMimeType.contains("zip") ||
                    lowerMimeType.contains("rar") ||
                    lowerMimeType.contains("7z") ||
                    lowerMimeType.contains("tar")) {
                return ARCHIVE;
            } else {
                return DOCUMENT; // Default for application types
            }
        }

        return OTHER;
    }
}
