package com.ntn.culinary.service;

import javax.servlet.http.Part;

public interface ImageService {
    String uploadImage(Part imagePart, String baseFileName, String type);
    void deleteImage(String filename, String type);
}
