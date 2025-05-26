package com.tanzeem.image.dto;

public class ImageUrlRequest {
    private String imageUrl;
    private String context = "general";

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
}
