package com.ntn.culinary.dao;

import com.ntn.culinary.model.AnnounceWinner;
import com.ntn.culinary.model.Announcement;

import java.util.List;

public interface AnnounceWinnerDao {
    /**
         * Inserts a new winner into the data store.
         *
         * @param announceWinner the winner to insert
         */
        void insertWinner(AnnounceWinner announceWinner);

        /**
         * Checks if a winner exists for the given announcement and contest entry.
         *
         * @param announcementId the ID of the announcement
         * @param contestEntryId the ID of the contest entry
         * @return true if the winner exists, false otherwise
         */
        boolean existsWinner(int announcementId, int contestEntryId);

        /**
         * Updates the information of an existing winner.
         *
         * @param announceWinner the winner with updated information
         */
        void updateWinner(AnnounceWinner announceWinner);

        /**
         * Deletes a winner based on announcement and contest entry IDs.
         *
         * @param announcementId the ID of the announcement
         * @param contestEntryId the ID of the contest entry
         */
        void deleteWinner(int announcementId, int contestEntryId);

        /**
         * Retrieves all winners for a specific announcement.
         *
         * @param announcementId the ID of the announcement
         * @return a list of winners for the announcement
         */
        List<AnnounceWinner> getAllWinnersByAnnouncementId(int announcementId);
}
