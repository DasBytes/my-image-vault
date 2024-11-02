package com.example.data_app;


public class ImageData {
    private String imageName;
    private String imageUrl;

    public ImageData(String imageName, String imageUrl) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
