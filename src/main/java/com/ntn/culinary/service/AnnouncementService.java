package com.ntn.culinary.service;

import com.ntn.culinary.request.AnnouncementRequest;
import com.ntn.culinary.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {
    List<AnnouncementResponse> getAllAnnouncements();

    AnnouncementResponse getAnnouncementById(int announcementId);

    void addAnnouncement(AnnouncementRequest announcementRequest);

    void updateAnnouncement(AnnouncementRequest announcementRequest);

    void deleteAnnouncement(int announcementId);
}
