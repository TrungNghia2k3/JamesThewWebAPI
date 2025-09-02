package com.ntn.culinary.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ntn.culinary.config.CloudinaryConfig;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

import static com.ntn.culinary.utils.StringUtils.slugify;

public class CloudinaryImageUtils {

    // Không còn cần IMAGE_DIRECTORY cục bộ nữa!
    private static Cloudinary cloudinary; // Lazy initialization thay vì static final

    /**
     * Lấy Cloudinary instance (lazy initialization)
     */
    private static synchronized Cloudinary getCloudinary() {
        if (cloudinary == null) {
            cloudinary = CloudinaryConfig.getCloudinary();
        }
        return cloudinary;
    }

    /**
     * Upload ảnh lên Cloudinary.
     *
     * @param imagePart    Part lấy từ request.getPart("image")
     * @param baseFileName Chuỗi base để tạo public_id (vd: slug của tên recipe).
     *                     Có thể null, trong trường hợp đó sẽ dùng UUID.
     * @param type         Thư mục ảo trên Cloudinary (vd: "recipes", "avatars", "categories").
     * @return public_id của ảnh đã upload trên Cloudinary (vd: "recipes/my-recipe-image-123")
     * Trả về null nếu không có file hoặc lỗi.
     */
    public static String uploadImage(Part imagePart, String baseFileName, String type) {
        if (imagePart == null || imagePart.getSize() == 0) {
            return null;
        }

        try (InputStream inputStream = imagePart.getInputStream()) {
            // Cloudinary có thể upload trực tiếp từ InputStream
            // Tuy nhiên, việc tạo File tạm thời sẽ giúp Cloudinary xử lý tốt hơn
            // cho các file lớn và đôi khi Stream có thể không seekable.
            // Nên tạo một file tạm thời để upload.

            File tempFile = null;
            String publicId = null;

            try {
                // Tạo file tạm thời để Cloudinary upload
                String submittedFileName = imagePart.getSubmittedFileName();
                String ext = "";
                int i = submittedFileName.lastIndexOf('.');
                if (i > 0) {
                    ext = submittedFileName.substring(i);
                }

                String uniqueBaseName = (baseFileName != null && !baseFileName.isEmpty())
                        ? slugify(baseFileName) + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8)
                        : UUID.randomUUID().toString(); // Fallback nếu baseFileName trống

                // Tạo publicId dạng: "type/unique-base-name"
                publicId = type + "/" + uniqueBaseName;

                // Tạo file tạm thời với tên duy nhất để tránh collision
                tempFile = File.createTempFile("upload-" + UUID.randomUUID().toString().substring(0, 8) + "-", ext);

                // Copy InputStream vào file tạm với REPLACE_EXISTING để tránh FileAlreadyExistsException
                Files.copy(inputStream, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                Map uploadResult = getCloudinary().uploader().upload(tempFile,
                        ObjectUtils.asMap(
                                "public_id", publicId,
                                "overwrite", true, // Ghi đè nếu public_id đã tồn tại
                                "resource_type", "image" // Đảm bảo upload đúng loại tài nguyên
                        ));

                String uploadedPublicId = (String) uploadResult.get("public_id");
                // String secureUrl = (String) uploadResult.get("secure_url"); // Nếu bạn muốn trả về URL đầy đủ

                System.out.println("Uploaded to Cloudinary: " + uploadedPublicId);
                return uploadedPublicId; // Trả về public_id để lưu vào DB

            } finally {
                // Đảm bảo file tạm được xóa sau khi upload
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to upload image to Cloudinary: " + e.getMessage());
            return null;
        } catch (Exception e) { // Bắt các exception khác từ Cloudinary SDK
            System.err.println("An error occurred during Cloudinary upload: " + e.getMessage());
            return null;
        }
    }

    /**
     * Xóa ảnh đã upload từ Cloudinary.
     *
     * @param filename Tên file cần xóa (vd: "apple-frangipan-1689012345678.jpg").
     *                 Thực tế là public_id trên Cloudinary.
     * @param type     Thư mục ảo trên Cloudinary (vd: "recipes", "avatars", "categories").
     */
    public static void deleteImage(String filename, String type) {

        String publicId = type + "/" + filename; // publicId trên Cloudinary (vd: "recipes/apple-frangipan-1689012345678")

        try {
            Map deleteResult = getCloudinary().uploader().destroy(publicId, ObjectUtils.emptyMap());
            String result = (String) deleteResult.get("result");
            if ("ok".equals(result)) {
                System.out.println("Deleted from Cloudinary: " + publicId);
            } else {
                System.err.println("Cloudinary delete failed for " + publicId + ": " + deleteResult);

            }
        } catch (IOException e) {
            System.err.println("Error deleting image from Cloudinary " + publicId + ": " + e.getMessage());
        }
    }
}
