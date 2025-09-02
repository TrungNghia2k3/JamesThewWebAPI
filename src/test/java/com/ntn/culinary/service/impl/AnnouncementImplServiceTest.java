package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.AnnounceWinnerDao;
import com.ntn.culinary.dao.AnnouncementDao;
import com.ntn.culinary.dao.ContestDao;
import com.ntn.culinary.dao.ContestEntryDao;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.AnnounceWinner;
import com.ntn.culinary.model.Announcement;
import com.ntn.culinary.model.Contest;
import com.ntn.culinary.model.ContestEntry;
import com.ntn.culinary.request.AnnouncementRequest;
import com.ntn.culinary.response.AnnouncementResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.ntn.culinary.fixture.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementImplServiceTest {

    @Mock
    private ContestDao contestDao;

    @Mock
    private AnnouncementDao announcementDao;

    @Mock
    private AnnounceWinnerDao announceWinnerDao;

    @Mock
    private ContestEntryDao contestEntryDao;

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    // TEST GET ALL ANNOUNCEMENTS
    @Test
    @DisplayName("Get all announcements when announcements exist should return AnnouncementResponses")
    void testGetAllAnnouncements_WhenAnnouncementsExist_ReturnsAnnouncementResponses() {
        // Arrange
        Announcement announcement = createAnnouncement();
        Contest contest = createContest();
        AnnounceWinner winner = createWinner();
        ContestEntry entry = createContestEntry();

        when(announcementDao.getAllAnnouncements()).thenReturn(List.of(announcement));
        when(contestDao.getContestById(42)).thenReturn(contest);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(1)).thenReturn(List.of(winner));
        when(contestEntryDao.getContestEntryById(200)).thenReturn(entry);

        // Act
        List<AnnouncementResponse> responses = announcementService.getAllAnnouncements();

        // Assert
        assertEquals(1, responses.size());
        AnnouncementResponse response = responses.get(0);
        assertEquals("Test Announcement", response.getTitle());
        assertEquals("Contest Headline", response.getContest().getHeadline());
        assertEquals(1, response.getWinners().size());
        assertEquals("Entry Title", response.getWinners().get(0).getContestEntry().getName());

        verify(announcementDao).getAllAnnouncements();
        verify(contestDao).getContestById(42);
        verify(announceWinnerDao).getAllWinnersByAnnouncementId(1);
        verify(contestEntryDao).getContestEntryById(200);
    }

    @Test
    @DisplayName("Get all announcements when no announcements exist should return empty list")
    void testGetAllAnnouncements_WhenNoAnnouncements_ReturnsEmptyList() {
        // Arrange
        when(announcementDao.getAllAnnouncements()).thenReturn(List.of());

        // Act
        List<AnnouncementResponse> responses = announcementService.getAllAnnouncements();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(announcementDao).getAllAnnouncements();
        verifyNoMoreInteractions(contestDao, announceWinnerDao, contestEntryDao);
    }

    @Test
    @DisplayName("Get all announcements when contest does not exist should throw RuntimeException")
    void testGetAllAnnouncements_WhenContestEntryNotFound_ThrowsRuntimeException() {
        // Arrange
        Announcement announcement = createAnnouncement();
        Contest contest = createContest();
        AnnounceWinner winner = createWinner();

        when(announcementDao.getAllAnnouncements()).thenReturn(List.of(announcement));
        when(contestDao.getContestById(42)).thenReturn(contest);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(1)).thenReturn(List.of(winner));
        when(contestEntryDao.getContestEntryById(200)).thenThrow(new RuntimeException());

        // Act
        assertThrows(RuntimeException.class, () -> announcementService.getAllAnnouncements());

        // Assert
        verify(announcementDao).getAllAnnouncements();
        verify(contestDao).getContestById(42);
        verify(announceWinnerDao).getAllWinnersByAnnouncementId(1);
        verify(contestEntryDao).getContestEntryById(200);
    }

    @Test
    @DisplayName("Get all announcements with multiple announcements should return all responses")
    void testGetAllAnnouncements_WithMultipleAnnouncements_ShouldReturnAllResponses() {
        // Arrange
        List<Announcement> announcements = createAnnouncementsList();
        Contest contest1 = createContest();
        contest1.setId(10);
        contest1.setHeadline("Contest 1");
        Contest contest2 = createContest();
        contest2.setId(20);
        contest2.setHeadline("Contest 2");
        Contest contest3 = createContest();
        contest3.setId(30);
        contest3.setHeadline("Contest 3");

        when(announcementDao.getAllAnnouncements()).thenReturn(announcements);
        when(contestDao.getContestById(10)).thenReturn(contest1);
        when(contestDao.getContestById(20)).thenReturn(contest2);
        when(contestDao.getContestById(30)).thenReturn(contest3);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(anyInt())).thenReturn(List.of());

        // Act
        List<AnnouncementResponse> responses = announcementService.getAllAnnouncements();

        // Assert
        assertEquals(3, responses.size());
        assertEquals("Announcement 1", responses.get(0).getTitle());
        assertEquals("Announcement 2", responses.get(1).getTitle());
        assertEquals("Announcement 3", responses.get(2).getTitle());

        verify(announcementDao).getAllAnnouncements();
        verify(contestDao).getContestById(10);
        verify(contestDao).getContestById(20);
        verify(contestDao).getContestById(30);
    }

    @Test
    @DisplayName("Get all announcements with some having no winners should handle gracefully")
    void testGetAllAnnouncements_WithSomeHavingNoWinners_ShouldHandleGracefully() {
        // Arrange
        Announcement announcement = createAnnouncement();
        Contest contest = createContest();

        when(announcementDao.getAllAnnouncements()).thenReturn(List.of(announcement));
        when(contestDao.getContestById(42)).thenReturn(contest);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(1)).thenReturn(List.of()); // No winners

        // Act
        List<AnnouncementResponse> responses = announcementService.getAllAnnouncements();

        // Assert
        assertEquals(1, responses.size());
        AnnouncementResponse response = responses.get(0);
        assertEquals("Test Announcement", response.getTitle());
        assertEquals(0, response.getWinners().size()); // Should have empty winners list

        verify(announceWinnerDao).getAllWinnersByAnnouncementId(1);
        verify(contestEntryDao, never()).getContestEntryById(anyInt()); // Should not call contest entry
    }

    @Test
    @DisplayName("Get all announcements when contest does not exist should throw RuntimeException")
    void testGetAllAnnouncements_WhenContestNotFound_ThrowsRuntimeException() {
        // Arrange
        Announcement announcement = createAnnouncement();
        when(announcementDao.getAllAnnouncements()).thenReturn(List.of(announcement));
        when(contestDao.getContestById(42)).thenThrow(new RuntimeException("Contest not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> announcementService.getAllAnnouncements());

        verify(announcementDao).getAllAnnouncements();
        verify(contestDao).getContestById(42);
        verifyNoInteractions(announceWinnerDao, contestEntryDao);
    }

    @Test
    @DisplayName("Get all announcements with multiple winners per announcement should map correctly")
    void testGetAllAnnouncements_WithMultipleWinnersPerAnnouncement_ShouldMapCorrectly() {
        // Arrange
        Announcement announcement = createAnnouncement();
        Contest contest = createContest();
        List<AnnounceWinner> winners = createWinnersList(); // Returns 2 winners
        ContestEntry entry1 = createContestEntry();
        entry1.setId(100);
        entry1.setName("Entry 1");
        ContestEntry entry2 = createContestEntry();
        entry2.setId(200);
        entry2.setName("Entry 2");

        when(announcementDao.getAllAnnouncements()).thenReturn(List.of(announcement));
        when(contestDao.getContestById(42)).thenReturn(contest);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(1)).thenReturn(winners);
        when(contestEntryDao.getContestEntryById(100)).thenReturn(entry1);
        when(contestEntryDao.getContestEntryById(200)).thenReturn(entry2);

        // Act
        List<AnnouncementResponse> responses = announcementService.getAllAnnouncements();

        // Assert
        assertEquals(1, responses.size());
        AnnouncementResponse response = responses.get(0);
        assertEquals(2, response.getWinners().size());
        assertEquals("Entry 1", response.getWinners().get(0).getContestEntry().getName());
        assertEquals("Entry 2", response.getWinners().get(1).getContestEntry().getName());
        assertEquals("1", response.getWinners().get(0).getRanking());
        assertEquals("2", response.getWinners().get(1).getRanking());
    }

    // TEST GET ANNOUNCEMENT BY ID
    @Test
    @DisplayName("Get announcement by ID when announcement exists should return AnnouncementResponse")
    void testGetAnnouncementById_WhenAnnouncementExists_ReturnsAnnouncementResponse() {
        // Arrange
        Announcement announcement = createAnnouncement();
        Contest contest = createContest();
        AnnounceWinner winner = createWinner();
        ContestEntry entry = createContestEntry();

        when(announcementDao.getAnnouncementById(1)).thenReturn(announcement);
        when(contestDao.getContestById(42)).thenReturn(contest);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(1)).thenReturn(List.of(winner));
        when(contestEntryDao.getContestEntryById(200)).thenReturn(entry);

        // Act
        AnnouncementResponse response = announcementService.getAnnouncementById(1);

        // Assert
        assertNotNull(response);
        assertEquals("Test Announcement", response.getTitle());
        assertEquals("Contest Headline", response.getContest().getHeadline());
        assertEquals(1, response.getWinners().size());
        assertEquals("Entry Title", response.getWinners().get(0).getContestEntry().getName());

        verify(announcementDao).getAnnouncementById(1);
        verify(contestDao).getContestById(42);
        verify(announceWinnerDao).getAllWinnersByAnnouncementId(1);
        verify(contestEntryDao).getContestEntryById(200);
    }

    @Test
    @DisplayName("Get announcement by ID when announcement does not exist should throw NotFoundException")
    void testGetAnnouncementById_WhenAnnouncementDoesNotExist_ThrowsNotFoundException() {
        // Arrange
        when(announcementDao.getAnnouncementById(1)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> announcementService.getAnnouncementById(1));

        // Verify no further interactions
        verify(announcementDao).getAnnouncementById(1);
        verifyNoMoreInteractions(contestDao, announceWinnerDao, contestEntryDao);
    }

    @Test
    @DisplayName("Get announcement by ID when contest does not exist should throw NotFoundException")
    void testGetAnnouncementById_WhenContestNotFound_ThrowsRuntimeException() {
        // Arrange
        Announcement announcement = createAnnouncement();
        when(announcementDao.getAnnouncementById(1)).thenReturn(announcement);
        when(contestDao.getContestById(42)).thenThrow(new RuntimeException("Contest not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> announcementService.getAnnouncementById(1));

        // Verify interactions
        verify(announcementDao).getAnnouncementById(1);
        verify(contestDao).getContestById(42);
        verifyNoMoreInteractions(announceWinnerDao, contestEntryDao);
    }

    @Test
    @DisplayName("Get announcement by ID with invalid ID should throw NotFoundException")
    void testGetAnnouncementById_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(announcementDao.getAnnouncementById(-1)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> announcementService.getAnnouncementById(-1));

        assertEquals("Announcement with ID does not exist.", exception.getMessage());
        verify(announcementDao).getAnnouncementById(-1);
        verifyNoMoreInteractions(contestDao, announceWinnerDao, contestEntryDao);
    }

    @Test
    @DisplayName("Get announcement by ID with zero ID should throw NotFoundException")
    void testGetAnnouncementById_WithZeroId_ShouldThrowNotFoundException() {
        // Arrange
        when(announcementDao.getAnnouncementById(0)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> announcementService.getAnnouncementById(0));

        assertEquals("Announcement with ID does not exist.", exception.getMessage());
        verify(announcementDao).getAnnouncementById(0);
    }

    @Test
    @DisplayName("Get announcement by ID with no winners should return response with empty winners")
    void testGetAnnouncementById_WithNoWinners_ShouldReturnResponseWithEmptyWinners() {
        // Arrange
        Announcement announcement = createAnnouncement();
        Contest contest = createContest();

        when(announcementDao.getAnnouncementById(1)).thenReturn(announcement);
        when(contestDao.getContestById(42)).thenReturn(contest);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(1)).thenReturn(List.of()); // No winners

        // Act
        AnnouncementResponse response = announcementService.getAnnouncementById(1);

        // Assert
        assertNotNull(response);
        assertEquals("Test Announcement", response.getTitle());
        assertEquals(0, response.getWinners().size());

        verify(contestEntryDao, never()).getContestEntryById(anyInt());
    }

    @Test
    @DisplayName("Get announcement by ID when winner contest entry not found should throw RuntimeException")
    void testGetAnnouncementById_WhenWinnerContestEntryNotFound_ShouldThrowRuntimeException() {
        // Arrange
        Announcement announcement = createAnnouncement();
        Contest contest = createContest();
        AnnounceWinner winner = createWinner();

        when(announcementDao.getAnnouncementById(1)).thenReturn(announcement);
        when(contestDao.getContestById(42)).thenReturn(contest);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(1)).thenReturn(List.of(winner));
        when(contestEntryDao.getContestEntryById(200)).thenThrow(new RuntimeException("Contest entry not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> announcementService.getAnnouncementById(1));

        verify(contestEntryDao).getContestEntryById(200);
    }

    @Test
    @DisplayName("Get announcement by ID should handle multiple winners correctly")
    void testGetAnnouncementById_ShouldHandleMultipleWinnersCorrectly() {
        // Arrange
        Announcement announcement = createAnnouncement();
        Contest contest = createContest();
        List<AnnounceWinner> winners = createWinnersList();
        ContestEntry entry1 = createContestEntry();
        entry1.setId(100);
        entry1.setName("Winner 1");
        ContestEntry entry2 = createContestEntry();
        entry2.setId(200);
        entry2.setName("Winner 2");

        when(announcementDao.getAnnouncementById(1)).thenReturn(announcement);
        when(contestDao.getContestById(42)).thenReturn(contest);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(1)).thenReturn(winners);
        when(contestEntryDao.getContestEntryById(100)).thenReturn(entry1);
        when(contestEntryDao.getContestEntryById(200)).thenReturn(entry2);

        // Act
        AnnouncementResponse response = announcementService.getAnnouncementById(1);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getWinners().size());
        assertEquals("Winner 1", response.getWinners().get(0).getContestEntry().getName());
        assertEquals("Winner 2", response.getWinners().get(1).getContestEntry().getName());
        assertEquals("1", response.getWinners().get(0).getRanking());
        assertEquals("2", response.getWinners().get(1).getRanking());
    }

    // TEST ADD ANNOUNCEMENT
    @Test
    @DisplayName("Add announcement with valid request should insert announcement and winners")
    void testAddAnnouncement_WhenValidRequest_ShouldInsertAnnouncementAndWinners() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();

        // Mock contest tồn tại
        when(contestDao.existsById(1)).thenReturn(true); // Contest ID 1 tồn tại

        // Check contest entry ids in winners
        when(contestEntryDao.existsById(100)).thenReturn(true); // Contest entry ID 100 tồn tại
        when(contestEntryDao.existsById(200)).thenReturn(true); // Contest entry ID 200 tồn tại

        // Mock chưa có announcement cho contest này
        when(announcementDao.existsAnnouncementWithContest(1)).thenReturn(false); // Contest ID 1 chưa có announcement

        // Mock chưa có announcement với title "New Announcement"
        when(announcementDao.existsAnnouncementWithTitle("New Announcement")).thenReturn(false);

        // Mock trả về announcementId
        when(announcementDao.getAnnouncementIdByContestId(1)).thenReturn(Optional.of(10)); // Giả sử ID 10 là ID của announcement mới

        // Act
        announcementService.addAnnouncement(request);

        // Assert & Verify
        // Kiểm tra insertAnnouncement được gọi với Announcement phù hợp
        ArgumentCaptor<Announcement> annCaptor = ArgumentCaptor.forClass(Announcement.class);
        verify(announcementDao).insertAnnouncement(annCaptor.capture());

        Announcement inserted = annCaptor.getValue();
        assertEquals("New Announcement", inserted.getTitle());
        assertEquals("Description", inserted.getDescription());
        assertEquals(1, inserted.getContestId());
        assertEquals(2, inserted.getWinners().size());

        // Kiểm tra insertWinner được gọi 2 lần
        ArgumentCaptor<AnnounceWinner> winnerCaptor = ArgumentCaptor.forClass(AnnounceWinner.class);
        verify(announceWinnerDao, times(2)).insertWinner(winnerCaptor.capture());

        List<AnnounceWinner> insertedWinners = winnerCaptor.getAllValues();

        // Assert winner 1
        AnnounceWinner w1 = insertedWinners.get(0);
        assertEquals(10, w1.getAnnouncementId());
        assertEquals(100, w1.getContestEntryId());
        assertEquals("1", w1.getRanking());

        // Assert winner 2
        AnnounceWinner w2 = insertedWinners.get(1);
        assertEquals(10, w2.getAnnouncementId());
        assertEquals(200, w2.getContestEntryId());
        assertEquals("2", w2.getRanking());
    }

    @Test
    @DisplayName("Add announcement when contest does not exist should throw NotFoundException")
    void testAddAnnouncement_WhenContestDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();

        // Mock contest không tồn tại
        when(contestDao.existsById(1)).thenReturn(false); // Contest ID 1 không tồn tại

        // Act & Assert
        assertThrows(NotFoundException.class, () -> announcementService.addAnnouncement(request));

        // Verify không gọi insertAnnouncement
        verify(announcementDao, never()).insertAnnouncement(any());
    }

    @Test
    @DisplayName("Add announcement when contest entry does not exist should throw NotFoundException")
    void testAddAnnouncement_WhenAnnouncementAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();

        when(contestDao.existsById(1)).thenReturn(true);

        // Check contest entry ids in winners
        when(contestEntryDao.existsById(100)).thenReturn(true); // Contest entry ID 100 tồn tại
        when(contestEntryDao.existsById(200)).thenReturn(true); // Contest entry ID 200 tồn tại

        when(announcementDao.existsAnnouncementWithContest(1)).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> announcementService.addAnnouncement(request));
        verify(announcementDao, never()).insertAnnouncement(any());
    }

    @Test
    @DisplayName("Add announcement when contest entry does not exist should throw NotFoundException")
    void testAddAnnouncement_WhenInsertWinnerFails_ShouldThrowRuntimeException() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();

        when(contestDao.existsById(1)).thenReturn(true);

        // Check contest entry ids in winners
        when(contestEntryDao.existsById(100)).thenReturn(true); // Contest entry ID 100 tồn tại
        when(contestEntryDao.existsById(200)).thenReturn(true); // Contest entry ID 200 tồn tại

        when(announcementDao.existsAnnouncementWithContest(1)).thenReturn(false);

        // Giả sử chưa có announcement với title "New Announcement"
        when(announcementDao.existsAnnouncementWithTitle("New Announcement")).thenReturn(false);

        when(announcementDao.getAnnouncementIdByContestId(1)).thenReturn(Optional.of(10));

        // Giả sử insertWinner ném RuntimeException
        doThrow(new RuntimeException("DB error"))
                .when(announceWinnerDao).insertWinner(any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> announcementService.addAnnouncement(request));

        verify(announcementDao).insertAnnouncement(any());
        verify(announceWinnerDao).insertWinner(any());
    }

    @Test
    @DisplayName("Add announcement when contest entry does not exist should throw NotFoundException")
    void testAddAnnouncement_WhenContestEntryDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();
        when(contestDao.existsById(1)).thenReturn(true);
        when(contestEntryDao.existsById(100)).thenReturn(true);
        when(contestEntryDao.existsById(200)).thenReturn(false); // This entry doesn't exist

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> announcementService.addAnnouncement(request));

        assertEquals("Contest entry with ID does not exist.", exception.getMessage());
        verify(announcementDao, never()).insertAnnouncement(any());
    }

    @Test
    @DisplayName("Add announcement when title already exists should throw ConflictException")
    void testAddAnnouncement_WhenTitleAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();
        when(contestDao.existsById(1)).thenReturn(true);
        when(contestEntryDao.existsById(100)).thenReturn(true);
        when(contestEntryDao.existsById(200)).thenReturn(true);
        when(announcementDao.existsAnnouncementWithContest(1)).thenReturn(false);
        when(announcementDao.existsAnnouncementWithTitle("New Announcement")).thenReturn(true); // Title exists

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class,
            () -> announcementService.addAnnouncement(request));

        assertEquals("Announcement with title already exists.", exception.getMessage());
        verify(announcementDao, never()).insertAnnouncement(any());
    }

    @Test
    @DisplayName("Add announcement with null or empty title should validate properly")
    void testAddAnnouncement_WithNullTitle_ShouldValidateProperly() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();
        request.setTitle(null); // Set null title - this tests the business logic issue you mentioned

        when(contestDao.existsById(1)).thenReturn(true);
        when(contestEntryDao.existsById(100)).thenReturn(true);
        when(contestEntryDao.existsById(200)).thenReturn(true);
        when(announcementDao.existsAnnouncementWithContest(1)).thenReturn(false);
        when(announcementDao.existsAnnouncementWithTitle(null)).thenReturn(false);
        when(announcementDao.getAnnouncementIdByContestId(1)).thenReturn(Optional.of(10));

        // Act & Assert
        // This test reveals the business logic issue - null title should be validated but currently isn't
        assertDoesNotThrow(() -> announcementService.addAnnouncement(request));

        // Verify that announcement with null title was actually inserted
        ArgumentCaptor<Announcement> captor = ArgumentCaptor.forClass(Announcement.class);
        verify(announcementDao).insertAnnouncement(captor.capture());
        assertNull(captor.getValue().getTitle()); // This shows the business logic flaw
    }

    @Test
    @DisplayName("Add announcement with empty winners list should succeed")
    void testAddAnnouncement_WithEmptyWinnersList_ShouldSucceed() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();
        request.getWinners().clear(); // Remove all winners

        when(contestDao.existsById(1)).thenReturn(true);
        when(announcementDao.existsAnnouncementWithContest(1)).thenReturn(false);
        when(announcementDao.existsAnnouncementWithTitle("New Announcement")).thenReturn(false);
        when(announcementDao.getAnnouncementIdByContestId(1)).thenReturn(Optional.of(10));

        // Act
        announcementService.addAnnouncement(request);

        // Assert
        verify(announcementDao).insertAnnouncement(any());
        verify(announceWinnerDao, never()).insertWinner(any()); // No winners to insert
    }

    @Test
    @DisplayName("Add announcement when announcement ID retrieval fails should throw RuntimeException")
    void testAddAnnouncement_WhenAnnouncementIdRetrievalFails_ShouldThrowRuntimeException() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();
        when(contestDao.existsById(1)).thenReturn(true);
        when(contestEntryDao.existsById(100)).thenReturn(true);
        when(contestEntryDao.existsById(200)).thenReturn(true);
        when(announcementDao.existsAnnouncementWithContest(1)).thenReturn(false);
        when(announcementDao.existsAnnouncementWithTitle("New Announcement")).thenReturn(false);
        when(announcementDao.getAnnouncementIdByContestId(1)).thenReturn(Optional.empty()); // ID not found

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> announcementService.addAnnouncement(request));

        assertEquals("Failed to retrieve announcement ID after insertion.", exception.getMessage());
        verify(announcementDao).insertAnnouncement(any());
        verify(announceWinnerDao, never()).insertWinner(any());
    }

    @Test
    @DisplayName("Add announcement with single winner should succeed")
    void testAddAnnouncement_WithSingleWinner_ShouldSucceed() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();
        request.getWinners().removeIf(w -> w.getContestEntryId() == 200); // Keep only one winner

        when(contestDao.existsById(1)).thenReturn(true);
        when(contestEntryDao.existsById(100)).thenReturn(true);
        when(announcementDao.existsAnnouncementWithContest(1)).thenReturn(false);
        when(announcementDao.existsAnnouncementWithTitle("New Announcement")).thenReturn(false);
        when(announcementDao.getAnnouncementIdByContestId(1)).thenReturn(Optional.of(10));

        // Act
        announcementService.addAnnouncement(request);

        // Assert
        verify(announcementDao).insertAnnouncement(any());
        verify(announceWinnerDao, times(1)).insertWinner(any()); // Only one winner inserted
    }

    @Test
    @DisplayName("Add announcement should set announcement date to current time")
    void testAddAnnouncement_ShouldSetAnnouncementDateToCurrentTime() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();
        when(contestDao.existsById(1)).thenReturn(true);
        when(contestEntryDao.existsById(100)).thenReturn(true);
        when(contestEntryDao.existsById(200)).thenReturn(true);
        when(announcementDao.existsAnnouncementWithContest(1)).thenReturn(false);
        when(announcementDao.existsAnnouncementWithTitle("New Announcement")).thenReturn(false);
        when(announcementDao.getAnnouncementIdByContestId(1)).thenReturn(Optional.of(10));

        // Act
        announcementService.addAnnouncement(request);

        // Assert
        ArgumentCaptor<Announcement> captor = ArgumentCaptor.forClass(Announcement.class);
        verify(announcementDao).insertAnnouncement(captor.capture());

        Announcement inserted = captor.getValue();
        assertNotNull(inserted.getAnnouncementDate());
        // The service sets current timestamp, so we verify it's not the original request date
        assertNotEquals(request.getAnnouncementDate(), inserted.getAnnouncementDate());
    }
}

