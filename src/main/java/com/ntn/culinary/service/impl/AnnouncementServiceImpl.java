package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.AnnounceWinnerDao;
import com.ntn.culinary.dao.AnnouncementDao;
import com.ntn.culinary.dao.ContestDao;
import com.ntn.culinary.dao.ContestEntryDao;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.AnnounceWinner;
import com.ntn.culinary.model.Announcement;
import com.ntn.culinary.request.AnnouncementRequest;
import com.ntn.culinary.response.AnnounceWinnerResponse;
import com.ntn.culinary.response.AnnouncementResponse;
import com.ntn.culinary.service.AnnouncementService;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class AnnouncementServiceImpl implements AnnouncementService {

    private final ContestDao contestDao;
    private final AnnouncementDao announcementDao;
    private final AnnounceWinnerDao announceWinnerDao;
    private final ContestEntryDao contestEntryDao;

    public AnnouncementServiceImpl(ContestDao contestDao, AnnouncementDao announcementDao, AnnounceWinnerDao announceWinnerDao, ContestEntryDao contestEntryDao) {
        this.contestDao = contestDao;
        this.announcementDao = announcementDao;
        this.announceWinnerDao = announceWinnerDao;
        this.contestEntryDao = contestEntryDao;
    }

    @Override
    public List<AnnouncementResponse> getAllAnnouncements() {
        // Fetch all announcements from the database
        return announcementDao.getAllAnnouncements().stream()
                .map(this::mapAnnouncementToResponse)
                .toList();
    }

    @Override
    public AnnouncementResponse getAnnouncementById(int announcementId) {
        // Fetch the announcement by contest ID
        Announcement announcement = announcementDao.getAnnouncementById(announcementId);

        // If the announcement does not exist, throw a NotFoundException
        if (announcement == null) {
            throw new NotFoundException("Announcement with ID does not exist.");
        }

        return mapAnnouncementToResponse(announcement);
    }

    @Override
    public void addAnnouncement(AnnouncementRequest announcementRequest) {

        // Validate the announcement request
        validateAnnouncementRequest(announcementRequest, false);

        // Create a new Announcement object from the request
        Announcement announcement = mapRequestToAnnouncement(announcementRequest);

        // Insert the announcement into the database
        announcementDao.insertAnnouncement(announcement);

        // Lấy id của announcement vừa được thêm
        Integer announcementId = announcementDao
                .getAnnouncementIdByContestId(announcementRequest.getContestId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve announcement ID after insertion."));

        // Insert winners into the database
        for (AnnounceWinner winner : announcement.getWinners()) {
            announceWinnerDao.insertWinner(
                    new AnnounceWinner(
                            announcementId,
                            winner.getContestEntryId(),
                            winner.getRanking()
                    )
            );
        }
    }

    @Override
    public void updateAnnouncement(AnnouncementRequest announcementRequest) {
        // Validate the announcement request
        validateAnnouncementRequest(announcementRequest, true);

        // Create a new Announcement object from the request
        Announcement announcement = mapRequestToAnnouncement(announcementRequest);

        // Check if the announcement exists
        announcementDao.updateAnnouncement(announcement);

        // Lấy list announce winners từ request và từ database
        List<AnnounceWinner> requestList = announcementRequest.getWinners();
        List<AnnounceWinner> dbList = announceWinnerDao.getAllWinnersByAnnouncementId(announcement.getId());

        // Convert request and db lists to maps for easier comparison
        Map<Integer, AnnounceWinner> requestMap = requestList.stream()
                .collect(java.util.stream.Collectors.toMap(AnnounceWinner::getContestEntryId, w -> w));
        Map<Integer, AnnounceWinner> dbMap = dbList.stream()
                .collect(java.util.stream.Collectors.toMap(AnnounceWinner::getContestEntryId, w -> w));

        // Compare and update winners
        // 1. Nếu winner không có trong db thì thêm mớ
        // 2. Nếu winner có trong db nhưng ranking khác thì cập nhật
        // 3. Nếu winner có trong db và ranking giống thì không làm gì
        // 4. Nếu winner có trong db nhưng không có trong request thì xóa khỏi db

        // Insert or update winners
        for (AnnounceWinner winner : requestList) {
            AnnounceWinner dbWinner = dbMap.get(winner.getContestEntryId());
            if (dbWinner == null) {
                announceWinnerDao.insertWinner(new AnnounceWinner(announcement.getId(), winner.getContestEntryId(), winner.getRanking()));
            } else if (!dbWinner.getRanking().equals(winner.getRanking())) {
                announceWinnerDao.updateWinner(new AnnounceWinner(announcement.getId(), winner.getContestEntryId(), winner.getRanking()));
            }
        }

        // Delete winners not present in request
        for (AnnounceWinner dbWinner : dbList) {
            if (!requestMap.containsKey(dbWinner.getContestEntryId())) {
                announceWinnerDao.deleteWinner(announcement.getId(), dbWinner.getContestEntryId());
            }
        }
    }

    @Override
    public void deleteAnnouncement(int announcementId) {
        // Check if the announcement exists
        if (announcementDao.existsAnnouncementById(announcementId)) {
            // Delete winners associated with the announcement
            List<AnnounceWinner> winners = announceWinnerDao.getAllWinnersByAnnouncementId(announcementId);
            if (!winners.isEmpty()) {
                winners.forEach(winner -> announceWinnerDao.deleteWinner(announcementId, winner.getContestEntryId()));
            }

            // Delete the announcement from the database
            announcementDao.deleteAnnouncementById(announcementId);
        } else {
            throw new NotFoundException("Announcement with ID does not exist.");
        }
    }

    private Announcement mapRequestToAnnouncement(AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setId(request.getId());
        announcement.setTitle(request.getTitle());
        announcement.setAnnouncementDate(new Date(System.currentTimeMillis()));
        announcement.setDescription(request.getDescription());
        announcement.setContestId(request.getContestId());
        announcement.setWinners(request.getWinners());

        return announcement;
    }

    private AnnouncementResponse mapAnnouncementToResponse(Announcement announcement) {
        AnnouncementResponse response = new AnnouncementResponse();
        response.setId(announcement.getId());
        response.setTitle(announcement.getTitle());
        response.setAnnouncementDate(announcement.getAnnouncementDate());
        response.setDescription(announcement.getDescription());
        response.setContest(contestDao.getContestById(announcement.getContestId())); // Get contest by ID
        response.setWinners(
                announceWinnerDao.getAllWinnersByAnnouncementId(announcement.getId())// Get winners from announceWinnerDao
                        .stream()
                        .map(winner ->
                                new AnnounceWinnerResponse(
                                        winner.getId(),
                                        contestEntryDao.getContestEntryById(winner.getContestEntryId()), // Get contest entry by ID
                                        winner.getRanking()))
                        .toList()
        );

        return response;
    }

    private void validateAnnouncementRequest(AnnouncementRequest request, boolean isUpdate) {
        // Kiểm tra
        // - Contest id phải tồn tại
        // - Contest entry id phải tồn tại
        // - Nếu là thêm mới, kiểm tra xem đã có announcement nào cho contest này chưa
        // - Nếu là thêm mới, kiểm tra xem title đã tồn tại chưa
        // - Nếu là cập nhật, kiểm tra xem announcement có tồn tại không
        // - Nếu là cập nhật, kiểm tra xem title đã tồn tại cho announcement khác chưa

        // Check if contest ID exists
        if (!contestDao.existsById(request.getContestId())) {
            throw new NotFoundException("Contest with ID does not exist.");
        }

        // Check if all contest entry IDs in winners exist
        for (AnnounceWinner winner : request.getWinners()) {
            if (!contestEntryDao.existsById(winner.getContestEntryId())) {
                throw new NotFoundException("Contest entry with ID does not exist.");
            }
        }

        // If adding a new announcement, check if an announcement already exists for the contest
        if (!isUpdate && announcementDao.existsAnnouncementWithContest(request.getContestId())) {
            throw new ConflictException("Announcement already exists");
        }

        // If adding a new announcement, check if the title already exists
        if (!isUpdate && announcementDao.existsAnnouncementWithTitle(request.getTitle())) {
            throw new ConflictException("Announcement with title already exists.");
        }

        // If updating, check if the announcement exists
        if (isUpdate && !announcementDao.existsAnnouncementById(request.getId())) {
            throw new NotFoundException("Announcement with ID does not exist.");
        }

        // If updating, check if the title already exists for another announcement
        if (isUpdate && announcementDao.existsAnnouncementWithTitleExcludingId(request.getId(), request.getTitle())) {
            throw new ConflictException("Announcement with title already exists, excluding the current announcement.");
        }
    }
}
