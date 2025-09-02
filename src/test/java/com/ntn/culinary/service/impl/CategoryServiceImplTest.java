package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.CategoryDao;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.Category;
import com.ntn.culinary.request.CategoryRequest;
import com.ntn.culinary.service.ImageService;
import com.ntn.culinary.utils.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Part;

import static com.ntn.culinary.utils.StringUtils.slugify;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private ImageService imageService;

    @Mock
    private Part mockImagePart;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    // Helper methods for test data
    private CategoryRequest createValidCategoryRequest() {
        return new CategoryRequest(0, "Test Category", null);
    }

    private CategoryRequest createCategoryRequestWithImage() {
        return new CategoryRequest(0, "Test Category", mockImagePart);
    }

    private CategoryRequest createCategoryRequestForUpdate() {
        return new CategoryRequest(1, "Updated Category", null);
    }

    private CategoryRequest createCategoryRequestForUpdateWithImage() {
        return new CategoryRequest(1, "Updated Category", mockImagePart);
    }

    private Category createExistingCategory() {
        Category category = new Category();
        category.setId(1);
        category.setName("Existing Category");
        category.setPath("existing-category.jpg");
        return category;
    }

    private Category createExistingCategoryWithoutImage() {
        Category category = new Category();
        category.setId(1);
        category.setName("Existing Category");
        category.setPath(null);
        return category;
    }

    // TEST ADD CATEGORY
    @Test
    @DisplayName("Add category with valid request without image should insert category successfully")
    void testAddCategory_WithValidRequestWithoutImage_ShouldInsertCategorySuccessfully() {
        // Arrange
        CategoryRequest request = createValidCategoryRequest();
        when(categoryDao.existsByName("Test Category")).thenReturn(false);

        // Act
        categoryService.addCategory(request);

        // Assert
        verify(categoryDao).existsByName("Test Category");

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryDao).insertCategory(captor.capture());

        Category insertedCategory = captor.getValue();
        assertEquals("Test Category", insertedCategory.getName());
        assertNull(insertedCategory.getPath()); // No image provided
    }

    @Test
    @DisplayName("Add category with valid request and image should insert category with image path")
    void testAddCategory_WithValidRequestAndImage_ShouldInsertCategoryWithImagePath() {
        // Arrange
        CategoryRequest request = createCategoryRequestWithImage();
        when(categoryDao.existsByName("Test Category")).thenReturn(false);
        when(mockImagePart.getSize()).thenReturn(1024L); // Non-zero size
        when(imageService.uploadImage(mockImagePart, "test-category", "categories")).thenReturn("test-category-123.jpg");

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify("Test Category")).thenReturn("test-category");

            // Act
            categoryService.addCategory(request);

            // Assert
            verify(categoryDao).existsByName("Test Category");
            verify(imageService).uploadImage(mockImagePart, "test-category", "categories");

            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
            verify(categoryDao).insertCategory(captor.capture());

            Category insertedCategory = captor.getValue();
            assertEquals("Test Category", insertedCategory.getName());
            assertEquals("test-category-123.jpg", insertedCategory.getPath());

            stringUtils.verify(() -> slugify("Test Category"));
        }
    }

    @Test
    @DisplayName("Add category with image having zero size should not upload image")
    void testAddCategory_WithImageHavingZeroSize_ShouldNotUploadImage() {
        // Arrange
        CategoryRequest request = createCategoryRequestWithImage();
        when(categoryDao.existsByName("Test Category")).thenReturn(false);
        when(mockImagePart.getSize()).thenReturn(0L); // Zero size

        // Act
        categoryService.addCategory(request);

        // Assert
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryDao).insertCategory(captor.capture());

        Category insertedCategory = captor.getValue();
        assertEquals("Test Category", insertedCategory.getName());
        assertNull(insertedCategory.getPath()); // No image saved due to zero size

        // Verify image service was never called
        verify(imageService, never()).uploadImage(any(), any(), any());
    }

    @Test
    @DisplayName("Add category when name already exists should throw ConflictException")
    void testAddCategory_WhenNameAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        CategoryRequest request = createValidCategoryRequest();
        when(categoryDao.existsByName("Test Category")).thenReturn(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
            () -> categoryService.addCategory(request));

        assertEquals("Category with name already exists", exception.getMessage());
        verify(categoryDao).existsByName("Test Category");
        verify(categoryDao, never()).insertCategory(any());
    }

    @Test
    @DisplayName("Add category with special characters in name should create proper slug")
    void testAddCategory_WithSpecialCharactersInName_ShouldCreateProperSlug() {
        // Arrange
        CategoryRequest request = new CategoryRequest(0, "Test & Category #1", mockImagePart);
        when(categoryDao.existsByName("Test & Category #1")).thenReturn(false);
        when(mockImagePart.getSize()).thenReturn(1024L);
        when(imageService.uploadImage(mockImagePart, "test-category-1", "categories")).thenReturn("test-category-1-456.jpg");

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify("Test & Category #1")).thenReturn("test-category-1");

            // Act
            categoryService.addCategory(request);

            // Assert
            stringUtils.verify(() -> slugify("Test & Category #1"));
            verify(imageService).uploadImage(mockImagePart, "test-category-1", "categories");
        }
    }

    @Test
    @DisplayName("Add category when image upload fails should propagate exception")
    void testAddCategory_WhenImageUploadFails_ShouldPropagateException() {
        // Arrange
        CategoryRequest request = createCategoryRequestWithImage();
        when(categoryDao.existsByName("Test Category")).thenReturn(false);
        when(mockImagePart.getSize()).thenReturn(1024L);
        when(imageService.uploadImage(mockImagePart, "test-category", "categories"))
            .thenThrow(new RuntimeException("Cloudinary upload failed"));

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify("Test Category")).thenReturn("test-category");

            // Act & Assert
            assertThrows(RuntimeException.class, () -> categoryService.addCategory(request));

            verify(categoryDao).existsByName("Test Category");
            verify(imageService).uploadImage(mockImagePart, "test-category", "categories");
            verify(categoryDao, never()).insertCategory(any());
        }
    }

    // TEST UPDATE CATEGORY
    @Test
    @DisplayName("Update category with valid request without image should update category successfully")
    void testUpdateCategory_WithValidRequestWithoutImage_ShouldUpdateCategorySuccessfully() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdate();
        Category existingCategory = createExistingCategory();

        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.existsCategoryWithNameExcludingId(1, "Updated Category")).thenReturn(false);
        when(categoryDao.getCategoryById(1)).thenReturn(existingCategory);

        // Act
        categoryService.updateCategory(request);

        // Assert
        verify(categoryDao).existsById(1);
        verify(categoryDao).existsCategoryWithNameExcludingId(1, "Updated Category");
        verify(categoryDao).getCategoryById(1);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryDao).updateCategory(captor.capture());

        Category updatedCategory = captor.getValue();
        assertEquals(1, updatedCategory.getId());
        assertEquals("Updated Category", updatedCategory.getName());
        assertNull(updatedCategory.getPath()); // No new image provided
    }

    @Test
    @DisplayName("Update category with image should delete old image and upload new one")
    void testUpdateCategory_WithImage_ShouldDeleteOldImageAndUploadNewOne() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdateWithImage();
        Category existingCategory = createExistingCategory();

        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.existsCategoryWithNameExcludingId(1, "Updated Category")).thenReturn(false);
        when(categoryDao.getCategoryById(1)).thenReturn(existingCategory);
        when(mockImagePart.getSize()).thenReturn(1024L);
        when(imageService.uploadImage(mockImagePart, "updated-category", "categories")).thenReturn("updated-category-789.jpg");

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify("Updated Category")).thenReturn("updated-category");

            // Act
            categoryService.updateCategory(request);

            // Assert
            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
            verify(categoryDao).updateCategory(captor.capture());

            Category updatedCategory = captor.getValue();
            assertEquals(1, updatedCategory.getId());
            assertEquals("Updated Category", updatedCategory.getName());
            assertEquals("updated-category-789.jpg", updatedCategory.getPath());

            // Verify old image was deleted and new image was uploaded
            verify(imageService).deleteImage("existing-category.jpg", "categories");
            verify(imageService).uploadImage(mockImagePart, "updated-category", "categories");
        }
    }

    @Test
    @DisplayName("Update category without existing image should not attempt to delete image")
    void testUpdateCategory_WithoutExistingImage_ShouldNotAttemptToDeleteImage() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdateWithImage();
        Category existingCategory = createExistingCategoryWithoutImage();

        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.existsCategoryWithNameExcludingId(1, "Updated Category")).thenReturn(false);
        when(categoryDao.getCategoryById(1)).thenReturn(existingCategory);
        when(mockImagePart.getSize()).thenReturn(1024L);
        when(imageService.uploadImage(mockImagePart, "updated-category", "categories")).thenReturn("updated-category-789.jpg");

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify("Updated Category")).thenReturn("updated-category");

            // Act
            categoryService.updateCategory(request);

            // Assert
            // Verify no delete operation was attempted since existing category had no image
            verify(imageService, never()).deleteImage(anyString(), anyString());
            verify(imageService).uploadImage(mockImagePart, "updated-category", "categories");
        }
    }

    @Test
    @DisplayName("Update category when ID does not exist should throw NotFoundException")
    void testUpdateCategory_WhenIdDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdate();
        when(categoryDao.existsById(1)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> categoryService.updateCategory(request));

        assertEquals("Category with id not found", exception.getMessage());
        verify(categoryDao).existsById(1);
        verify(categoryDao, never()).getCategoryById(anyInt());
        verify(categoryDao, never()).updateCategory(any());
    }

    @Test
    @DisplayName("Update category when name exists for another category should throw ConflictException")
    void testUpdateCategory_WhenNameExistsForAnotherCategory_ShouldThrowConflictException() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdate();
        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.existsCategoryWithNameExcludingId(1, "Updated Category")).thenReturn(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
            () -> categoryService.updateCategory(request));

        assertEquals("Category with name already exists, excluding the current one", exception.getMessage());
        verify(categoryDao).existsById(1);
        verify(categoryDao).existsCategoryWithNameExcludingId(1, "Updated Category");
        verify(categoryDao, never()).updateCategory(any());
    }

    @Test
    @DisplayName("Update category when category not found after validation should throw NotFoundException")
    void testUpdateCategory_WhenCategoryNotFoundAfterValidation_ShouldThrowNotFoundException() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdate();
        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.existsCategoryWithNameExcludingId(1, "Updated Category")).thenReturn(false);
        when(categoryDao.getCategoryById(1)).thenReturn(null); // Category deleted between validation and update

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> categoryService.updateCategory(request));

        assertEquals("Category not found", exception.getMessage());
        verify(categoryDao).getCategoryById(1);
        verify(categoryDao, never()).updateCategory(any());
    }

    @Test
    @DisplayName("Update category with concurrent access should handle race conditions")
    void testUpdateCategory_WithConcurrentAccess_ShouldHandleRaceConditions() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdate();
        Category existingCategory = createExistingCategory();

        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.existsCategoryWithNameExcludingId(1, "Updated Category"))
            .thenReturn(false) // First check passes
            .thenReturn(true); // If called again, would fail
        when(categoryDao.getCategoryById(1)).thenReturn(existingCategory);

        // Act
        categoryService.updateCategory(request);

        // Assert - Service should proceed with first validation result
        verify(categoryDao).existsById(1);
        verify(categoryDao).existsCategoryWithNameExcludingId(1, "Updated Category");
        verify(categoryDao).updateCategory(any());
    }

    @Test
    @DisplayName("Update category when image delete fails should handle gracefully")
    void testUpdateCategory_WhenImageDeleteFails_ShouldHandleGracefully() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdateWithImage();
        Category existingCategory = createExistingCategory();

        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.existsCategoryWithNameExcludingId(1, "Updated Category")).thenReturn(false);
        when(categoryDao.getCategoryById(1)).thenReturn(existingCategory);
        when(mockImagePart.getSize()).thenReturn(1024L);
        doThrow(new RuntimeException("Cloudinary delete failed")).when(imageService).deleteImage("existing-category.jpg", "categories");

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify("Updated Category")).thenReturn("updated-category");

            // Act & Assert - Should propagate the exception
            assertThrows(RuntimeException.class, () -> categoryService.updateCategory(request));

            verify(imageService).deleteImage("existing-category.jpg", "categories");
            // Should not proceed to upload new image if delete fails
            verify(imageService, never()).uploadImage(any(), any(), any());
            verify(categoryDao, never()).updateCategory(any());
        }
    }

    @Test
    @DisplayName("Update category when new image upload fails should handle gracefully")
    void testUpdateCategory_WhenNewImageUploadFails_ShouldHandleGracefully() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdateWithImage();
        Category existingCategory = createExistingCategory();

        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.existsCategoryWithNameExcludingId(1, "Updated Category")).thenReturn(false);
        when(categoryDao.getCategoryById(1)).thenReturn(existingCategory);
        when(mockImagePart.getSize()).thenReturn(1024L);
        when(imageService.uploadImage(mockImagePart, "updated-category", "categories"))
            .thenThrow(new RuntimeException("Cloudinary upload failed"));

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify("Updated Category")).thenReturn("updated-category");

            // Act & Assert
            assertThrows(RuntimeException.class, () -> categoryService.updateCategory(request));

            verify(imageService).deleteImage("existing-category.jpg", "categories");
            verify(imageService).uploadImage(mockImagePart, "updated-category", "categories");
            verify(categoryDao, never()).updateCategory(any());
        }
    }

    @Test
    @DisplayName("Add category with Cloudinary returning null should handle gracefully")
    void testAddCategory_WithCloudinaryReturningNull_ShouldHandleGracefully() {
        // Arrange
        CategoryRequest request = createCategoryRequestWithImage();
        when(categoryDao.existsByName("Test Category")).thenReturn(false);
        when(mockImagePart.getSize()).thenReturn(1024L);
        when(imageService.uploadImage(mockImagePart, "test-category", "categories")).thenReturn(null);

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify("Test Category")).thenReturn("test-category");

            // Act
            categoryService.addCategory(request);

            // Assert
            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
            verify(categoryDao).insertCategory(captor.capture());

            Category insertedCategory = captor.getValue();
            assertEquals("Test Category", insertedCategory.getName());
            assertNull(insertedCategory.getPath()); // Should handle null return gracefully

            verify(imageService).uploadImage(mockImagePart, "test-category", "categories");
        }
    }

    @Test
    @DisplayName("Update category with very large image file should handle appropriately")
    void testUpdateCategory_WithVeryLargeImageFile_ShouldHandleAppropriately() {
        // Arrange
        CategoryRequest request = createCategoryRequestForUpdateWithImage();
        Category existingCategory = createExistingCategory();

        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.existsCategoryWithNameExcludingId(1, "Updated Category")).thenReturn(false);
        when(categoryDao.getCategoryById(1)).thenReturn(existingCategory);
        when(mockImagePart.getSize()).thenReturn(50_000_000L); // 50MB file
        when(imageService.uploadImage(mockImagePart, "updated-category", "categories")).thenReturn("updated-category-large.jpg");

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify("Updated Category")).thenReturn("updated-category");

            // Act
            categoryService.updateCategory(request);

            // Assert
            verify(imageService).deleteImage("existing-category.jpg", "categories");
            verify(imageService).uploadImage(mockImagePart, "updated-category", "categories");

            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
            verify(categoryDao).updateCategory(captor.capture());

            Category updatedCategory = captor.getValue();
            assertEquals("updated-category-large.jpg", updatedCategory.getPath());
        }
    }

    @Test
    @DisplayName("Delete category with image should delete image from Cloudinary")
    void testDeleteCategory_WithImage_ShouldDeleteImageFromCloudinary() {
        // Arrange
        Category categoryWithImage = createExistingCategory();
        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.getCategoryById(1)).thenReturn(categoryWithImage);

        // Act
        categoryService.deleteCategory(1);

        // Assert
        verify(categoryDao).existsById(1);
        verify(categoryDao).getCategoryById(1);
        verify(imageService).deleteImage("existing-category.jpg", "categories");
        verify(categoryDao).deleteCategoryById(1);
    }

    @Test
    @DisplayName("Delete category without image should not attempt to delete image")
    void testDeleteCategory_WithoutImage_ShouldNotAttemptToDeleteImage() {
        // Arrange
        Category categoryWithoutImage = createExistingCategoryWithoutImage();
        when(categoryDao.existsById(1)).thenReturn(true);
        when(categoryDao.getCategoryById(1)).thenReturn(categoryWithoutImage);

        // Act
        categoryService.deleteCategory(1);

        // Assert
        verify(categoryDao).existsById(1);
        verify(categoryDao).getCategoryById(1);
        verify(imageService, never()).deleteImage(anyString(), anyString());
        verify(categoryDao).deleteCategoryById(1);
    }
}
