package com.ntn.culinary.request;

import com.google.gson.annotations.Expose;

public class AreaRequest {
    private int id;
    private String name;

    public AreaRequest() {
    }

    public AreaRequest(int id, String name) {
        this.id = id;
        this.name = name;
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
}
