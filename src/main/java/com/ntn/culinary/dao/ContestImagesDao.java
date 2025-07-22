package com.ntn.culinary.dao;

import com.ntn.culinary.model.ContestImages;

import java.util.List;

public interface ContestImagesDao {
    List<ContestImages> getContestImagesByContestId(int id);

    void addContestImage(ContestImages contestImages);

    void deleteContestImageById(int id);

    boolean existsById(int id);
}
