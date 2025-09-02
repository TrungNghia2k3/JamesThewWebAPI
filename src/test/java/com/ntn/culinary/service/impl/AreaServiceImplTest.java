package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.AreaDao;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.Area;
import com.ntn.culinary.request.AreaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceImplTest {

    @Mock
    private AreaDao areaDao;

    @InjectMocks
    private AreaServiceImpl areaService;

    // Helper methods for test data
    private AreaRequest createValidAreaRequest() {
        return new AreaRequest(0, "Test Area");
    }

    private AreaRequest createAreaRequestForUpdate() {
        return new AreaRequest(1, "Updated Area");
    }

    // TEST ADD AREA
    @Test
    @DisplayName("Add area with valid request should insert area successfully")
    void testAddArea_WithValidRequest_ShouldInsertAreaSuccessfully() {
        // Arrange
        AreaRequest request = createValidAreaRequest();
        when(areaDao.existsByName("Test Area")).thenReturn(false);

        // Act
        areaService.addArea(request);

        // Assert
        verify(areaDao).existsByName("Test Area");
        verify(areaDao).insertArea("Test Area");
    }

    @Test
    @DisplayName("Add area when name already exists should throw ConflictException")
    void testAddArea_WhenNameAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        AreaRequest request = createValidAreaRequest();
        when(areaDao.existsByName("Test Area")).thenReturn(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
                () -> areaService.addArea(request));

        assertEquals("Area with name already exists.", exception.getMessage());
        verify(areaDao).existsByName("Test Area");
        verify(areaDao, never()).insertArea(anyString());
    }

    @Test
    @DisplayName("Add area with special characters should succeed")
    void testAddArea_WithSpecialCharacters_ShouldSucceed() {
        // Arrange
        AreaRequest request = new AreaRequest(0, "Area-123 & Test!");
        when(areaDao.existsByName("Area-123 & Test!")).thenReturn(false);

        // Act
        areaService.addArea(request);

        // Assert
        verify(areaDao).existsByName("Area-123 & Test!");
        verify(areaDao).insertArea("Area-123 & Test!");
    }

    @Test
    @DisplayName("Add area with very long name should succeed")
    void testAddArea_WithVeryLongName_ShouldSucceed() {
        // Arrange
        String longName = "A".repeat(255);
        AreaRequest request = new AreaRequest(0, longName);
        when(areaDao.existsByName(longName)).thenReturn(false);

        // Act
        areaService.addArea(request);

        // Assert
        verify(areaDao).existsByName(longName);
        verify(areaDao).insertArea(longName);
    }

    @Test
    @DisplayName("Add area when DAO existsByName throws exception should propagate exception")
    void testAddArea_WhenDaoExistsByNameThrowsException_ShouldPropagateException() {
        // Arrange
        AreaRequest request = createValidAreaRequest();
        when(areaDao.existsByName("Test Area")).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> areaService.addArea(request));

        verify(areaDao).existsByName("Test Area");
        verify(areaDao, never()).insertArea(anyString());
    }

    @Test
    @DisplayName("Add area when DAO insertArea throws exception should propagate exception")
    void testAddArea_WhenDaoInsertAreaThrowsException_ShouldPropagateException() {
        // Arrange
        AreaRequest request = createValidAreaRequest();
        when(areaDao.existsByName("Test Area")).thenReturn(false);
        doThrow(new RuntimeException("Insert failed")).when(areaDao).insertArea("Test Area");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> areaService.addArea(request));

        verify(areaDao).existsByName("Test Area");
        verify(areaDao).insertArea("Test Area");
    }

    @Test
    @DisplayName("Add area with case sensitive name should be treated as different")
    void testAddArea_WithCaseSensitiveName_ShouldBeTreatedAsDifferent() {
        // Arrange
        AreaRequest request = new AreaRequest(0, "Test Area");
        when(areaDao.existsByName("Test Area")).thenReturn(false);

        // Act
        areaService.addArea(request);

        // Assert - Service should pass exact case to DAO
        verify(areaDao).existsByName("Test Area");
        verify(areaDao).insertArea("Test Area");
    }

    // TEST UPDATE AREA
    @Test
    @DisplayName("Update area with valid request should update area successfully")
    void testUpdateArea_WithValidRequest_ShouldUpdateAreaSuccessfully() {
        // Arrange
        AreaRequest request = createAreaRequestForUpdate();
        when(areaDao.existsById(1)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(1, "Updated Area")).thenReturn(false);

        // Act
        areaService.updateArea(request);

        // Assert
        verify(areaDao).existsById(1);
        verify(areaDao).existsAreaWithNameExcludingId(1, "Updated Area");

        ArgumentCaptor<Area> captor = ArgumentCaptor.forClass(Area.class);
        verify(areaDao).updateArea(captor.capture());

        Area updatedArea = captor.getValue();
        assertEquals(1, updatedArea.getId());
        assertEquals("Updated Area", updatedArea.getName());
    }

    @Test
    @DisplayName("Update area when ID does not exist should throw NotFoundException")
    void testUpdateArea_WhenIdDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        AreaRequest request = createAreaRequestForUpdate();
        when(areaDao.existsById(1)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> areaService.updateArea(request));

        assertEquals("Area with ID does not exist.", exception.getMessage());
        verify(areaDao).existsById(1);
        verify(areaDao, never()).existsAreaWithNameExcludingId(anyInt(), anyString());
        verify(areaDao, never()).updateArea(any());
    }

    @Test
    @DisplayName("Update area when name already exists for another area should throw ConflictException")
    void testUpdateArea_WhenNameExistsForAnotherArea_ShouldThrowConflictException() {
        // Arrange
        AreaRequest request = createAreaRequestForUpdate();
        when(areaDao.existsById(1)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(1, "Updated Area")).thenReturn(true);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
                () -> areaService.updateArea(request));

        assertEquals("Area with name already exists.", exception.getMessage());
        verify(areaDao).existsById(1);
        verify(areaDao).existsAreaWithNameExcludingId(1, "Updated Area");
        verify(areaDao, never()).updateArea(any());
    }

    @Test
    @DisplayName("Update area with empty name should proceed with validation")
    void testUpdateArea_WithEmptyName_ShouldProceedWithValidation() {
        // Arrange
        AreaRequest request = new AreaRequest(1, "");
        when(areaDao.existsById(1)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(1, "")).thenReturn(false);

        // Act
        areaService.updateArea(request);

        // Assert
        verify(areaDao).existsById(1);
        verify(areaDao).existsAreaWithNameExcludingId(1, "");

        ArgumentCaptor<Area> captor = ArgumentCaptor.forClass(Area.class);
        verify(areaDao).updateArea(captor.capture());

        Area updatedArea = captor.getValue();
        assertEquals(1, updatedArea.getId());
        assertEquals("", updatedArea.getName());
    }

    @Test
    @DisplayName("Update area with negative ID should proceed but validation should fail")
    void testUpdateArea_WithNegativeId_ShouldProceedButValidationShouldFail() {
        // Arrange
        AreaRequest request = new AreaRequest(-1, "Test Area");
        when(areaDao.existsById(-1)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> areaService.updateArea(request));

        assertEquals("Area with ID does not exist.", exception.getMessage());
        verify(areaDao).existsById(-1);
        verify(areaDao, never()).updateArea(any());
    }

    @Test
    @DisplayName("Update area with zero ID should proceed but validation should fail")
    void testUpdateArea_WithZeroId_ShouldProceedButValidationShouldFail() {
        // Arrange
        AreaRequest request = new AreaRequest(0, "Test Area");
        when(areaDao.existsById(0)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> areaService.updateArea(request));

        assertEquals("Area with ID does not exist.", exception.getMessage());
        verify(areaDao).existsById(0);
        verify(areaDao, never()).updateArea(any());
    }

    @Test
    @DisplayName("Update area when updating to same name should succeed")
    void testUpdateArea_WhenUpdatingToSameName_ShouldSucceed() {
        // Arrange
        AreaRequest request = new AreaRequest(1, "Same Name");
        when(areaDao.existsById(1)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(1, "Same Name")).thenReturn(false);

        // Act
        areaService.updateArea(request);

        // Assert
        verify(areaDao).existsById(1);
        verify(areaDao).existsAreaWithNameExcludingId(1, "Same Name");
        verify(areaDao).updateArea(any());
    }

    @Test
    @DisplayName("Update area with special characters should succeed")
    void testUpdateArea_WithSpecialCharacters_ShouldSucceed() {
        // Arrange
        AreaRequest request = new AreaRequest(1, "Updated-Area & Test#123!");
        when(areaDao.existsById(1)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(1, "Updated-Area & Test#123!")).thenReturn(false);

        // Act
        areaService.updateArea(request);

        // Assert
        verify(areaDao).existsById(1);
        verify(areaDao).existsAreaWithNameExcludingId(1, "Updated-Area & Test#123!");

        ArgumentCaptor<Area> captor = ArgumentCaptor.forClass(Area.class);
        verify(areaDao).updateArea(captor.capture());

        Area updatedArea = captor.getValue();
        assertEquals(1, updatedArea.getId());
        assertEquals("Updated-Area & Test#123!", updatedArea.getName());
    }

    @Test
    @DisplayName("Update area with whitespace name should proceed with validation")
    void testUpdateArea_WithWhitespaceName_ShouldProceedWithValidation() {
        // Arrange
        AreaRequest request = new AreaRequest(1, "   ");
        when(areaDao.existsById(1)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(1, "   ")).thenReturn(false);

        // Act
        areaService.updateArea(request);

        // Assert
        verify(areaDao).existsById(1);
        verify(areaDao).existsAreaWithNameExcludingId(1, "   ");

        ArgumentCaptor<Area> captor = ArgumentCaptor.forClass(Area.class);
        verify(areaDao).updateArea(captor.capture());

        Area updatedArea = captor.getValue();
        assertEquals(1, updatedArea.getId());
        assertEquals("   ", updatedArea.getName());
    }

    @Test
    @DisplayName("Update area when DAO existsById throws exception should propagate exception")
    void testUpdateArea_WhenDaoExistsByIdThrowsException_ShouldPropagateException() {
        // Arrange
        AreaRequest request = createAreaRequestForUpdate();
        when(areaDao.existsById(1)).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> areaService.updateArea(request));

        verify(areaDao).existsById(1);
        verify(areaDao, never()).existsAreaWithNameExcludingId(anyInt(), anyString());
        verify(areaDao, never()).updateArea(any());
    }

    @Test
    @DisplayName("Update area when DAO existsAreaWithNameExcludingId throws exception should propagate exception")
    void testUpdateArea_WhenDaoExistsAreaWithNameExcludingIdThrowsException_ShouldPropagateException() {
        // Arrange
        AreaRequest request = createAreaRequestForUpdate();
        when(areaDao.existsById(1)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(1, "Updated Area"))
                .thenThrow(new RuntimeException("Database query error"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> areaService.updateArea(request));

        verify(areaDao).existsById(1);
        verify(areaDao).existsAreaWithNameExcludingId(1, "Updated Area");
        verify(areaDao, never()).updateArea(any());
    }

    @Test
    @DisplayName("Update area when DAO updateArea throws exception should propagate exception")
    void testUpdateArea_WhenDaoUpdateAreaThrowsException_ShouldPropagateException() {
        // Arrange
        AreaRequest request = createAreaRequestForUpdate();
        when(areaDao.existsById(1)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(1, "Updated Area")).thenReturn(false);
        doThrow(new RuntimeException("Update failed")).when(areaDao).updateArea(any());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> areaService.updateArea(request));

        verify(areaDao).existsById(1);
        verify(areaDao).existsAreaWithNameExcludingId(1, "Updated Area");
        verify(areaDao).updateArea(any());
    }

    @Test
    @DisplayName("Update area mapping should preserve all request fields")
    void testUpdateArea_MappingShouldPreserveAllRequestFields() {
        // Arrange
        AreaRequest request = new AreaRequest(42, "Complex Area Name!");
        when(areaDao.existsById(42)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(42, "Complex Area Name!")).thenReturn(false);

        // Act
        areaService.updateArea(request);

        // Assert
        ArgumentCaptor<Area> captor = ArgumentCaptor.forClass(Area.class);
        verify(areaDao).updateArea(captor.capture());

        Area mappedArea = captor.getValue();
        assertEquals(42, mappedArea.getId());
        assertEquals("Complex Area Name!", mappedArea.getName());
    }

    @Test
    @DisplayName("Add area should handle concurrent access scenarios")
    void testAddArea_ShouldHandleConcurrentAccessScenarios() {
        // Arrange - Simulate race condition where name becomes available between validation checks
        AreaRequest request = createValidAreaRequest();
        when(areaDao.existsByName("Test Area"))
                .thenReturn(false) // First check passes
                .thenReturn(true); // If called again, it would fail

        // Act
        areaService.addArea(request);

        // Assert - Service should proceed with first validation result
        verify(areaDao).existsByName("Test Area");
        verify(areaDao).insertArea("Test Area");
    }

    @Test
    @DisplayName("Update area should handle concurrent access scenarios")
    void testUpdateArea_ShouldHandleConcurrentAccessScenarios() {
        // Arrange - Simulate concurrent modification
        AreaRequest request = createAreaRequestForUpdate();
        when(areaDao.existsById(1)).thenReturn(true);
        when(areaDao.existsAreaWithNameExcludingId(1, "Updated Area"))
                .thenReturn(false) // First check passes
                .thenReturn(true); // If called again, it would fail

        // Act
        areaService.updateArea(request);

        // Assert - Service should proceed with first validation result
        verify(areaDao).existsById(1);
        verify(areaDao).existsAreaWithNameExcludingId(1, "Updated Area");
        verify(areaDao).updateArea(any());
    }
}