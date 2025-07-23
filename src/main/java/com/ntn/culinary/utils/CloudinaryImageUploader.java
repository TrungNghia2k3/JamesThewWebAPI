package com.ntn.culinary.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ntn.culinary.config.CloudinaryConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

public class CloudinaryImageUploader {

    // Đặt tên file bạn muốn bắt đầu upload từ đây
    private static final String START_FILE_NAME = "colorful-corn-salad-instruction-2.jpg";
    // Đặt tên thư mục chứa file đó (ví dụ: "instructions")
    private static final String START_FOLDER = "instructions";
    private static boolean foundStartFile = false; // Biến cờ để biết đã tìm thấy file bắt đầu chưa

    private static final Cloudinary cloudinary = CloudinaryConfig.getCloudinary();

    public static void main(String[] args) {
        // Lấy thông tin cấu hình từ biến môi trường

        String cloudName = System.getenv("CLOUDINARY_CLOUD_NAME");
        String apiKey = System.getenv("CLOUDINARY_API_KEY");
        String apiSecret = System.getenv("CLOUDINARY_API_SECRET");

//        String cloudName = "dmbpesu2z";
//        String apiKey = "343896654985164";
//        String apiSecret = "jT7-FdtWEFpJFYnfitdwLVIZnn0";

        System.out.println(cloudName);
        System.out.println(apiKey);
        System.out.println(apiSecret);

//        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
//                "cloud_name", cloudName,
//                "api_key", apiKey,
//                "api_secret", apiSecret,
//                "secure", true
//        ));
//
//        // Đường dẫn tới folder 'images' cục bộ của bạn
//        String baseLocalPath = "E:\\Project\\JamesThewProject\\source-code\\backend\\images";
//        Path startPath = Paths.get(baseLocalPath);
//
//        File baseDir = startPath.toFile();
//        if (!baseDir.exists()) {
//            System.err.println("Error: Directory does not exist: " + baseLocalPath);
//            return;
//        }
//        if (!baseDir.canRead()) {
//            System.err.println("Error: Cannot read from directory: " + baseLocalPath + ". Check permissions.");
//            return;
//        }
//
//        System.out.println("Starting image upload and database update process...");
//        System.out.println("Will skip files until: " + START_FOLDER + "/" + START_FILE_NAME);
//
//        try (Stream<Path> walk = Files.walk(startPath)) {
//            walk.filter(Files::isRegularFile)
//                    // Sắp xếp các file để đảm bảo thứ tự duyệt ổn định (quan trọng nếu bạn dùng cờ skip)
//                    .sorted()
//                    .forEach(file -> {
//                        // Xây dựng đường dẫn tương đối và tên file để so sánh
//                        String relativePathWithExtension = startPath.relativize(file).toString().replace("\\", "/");
//                        String currentFileName = file.getFileName().toString();
//                        String currentFolder = file.getParent().getFileName().toString(); // Lấy tên folder chứa file
//
//                        // Logic bỏ qua
//                        if (!foundStartFile) {
//                            if (currentFolder.equals(START_FOLDER) && currentFileName.equals(START_FILE_NAME)) {
//                                foundStartFile = true; // Đã tìm thấy file bắt đầu, từ giờ sẽ xử lý
//                                System.out.println("Found start file. Starting upload from: " + relativePathWithExtension);
//                            } else {
//                                System.out.println("Skipping: " + relativePathWithExtension);
//                                return; // Bỏ qua file này và chuyển sang file tiếp theo
//                            }
//                        }
//
//                        // Nếu đã tìm thấy file bắt đầu hoặc cờ đã được đặt true, thì xử lý upload
//                        try {
//                            String publicId = relativePathWithExtension.substring(0, relativePathWithExtension.lastIndexOf('.'));
//
//                            // Thêm overwrite: true để đảm bảo ghi đè nếu ảnh đã tồn tại trên Cloudinary
//                            Map uploadResult = cloudinary.uploader().upload(file.toFile(),
//                                    ObjectUtils.asMap("public_id", publicId, "overwrite", true));
//
//                            String imageUrl = (String) uploadResult.get("secure_url");
//                            String uploadedPublicId = (String) uploadResult.get("public_id");
//
//                            System.out.println("Uploaded: " + file.getFileName() + " -> Public ID: " + uploadedPublicId + " -> URL: " + imageUrl);
//
//                            // === THÊM LOGIC CẬP NHẬT DATABASE TẠI ĐÂY ===
//                            // Gọi các hàm cập nhật database tương ứng với mỗi loại ảnh
//                            // if (relativePathWithExtension.startsWith("categories/")) {
//                            //     updateCategoryPublicId(currentFileName, uploadedPublicId, imageUrl);
//                            // } else if (relativePathWithExtension.startsWith("avatars/")) {
//                            //     updateUserAvatarPublicId(currentFileName, uploadedPublicId, imageUrl);
//                            // }
//                            // ... và cứ tiếp tục cho các folder khác
//
//                        } catch (IOException e) {
//                            System.err.println("Failed to upload " + file.getFileName() + ": " + e.getMessage());
//                        } catch (Exception e) { // Bắt SQLException nếu bạn đã thêm logic DB
//                            System.err.println("An error occurred during upload or DB update for " + file.getFileName() + ": " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    });
//        } catch (IOException e) {
//            System.err.println("Error walking through directory: " + baseLocalPath + " - " + e.getMessage());
//            e.printStackTrace();
//        }
//        System.out.println("Image upload and database update process completed.");
    }

    // Các hàm cập nhật database (giữ nguyên hoặc sửa đổi như đã hướng dẫn)
    // private static void updateCategoryPublicId(String originalFileName, String newPublicId, String newImageUrl) throws SQLException { ... }
    // private static void updateUserAvatarPublicId(String originalFileName, String newPublicId, String newImageUrl) throws SQLException { ... }
    // ...
}
