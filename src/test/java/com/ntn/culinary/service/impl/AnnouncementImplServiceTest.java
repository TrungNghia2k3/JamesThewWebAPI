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
import org.junit.jupiter.api.Disabled;
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
    @Disabled
    @DisplayName("Get all announcements - when announcements exist, should return announcement responses")
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
    @DisplayName("Get all announcements - when no announcements exist, should return empty list")
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
    @DisplayName("Get all announcements - when contest entry not found, should throw RuntimeException")
    void testGetAllAnnouncements_WhenContestEntryNotFound_ThrowsRuntimeException() {
        // Arrange
        Announcement announcement = createAnnouncement();
        Contest contest = createContest();
        AnnounceWinner winner = createWinner();

        when(announcementDao.getAllAnnouncements()).thenReturn(List.of(announcement));
        when(contestDao.getContestById(42)).thenReturn(contest);
        when(announceWinnerDao.getAllWinnersByAnnouncementId(1)).thenReturn(List.of(winner));
        when(contestEntryDao.getContestEntryById(200)).thenThrow(new RuntimeException("ContestEntry not found"));

        // Act
        assertThrows(RuntimeException.class, () -> announcementService.getAllAnnouncements());

        // Assert
        verify(announcementDao).getAllAnnouncements();
        verify(contestDao).getContestById(42);
        verify(announceWinnerDao).getAllWinnersByAnnouncementId(1);
        verify(contestEntryDao).getContestEntryById(200);
    }

    @Test
    @DisplayName("Get all announcements, when announcement exists, should return announcement responses")
    @Disabled
    void testGetAllAnnouncements_WhenAnnouncementsExist_ReturnsAnnouncementResponses_() {
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

    // TEST ADD ANNOUNCEMENT
    @Test
    @Disabled
    @DisplayName("Add announcement - when valid request, should insert announcement and winners")
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
    @DisplayName("Add announcement - when request is null, should throw NotFoundException")
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
    @DisplayName("Add announcement - when contest entry does not exist, should throw ConflictException")
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
    @DisplayName("Add announcement - when announcement with title already exists, should throw RuntimeException")
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


    // TEST UPDATE ANNOUNCEMENT
    @Test
    @Disabled
    void testUpdateAnnouncement_WhenValidRequest_ShouldUpdateAnnouncementAndWinners() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();
        request.setId(1); // Set ID for update

        Announcement existingAnnouncement = createAnnouncement();
        existingAnnouncement.setId(1);

        when(announcementDao.getAnnouncementById(1)).thenReturn(existingAnnouncement);
        when(contestDao.existsById(1)).thenReturn(true);
        when(contestEntryDao.existsById(100)).thenReturn(true);
        when(contestEntryDao.existsById(200)).thenReturn(true);

        // Act
        announcementService.updateAnnouncement(request);

        // Assert
        ArgumentCaptor<Announcement> annCaptor = ArgumentCaptor.forClass(Announcement.class);
        verify(announcementDao).updateAnnouncement(annCaptor.capture());

        Announcement updated = annCaptor.getValue();
        assertEquals("New Announcement", updated.getTitle());
        assertEquals("Description", updated.getDescription());
        assertEquals(1, updated.getContestId());

        ArgumentCaptor<AnnounceWinner> winnerCaptor = ArgumentCaptor.forClass(AnnounceWinner.class);
        verify(announceWinnerDao, times(2)).updateWinner(winnerCaptor.capture());

        List<AnnounceWinner> updatedWinners = winnerCaptor.getAllValues();
        assertEquals("1", updatedWinners.get(0).getRanking());
        assertEquals("2", updatedWinners.get(1).getRanking());
    }

    @Test
    void testUpdateAnnouncement_WhenContestDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        AnnouncementRequest request = createAnnouncementRequest();

        when(contestDao.existsById(1)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> announcementService.updateAnnouncement(request));

        verify(announcementDao, never()).updateAnnouncement(any());
    }
}