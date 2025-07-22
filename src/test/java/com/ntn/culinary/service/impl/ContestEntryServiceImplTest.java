package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.*;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.ContestEntry;
import com.ntn.culinary.model.ContestEntryInstruction;
import com.ntn.culinary.request.ContestEntryRequest;
import com.ntn.culinary.utils.ImageUtils;
import org.junit.jupiter.api.Disabled;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContestEntryServiceImplTest {

    @Mock private UserDao userDao;
    @Mock private ContestDao contestDao;
    @Mock private ContestEntryDao contestEntryDao;
    @Mock private CategoryDao categoryDao;
    @Mock private AreaDao areaDao;
    @Mock private ContestEntryInstructionsDao contestEntryInstructionsDao;
    @Mock private Part imagePart;

    @InjectMocks
    private ContestEntryServiceImpl contestEntryService;

    @Test // User không tồn tại
    @Disabled
    void addContestEntry_WhenUserDoesNotExist_ShouldThrowNotFound() {
        ContestEntryRequest request = createContestEntryRequest();

        when(userDao.existsById(request.getUserId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> contestEntryService.addContestEntry(request, null));

        verifyNoInteractions(contestEntryDao);
    }

    @Test // Contest không tồn tại
    @Disabled
    void addContestEntry_WhenContestDoesNotExist_ShouldThrowNotFound() {
        ContestEntryRequest request = createContestEntryRequest();

        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> contestEntryService.addContestEntry(request, null));

        verifyNoInteractions(contestEntryDao);
    }

    @Test // ContestEntry trùng tên
    @Disabled
    void addContestEntry_WhenEntryAlreadyExists_ShouldThrowConflict() {
        ContestEntryRequest request = createContestEntryRequest();

        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(true);
        when(categoryDao.existsByName(request.getCategory())).thenReturn(true);
        when(areaDao.existsByName(request.getArea())).thenReturn(true);
        when(contestEntryDao.existsByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> contestEntryService.addContestEntry(request, null));

        verifyNoMoreInteractions(contestEntryDao);
    }

    @Test // Happy path có image
    @Disabled
    void addContestEntry_WhenValidRequestWithImage_ShouldInsertEntryAndInstructions() {
        // Arrange
        ContestEntryRequest request = createContestEntryRequest();
        ContestEntryInstruction instr1 = new ContestEntryInstruction(1, 1, 1, "Step 1", "Text 1", "img1.png");
        request.setContestEntryInstructions(List.of(instr1));

        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(true);
        when(categoryDao.existsByName(request.getCategory())).thenReturn(true);
        when(areaDao.existsByName(request.getArea())).thenReturn(true);
        when(contestEntryDao.existsByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(false);

        when(imagePart.getSize()).thenReturn(123L);

        // Mock static ImageUtils
        try (MockedStatic<ImageUtils> imageUtilsMock = mockStatic(ImageUtils.class)) {
            imageUtilsMock.when(() -> ImageUtils.slugify(anyString()))
                    .thenReturn("slug");
            imageUtilsMock.when(() -> ImageUtils.saveImage(eq(imagePart), eq("slug"), eq("contest_entries")))
                    .thenReturn("saved_image.jpg");

            when(contestEntryDao.getContestEntryIdByUserIdAndContestId(
                    request.getUserId(), request.getContestId()))
                    .thenReturn(42);

            // Act
            contestEntryService.addContestEntry(request, imagePart);

            // Assert
            // Capture entry
            ArgumentCaptor<ContestEntry> entryCaptor = ArgumentCaptor.forClass(ContestEntry.class);
            verify(contestEntryDao).addContestEntry(entryCaptor.capture());
            ContestEntry savedEntry = entryCaptor.getValue();
            assertEquals("saved_image.jpg", savedEntry.getImage());
            assertEquals("PENDING", savedEntry.getStatus());
            assertEquals(request.getName(), savedEntry.getName());

            // Capture instruction
            ArgumentCaptor<ContestEntryInstruction> instrCaptor = ArgumentCaptor.forClass(ContestEntryInstruction.class);
            verify(contestEntryInstructionsDao).addContestEntryInstructions(instrCaptor.capture());
            ContestEntryInstruction savedInstr = instrCaptor.getValue();
            assertEquals(42, savedInstr.getContestEntryId());
            assertEquals("Step 1", savedInstr.getName());
        }
    }


    @Test // Happy path không có image
    @Disabled
    void addContestEntry_WhenValidRequestWithoutImage_ShouldInsertEntry() {
        ContestEntryRequest request = createContestEntryRequest();
        request.setContestEntryInstructions(Collections.emptyList());

        when(userDao.existsById(request.getUserId())).thenReturn(true);
        when(contestDao.existsById(request.getContestId())).thenReturn(true);
        when(categoryDao.existsByName(request.getCategory())).thenReturn(true);
        when(areaDao.existsByName(request.getArea())).thenReturn(true);
        when(contestEntryDao.existsByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName()))
                .thenReturn(false);

        when(contestEntryDao.getContestEntryIdByUserIdAndContestId(
                request.getUserId(), request.getContestId()))
                .thenReturn(100);

        // Act
        contestEntryService.addContestEntry(request, null);

        // Assert
        verify(contestEntryDao).addContestEntry(any());
        verify(contestEntryInstructionsDao, never()).addContestEntryInstructions(any());
    }
}