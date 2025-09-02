package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.User;
import com.ntn.culinary.request.RegisterRequest;
import com.ntn.culinary.request.UserRequest;
import com.ntn.culinary.response.UserResponse;
import com.ntn.culinary.service.ImageService;
import com.ntn.culinary.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.ntn.culinary.constant.Cloudinary.CLOUDINARY_URL;
import static com.ntn.culinary.utils.StringUtils.slugify;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final ImageService imageService;

    public UserServiceImpl(UserDao userDao, ImageService imageService) {
        this.userDao = userDao;
        this.imageService = imageService;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userDao.getAllUsers();
        return users.stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(int id) {
        User user = userDao.getUserById(id);
        if (user == null) {
            return null;
        }
        return mapUserToResponse(user);
    }

    @Override
    public void register(RegisterRequest request) {
        User user = mapRequestToRegisterRequest(request);
        userDao.addUser(user);
    }

    @Override
    public void updateGeneralUser(UserRequest request) {
        User existingUser = userDao.getUserById(request.getId());

        if (existingUser == null) {
            throw new NotFoundException("User with ID " + request.getId() + " not found.");
        }

        String fileName = null;
        if (request.getAvatar() != null && request.getAvatar().getSize() > 0) {
            // Xóa ảnh cũ nếu có
            if (existingUser.getAvatar() != null) {
                imageService.deleteImage(existingUser.getAvatar(), "avatars");
            }

            // Tạo slug từ tên người dùng
            String slug = slugify(request.getFirstName() + " " + request.getLastName());

            // Lưu ảnh và cập nhật tên file
            fileName = imageService.uploadImage(request.getAvatar(), slug, "avatars");
        }

        userDao.updateUser(mapRequestToUser(request), fileName);
    }

    @Override
    public void deleteUser(int id) {
        User existingUser = userDao.getUserById(id);

        if (existingUser == null) {
            throw new NotFoundException("User with ID " + id + " not found.");
        }

        userDao.deleteUser(id);
    }

    @Override
    public void toggleUserStatus(int id) {

        User existingUser = userDao.getUserById(id);

        if (existingUser == null) {
            throw new NotFoundException("User with ID " + id + " not found.");
        }

        userDao.toggleUserActiveStatus(id);
    }

    @Override
    public boolean isSubscriptionValid(int id) {
        return userDao.isSubscriptionValid(id);
    }

    private User mapRequestToUser(UserRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setLocation(request.getLocation());
        user.setSchool(request.getSchool());
        user.setHighlights(request.getHighlights());
        user.setExperience(request.getExperience());
        user.setEducation(request.getEducation());
        user.setSocialLinks(String.join("|", request.getSocialLinks()));
        return user;
    }

    private User mapRequestToRegisterRequest(RegisterRequest request) {
        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return user;
    }

    private UserResponse mapUserToResponse(User user) {
        String imageUrl = CLOUDINARY_URL + "avatars/";

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhone(user.getPhone());
        response.setCreatedAt(user.getCreatedAt());
        response.setActive(user.isActive());
        response.setAvatar(user.getAvatar() != null ? imageUrl + user.getAvatar() : null);
        response.setLocation(user.getLocation());
        response.setSchool(user.getSchool());
        response.setHighlights(user.getHighlights());
        response.setExperience(user.getExperience());
        response.setEducation(user.getEducation());

        // Xử lý socialLinks thành List<String>
        String socialLinksStr = user.getSocialLinks();
        if (socialLinksStr != null && !socialLinksStr.trim().isEmpty()) {
            List<String> socialLinksList = Arrays.stream(socialLinksStr.split("\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            response.setSocialLinks(socialLinksList);
        } else {
            response.setSocialLinks(Collections.emptyList());
        }

        // Thiết lập roles và permissions
        response.setRoles(user.getRoles());
        response.setPermissions(user.getPermissions());

        return response;
    }
}
