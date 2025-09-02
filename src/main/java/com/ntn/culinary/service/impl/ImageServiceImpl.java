package com.ntn.culinary.service.impl;

import com.ntn.culinary.service.ImageService;
import com.ntn.culinary.utils.CloudinaryImageUtils;

import javax.servlet.http.Part;

public class ImageServiceImpl implements ImageService {

    @Override
    public String uploadImage(Part imagePart, String baseFileName, String type) {
        return CloudinaryImageUtils.uploadImage(imagePart, baseFileName, type);
    }

    @Override
    public void deleteImage(String filename, String type) {
        CloudinaryImageUtils.deleteImage(filename, type);
    }
}
