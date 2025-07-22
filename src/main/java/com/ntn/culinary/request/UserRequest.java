package com.ntn.culinary.request;

import com.google.gson.annotations.Expose;

import javax.servlet.http.Part;
import java.sql.Timestamp;
import java.util.Date;

public class UserRequest {
    @Expose
    private int id;

    @Expose
    private String password;

    @Expose
    private String email;

    @Expose
    private String firstName;

    @Expose
    private String lastName;

    @Expose
    private String phone;

    @Expose
    private Part avatar;

    @Expose
    private String location;

    @Expose
    private String school;

    @Expose
    private String highlights;

    @Expose
    private String experience;

    @Expose
    private String education;

    @Expose
    private String socialLinks;

    public UserRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Part getAvatar() {
        return avatar;
    }

    public void setAvatar(Part avatar) {
        this.avatar = avatar;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(String socialLinks) {
        this.socialLinks = socialLinks;
    }
}
