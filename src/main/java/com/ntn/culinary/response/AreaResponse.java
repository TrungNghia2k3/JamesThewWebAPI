package com.ntn.culinary.response;

public class AreaResponse {
    private int id;
    private String name;

    public AreaResponse() {
    }

    public AreaResponse(int id, String name) {
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

    @Override
    public String toString() {
        return "AreaResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
