package com.example.g.filesys;

public class Fileit {
    private String name;
    private int imageId;
    private String path;
    private int typeId;

    public Fileit(String name, int imageId , String path){
        this.name = name;
        this.imageId = imageId;
        this.path = path;
    }
    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

    public String getPath() {
        return path;
    }
    public void setImageId(int i){
        this.imageId = imageId;
    }

}