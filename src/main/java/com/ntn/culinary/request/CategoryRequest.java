package com.ntn.culinary.request;

import javax.servlet.http.Part;

public class CategoryRequest {
    private int id;
    private String name;
    private Part image;

    public CategoryRequest() {
    }

    public CategoryRequest(int id, String name, Part image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        this.image = image;
    }
}
