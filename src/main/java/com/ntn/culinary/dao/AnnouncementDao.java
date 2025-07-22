package com.ntn.culinary.dao;

import com.ntn.culinary.model.Announcement;

import java.util.List;
import java.util.Optional;

public interface AnnouncementDao {
    /**
     * Inserts a new announcement into the data store.
     *
     * @param announcement the announcement to insert
     */
    void insertAnnouncement(Announcement announcement);

    /**
     * Checks if an announcement exists for the specified contest.
     *
     * @param contestId the contest ID to check
     * @return true if an announcement exists, false otherwise
     */
    boolean existsAnnouncementWithContest(int contestId);

    /**
     * Checks if an announcement exists with the specified ID.
     *
     * @param id the announcement ID to check
     * @return true if the announcement exists, false otherwise
     */
    boolean existsAnnouncementById(int id);

    /**
     * Retrieves the announcement ID associated with the given contest ID.
     *
     * @param contestId the contest ID
     * @return an Optional containing the announcement ID if found, or empty otherwise
     */
    Optional<Integer> getAnnouncementIdByContestId(int contestId);

    /**
     * Retrieves all announcements.
     *
     * @return a list of all announcements
     */
    List<Announcement> getAllAnnouncements();

    /**
     * Retrieves an announcement by its ID.
     *
     * @param id the announcement ID
     * @return the announcement with the specified ID
     */
    Announcement getAnnouncementById(int id);

    /**
     * Updates an existing announcement.
     *
     * @param announcement the announcement to update
     */
    void updateAnnouncement(Announcement announcement);

    /**
     * Deletes an announcement by its ID.
     *
     * @param id the announcement ID to delete
     */
    void deleteAnnouncementById(int id);

    /**
     * Checks if an announcement exists with the specified title.
     *
     * @param title the title of the announcement
     * @return true if an announcement with the given title exists, false otherwise
     */
    boolean existsAnnouncementWithTitle(String title);

    /**
     * Checks if an announcement with the given title exists, excluding the announcement with the specified ID.
     *
     * @param id    the ID of the announcement to exclude from the check
     * @param title the title to check for existence
     * @return true if another announcement with the given title exists, false otherwise
     */
    boolean existsAnnouncementWithTitleExcludingId(int id, String title);
}
