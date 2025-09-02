package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.*;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.ContestEntry;
import com.ntn.culinary.model.ContestEntryInstruction;
import com.ntn.culinary.request.ContestEntryRequest;
import com.ntn.culinary.request.DeleteContestEntryRequest;
import com.ntn.culinary.response.ContestEntryResponse;
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
import java.util.Collections;
import java.util.List;

import static com.ntn.culinary.fixture.TestDataFactory.createContestEntryRequest;
import static com.ntn.culinary.utils.StringUtils.slugify;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContestEntryServiceImplTest {

    @Mock
    private UserDao userDao;
    @Mock
    private ContestDao contestDao;
    @Mock
    private ContestEntryDao contestEntryDao;
    @Mock
    private CategoryDao categoryDao;
    @Mock
    private AreaDao areaDao;
    @Mock
    private ContestEntryInstructionsDao contestEntryInstructionsDao;
    @Mock
    private ImageService imageService;
    @Mock
    private Part imagePart;

    @InjectMocks
    private ContestEntryServiceImpl contestEntryService;

    // Helper methods for test data
    private ContestEntryRequest createValidContestEntryRequest() {
        ContestEntryRequest request = createContestEntryRequest();
        request.setContestEntryInstructions(Collections.emptyList());
        return request;
    }

    private ContestEntryRequest createContestEntryRequestWithInstructions() {
        ContestEntryRequest request = createContestEntryRequest();
        ContestEntryInstruction instruction = new ContestEntryInstruction(0, 1, "Step 1", "Instruction text", "step1.jpg");
        request.setContestEntryInstructions(List.of(instruction));
        return request;
    }

    private ContestEntry createExistingContestEntry() {
        ContestEntry entry = new ContestEntry();
        entry.setId(1);
        entry.setUserId(1);
        entry.setContestId(1);
        entry.setName("Test Entry");
        entry.setImage("existing-image.jpg");
        return entry;
    }

    // Helper method for setting up validation mocks
    private void setupValidationMocks(ContestEntryRequest request) {
        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(true);
        when(categoryDao.existsByName(request.getCategory())).thenReturn(true);
        when(areaDao.existsByName(request.getArea())).thenReturn(true);
        when(contestDao.isContestClosed(request.getContestId())).thenReturn(false);
    }

    // TEST ADD CONTEST ENTRY
    @Test
    @DisplayName("Add contest entry when user does not exist should throw NotFoundException")
    void testAddContestEntry_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        when(userDao.existsById(request.getUserId())).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.addContestEntry(request, null));

        assertEquals("User with ID does not exist.", exception.getMessage());
        verify(userDao).existsById(request.getUserId());
        verifyNoInteractions(contestEntryDao);
    }

    @Test
    @DisplayName("Add contest entry when contest does not exist should throw NotFoundException")
    void testAddContestEntry_WhenContestDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.addContestEntry(request, null));

        assertEquals("Contest with ID does not exist.", exception.getMessage());
        verify(contestDao).existsById(request.getContestId());
        verifyNoInteractions(contestEntryDao);
    }

    @Test
    @DisplayName("Add contest entry when category does not exist should throw NotFoundException")
    void testAddContestEntry_WhenCategoryDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(true);
        when(categoryDao.existsByName(request.getCategory())).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.addContestEntry(request, null));

        assertEquals("Category does not exist.", exception.getMessage());
        verify(categoryDao).existsByName(request.getCategory());
        verifyNoInteractions(contestEntryDao);
    }

    @Test
    @DisplayName("Add contest entry when area does not exist should throw NotFoundException")
    void testAddContestEntry_WhenAreaDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(true);
        when(categoryDao.existsByName(request.getCategory())).thenReturn(true);
        when(areaDao.existsByName(request.getArea())).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.addContestEntry(request, null));

        assertEquals("Area does not exist.", exception.getMessage());
        verify(areaDao).existsByName(request.getArea());
        verifyNoInteractions(contestEntryDao);
    }

    @Test
    @DisplayName("Add contest entry when contest is closed should throw ConflictException")
    void testAddContestEntry_WhenContestIsClosed_ShouldThrowConflictException() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(true);
        when(categoryDao.existsByName(request.getCategory())).thenReturn(true);
        when(areaDao.existsByName(request.getArea())).thenReturn(true);
        when(contestDao.isContestClosed(request.getContestId())).thenReturn(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
                () -> contestEntryService.addContestEntry(request, null));

        assertEquals("Cannot update contest entry as the contest is closed.", exception.getMessage());
        verify(contestDao).isContestClosed(request.getContestId());
        verifyNoInteractions(contestEntryDao);
    }

    @Test
    @DisplayName("Add contest entry when entry already exists should throw ConflictException")
    void testAddContestEntry_WhenEntryAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(true);
        when(categoryDao.existsByName(request.getCategory())).thenReturn(true);
        when(areaDao.existsByName(request.getArea())).thenReturn(true);
        when(contestDao.isContestClosed(request.getContestId())).thenReturn(false);
        // This is the validation that should fail for CREATE
        when(contestEntryDao.existsByNameAndContestId(request.getName(), request.getContestId()))
                .thenReturn(true); // Entry with same name exists in contest

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
                () -> contestEntryService.addContestEntry(request, null));

        assertEquals("Contest entry with the same name already exists for this contest.", exception.getMessage());
        verify(contestEntryDao).existsByNameAndContestId(request.getName(), request.getContestId());
        // Should not reach the user-specific check since validation fails first
        verify(contestEntryDao, never()).existsByUserIdAndContestIdAndName(anyInt(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Add contest entry with valid request without image should succeed")
    void testAddContestEntry_WithValidRequestWithoutImage_ShouldSucceed() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        setupValidationMocks(request);
        when(contestEntryDao.existsByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(false);
        when(contestEntryDao.existsByNameAndContestId(request.getName(), request.getContestId()))
                .thenReturn(false); // Add this validation mock
        when(contestEntryDao.getContestEntryIdByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(42);

        // Act
        contestEntryService.addContestEntry(request, null);

        // Assert
        ArgumentCaptor<ContestEntry> entryCaptor = ArgumentCaptor.forClass(ContestEntry.class);
        verify(contestEntryDao).addContestEntry(entryCaptor.capture());

        ContestEntry savedEntry = entryCaptor.getValue();
        assertEquals(request.getName(), savedEntry.getName());
        assertEquals("PENDING", savedEntry.getStatus());
        assertNull(savedEntry.getImage()); // No image provided

        verify(imageService, never()).uploadImage(any(), any(), any());
    }

    @Test
    @DisplayName("Add contest entry with valid request and image should upload image and succeed")
    void testAddContestEntry_WithValidRequestAndImage_ShouldUploadImageAndSucceed() {
        // Arrange
        ContestEntryRequest request = createContestEntryRequestWithInstructions();
        setupValidationMocks(request);
        when(contestEntryDao.existsByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(false);
        when(contestEntryDao.existsByNameAndContestId(request.getName(), request.getContestId()))
                .thenReturn(false); // Add this validation mock
        when(contestEntryDao.getContestEntryIdByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(42);
        when(imagePart.getSize()).thenReturn(1024L);
        when(imageService.uploadImage(imagePart, "test-entry", "contest_entries"))
                .thenReturn("test-entry-123.jpg");

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify(request.getName())).thenReturn("test-entry");

            // Act
            contestEntryService.addContestEntry(request, imagePart);

            // Assert
            ArgumentCaptor<ContestEntry> entryCaptor = ArgumentCaptor.forClass(ContestEntry.class);
            verify(contestEntryDao).addContestEntry(entryCaptor.capture());

            ContestEntry savedEntry = entryCaptor.getValue();
            assertEquals("test-entry-123.jpg", savedEntry.getImage());
            assertEquals("PENDING", savedEntry.getStatus());

            verify(imageService).uploadImage(imagePart, "test-entry", "contest_entries");

            // Verify instructions were added
            ArgumentCaptor<ContestEntryInstruction> instrCaptor = ArgumentCaptor.forClass(ContestEntryInstruction.class);
            verify(contestEntryInstructionsDao).addContestEntryInstructions(instrCaptor.capture());

            ContestEntryInstruction savedInstruction = instrCaptor.getValue();
            assertEquals(42, savedInstruction.getContestEntryId());
            assertEquals("Step 1", savedInstruction.getName());
        }
    }

    // TEST UPDATE CONTEST ENTRY
    @Test
    @DisplayName("Update contest entry with valid request and new image should delete old image and upload new one")
    void testUpdateContestEntry_WithValidRequestAndNewImage_ShouldDeleteOldImageAndUploadNewOne() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        request.setId(1); // Set ID for update validation
        ContestEntry existingEntry = createExistingContestEntry();

        setupValidationMocks(request);
        when(contestEntryDao.getContestEntryByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(existingEntry);
        when(contestEntryDao.existsContestEntryWithNameExcludingId(
                request.getName(), request.getContestId(), request.getId()))
                .thenReturn(true); // Entry name is valid (not conflicting)
        when(imagePart.getSize()).thenReturn(1024L);
        when(imageService.uploadImage(imagePart, "test-entry", "contest_entries"))
                .thenReturn("test-entry-new.jpg");

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify(request.getName())).thenReturn("test-entry");

            // Act
            contestEntryService.updateContestEntry(request, imagePart);

            // Assert
            verify(imageService).deleteImage("existing-image.jpg", "contest_entries");
            verify(imageService).uploadImage(imagePart, "test-entry", "contest_entries");
            verify(contestEntryDao).updateContestEntry(any(), eq("test-entry-new.jpg"));
        }
    }

    @Test
    @DisplayName("Update contest entry when entry not found should throw NotFoundException")
    void testUpdateContestEntry_WhenEntryNotFound_ShouldThrowNotFoundException() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        request.setId(1); // Set ID for update validation
        setupValidationMocks(request);

        // Mock validation to pass (entry name is valid for update)
        when(contestEntryDao.existsContestEntryWithNameExcludingId(
                request.getName(), request.getContestId(), request.getId()))
                .thenReturn(true); // Validation passes

        // But entry is not found when trying to get it
        when(contestEntryDao.getContestEntryByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.updateContestEntry(request, null));

        assertEquals("Contest entry not found.", exception.getMessage());
        verify(imageService, never()).uploadImage(any(), any(), any());
        verify(imageService, never()).deleteImage(any(), any());
    }

    @Test
    @DisplayName("Update contest entry without new image should not change existing image")
    void testUpdateContestEntry_WithoutNewImage_ShouldNotChangeExistingImage() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        request.setId(1); // Set ID for update validation
        ContestEntry existingEntry = createExistingContestEntry();

        setupValidationMocks(request);
        when(contestEntryDao.getContestEntryByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(existingEntry);
        when(contestEntryDao.existsContestEntryWithNameExcludingId(
                request.getName(), request.getContestId(), request.getId()))
                .thenReturn(true); // Entry name is valid (not conflicting)

        List<ContestEntryInstruction> existingInstructions = List.of(
                new ContestEntryInstruction(1, 1, 1, "Existing Step", "Existing text", "existing.jpg")
        );
        when(contestEntryInstructionsDao.getContestEntryInstructionsByContestEntryId(1))
                .thenReturn(existingInstructions);


        // Act
        contestEntryService.updateContestEntry(request, null);

        // Assert
        verify(imageService, never()).uploadImage(any(), any(), any());
        verify(imageService, never()).deleteImage(any(), any());
        verify(contestEntryDao).updateContestEntry(any(), eq("existing-image.jpg"));
    }

    // TEST DELETE CONTEST ENTRY
    @Test
    @DisplayName("Delete contest entry when entry exists should delete entry and image")
    void testDeleteContestEntry_WhenEntryExists_ShouldDeleteEntryAndImage() {
        // Arrange
        DeleteContestEntryRequest request = new DeleteContestEntryRequest();
        request.setUserId(1);
        request.setContestId(1);
        request.setName("Test Entry");

        ContestEntry existingEntry = createExistingContestEntry();
        List<ContestEntryInstruction> instructions = List.of(
                new ContestEntryInstruction(1, 1, 1, "Step 1", "Text", "step.jpg")
        );

        when(contestEntryDao.existsByUserIdAndContestIdAndName(1, 1, "Test Entry")).thenReturn(true);
        when(contestEntryDao.getContestEntryByUserIdAndContestIdAndName(1, 1, "Test Entry"))
                .thenReturn(existingEntry);
        when(contestEntryInstructionsDao.getContestEntryInstructionsByContestEntryId(1))
                .thenReturn(instructions);

        // Act
        contestEntryService.deleteContestEntry(request);

        // Assert
        verify(contestEntryInstructionsDao).deleteContestEntryInstructionById(1);
        verify(imageService).deleteImage("existing-image.jpg", "contest_entries");
        verify(contestEntryDao).deleteContestEntryByUserIdAndContestIdAndName(1, 1, "Test Entry");
    }

    @Test
    @DisplayName("Delete contest entry when entry does not exist should throw NotFoundException")
    void testDeleteContestEntry_WhenEntryDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        DeleteContestEntryRequest request = new DeleteContestEntryRequest();
        request.setUserId(1);
        request.setContestId(1);
        request.setName("Nonexistent Entry");

        when(contestEntryDao.existsByUserIdAndContestIdAndName(1, 1, "Nonexistent Entry"))
                .thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.deleteContestEntry(request));

        assertEquals("Contest entry with the specified user ID, contest ID, and name does not exist.", exception.getMessage());
        verify(imageService, never()).deleteImage(any(), any());
        verify(contestEntryDao, never()).deleteContestEntryByUserIdAndContestIdAndName(anyInt(), anyInt(), anyString());
    }

    // TEST GET METHODS
    @Test
    @DisplayName("Get contest entry by user ID and contest ID when entry exists should return response")
    void testGetContestEntryByUserIdAndContestId_WhenEntryExists_ShouldReturnResponse() {
        // Arrange
        ContestEntry contestEntry = createExistingContestEntry();
        when(contestEntryDao.getContestEntryByUserIdAndContestId(1, 1)).thenReturn(contestEntry);
        when(contestEntryInstructionsDao.getContestEntryInstructionsByContestEntryId(1))
                .thenReturn(Collections.emptyList());

        // Act
        ContestEntryResponse response = contestEntryService.getContestEntryByUserIdAndContestId(1, 1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Test Entry", response.getName());
        assertTrue(response.getImage().contains("contest_entry/existing-image.jpg"));
    }

    @Test
    @DisplayName("Get contest entry by user ID and contest ID when entry not found should throw NotFoundException")
    void testGetContestEntryByUserIdAndContestId_WhenEntryNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(contestEntryDao.getContestEntryByUserIdAndContestId(1, 1)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.getContestEntryByUserIdAndContestId(1, 1));

        assertEquals("Contest entry not found for the specified user ID and contest ID.", exception.getMessage());
    }

    @Test
    @DisplayName("Get contest entry by ID when entry exists should return response")
    void testGetContestEntryById_WhenEntryExists_ShouldReturnResponse() {
        // Arrange
        ContestEntry contestEntry = createExistingContestEntry();
        when(contestEntryDao.getContestEntryById(1)).thenReturn(contestEntry);
        when(contestEntryInstructionsDao.getContestEntryInstructionsByContestEntryId(1))
                .thenReturn(Collections.emptyList());

        // Act
        ContestEntryResponse response = contestEntryService.getContestEntryById(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Test Entry", response.getName());
    }

    @Test
    @DisplayName("Get contest entry by ID when entry not found should throw NotFoundException")
    void testGetContestEntryById_WhenEntryNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(contestEntryDao.getContestEntryById(1)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.getContestEntryById(1));

        assertEquals("Contest entry with the specified ID does not exist.", exception.getMessage());
    }

    @Test
    @DisplayName("Get contest entries by contest ID when entries exist should return list")
    void testGetContestEntriesByContestId_WhenEntriesExist_ShouldReturnList() {
        // Arrange
        List<ContestEntry> contestEntries = List.of(createExistingContestEntry());
        when(contestEntryDao.getContestEntryByContestId(1)).thenReturn(contestEntries);
        when(contestEntryInstructionsDao.getContestEntryInstructionsByContestEntryId(1))
                .thenReturn(Collections.emptyList());

        // Act
        List<ContestEntryResponse> responses = contestEntryService.getContestEntriesByContestId(1);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Entry", responses.get(0).getName());
    }

    @Test
    @DisplayName("Get contest entries by contest ID when no entries found should throw NotFoundException")
    void testGetContestEntriesByContestId_WhenNoEntriesFound_ShouldThrowNotFoundException() {
        // Arrange
        when(contestEntryDao.getContestEntryByContestId(1)).thenReturn(Collections.emptyList());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.getContestEntriesByContestId(1));

        assertEquals("No contest entries found for the specified contest ID.", exception.getMessage());
    }

    @Test
    @DisplayName("Get contest entries by user ID when entries exist should return list")
    void testGetContestEntriesByUserId_WhenEntriesExist_ShouldReturnList() {
        // Arrange
        List<ContestEntry> contestEntries = List.of(createExistingContestEntry());
        when(contestEntryDao.getContestEntriesByUserId(1)).thenReturn(contestEntries);
        when(contestEntryInstructionsDao.getContestEntryInstructionsByContestEntryId(1))
                .thenReturn(Collections.emptyList());

        // Act
        List<ContestEntryResponse> responses = contestEntryService.getContestEntriesByUserId(1);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Entry", responses.get(0).getName());
    }

    @Test
    @DisplayName("Get contest entries by user ID when no entries found should throw NotFoundException")
    void testGetContestEntriesByUserId_WhenNoEntriesFound_ShouldThrowNotFoundException() {
        // Arrange
        when(contestEntryDao.getContestEntriesByUserId(1)).thenReturn(Collections.emptyList());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> contestEntryService.getContestEntriesByUserId(1));

        assertEquals("No contest entries found for the specified user ID.", exception.getMessage());
    }

    // ERROR HANDLING TESTS
    @Test
    @DisplayName("Add contest entry when image upload fails should propagate exception")
    void testAddContestEntry_WhenImageUploadFails_ShouldPropagateException() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        setupValidationMocks(request);
        when(contestEntryDao.existsByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(false);
        when(imagePart.getSize()).thenReturn(1024L);
        when(imageService.uploadImage(imagePart, "test-entry", "contest_entries"))
                .thenThrow(new RuntimeException("Cloudinary upload failed"));

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify(request.getName())).thenReturn("test-entry");

            // Act & Assert
            assertThrows(RuntimeException.class,
                    () -> contestEntryService.addContestEntry(request, imagePart));

            verify(imageService).uploadImage(imagePart, "test-entry", "contest_entries");
            verify(contestEntryDao, never()).addContestEntry(any());
        }
    }

    @Test
    @DisplayName("Update contest entry when image delete fails should propagate exception")
    void testUpdateContestEntry_WhenImageDeleteFails_ShouldPropagateException() {
        // Arrange
        ContestEntryRequest request = createValidContestEntryRequest();
        request.setId(1); // Set ID for update validation
        ContestEntry existingEntry = createExistingContestEntry();

        setupValidationMocks(request);
        // Mock validation to pass
        when(contestEntryDao.existsContestEntryWithNameExcludingId(
                request.getName(), request.getContestId(), request.getId()))
                .thenReturn(true);

        when(contestEntryDao.getContestEntryByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(existingEntry);
        when(imagePart.getSize()).thenReturn(1024L);

        // Mock delete to throw exception
        doThrow(new RuntimeException("Cloudinary delete failed"))
                .when(imageService).deleteImage("existing-image.jpg", "contest_entries");

        try (MockedStatic<StringUtils> stringUtils = mockStatic(StringUtils.class)) {
            stringUtils.when(() -> slugify(request.getName())).thenReturn("test-entry");

            // Act & Assert
            assertThrows(RuntimeException.class,
                    () -> contestEntryService.updateContestEntry(request, imagePart));

            verify(imageService).deleteImage("existing-image.jpg", "contest_entries");
            verify(imageService, never()).uploadImage(any(), any(), any());
            verify(contestEntryDao, never()).updateContestEntry(any(), any());
        }
    }
}
