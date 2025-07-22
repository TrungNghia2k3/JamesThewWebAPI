package com.ntn.culinary.model;

import java.util.Objects;

public class Area {
    private int id;
    private String name;

    public Area() {}

    public Area(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Area area)) return false;
        return id == area.id && Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Area{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
