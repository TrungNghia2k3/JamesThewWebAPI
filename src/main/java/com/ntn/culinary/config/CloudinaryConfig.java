package com.ntn.culinary.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryConfig {

    private static Cloudinary cloudinaryInstance;

    public static synchronized Cloudinary getCloudinary() {
        if (cloudinaryInstance == null) {
            String cloudName = System.getenv("CLOUDINARY_CLOUD_NAME");
            String apiKey = System.getenv("CLOUDINARY_API_KEY");
            String apiSecret = System.getenv("CLOUDINARY_API_SECRET");

            if (cloudName == null || apiKey == null || apiSecret == null) {
                throw new RuntimeException("Cloudinary environment variables (CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET) are not set and fallback values are also missing.");
            }

            cloudinaryInstance = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", true // Always use HTTPS
            ));
        }
        return cloudinaryInstance;
    }
}
