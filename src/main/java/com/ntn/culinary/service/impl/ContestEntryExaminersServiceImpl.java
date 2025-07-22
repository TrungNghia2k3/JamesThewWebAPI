package com.ntn.culinary.service.impl;

import com.ntn.culinary.constant.ContestEntryStatusType;
import com.ntn.culinary.dao.ContestEntryDao;
import com.ntn.culinary.dao.ContestEntryExaminersDao;
import com.ntn.culinary.dao.UserDao;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.ContestEntryExaminers;
import com.ntn.culinary.request.ContestEntryExaminersRequest;
import com.ntn.culinary.service.ContestEntryExaminersService;

import java.sql.Timestamp;
import java.util.List;

public class
ContestEntryExaminersServiceImpl implements ContestEntryExaminersService {

    private final ContestEntryDao contestEntryDao;
    private final ContestEntryExaminersDao contestEntryExaminersDao;
    private final UserDao userDao;

    public ContestEntryExaminersServiceImpl(ContestEntryDao contestEntryDao, ContestEntryExaminersDao contestEntryExaminersDao, UserDao userDao) {
        this.contestEntryDao = contestEntryDao;
        this.contestEntryExaminersDao = contestEntryExaminersDao;
        this.userDao = userDao;
    }

    @Override
    public void addExaminer(ContestEntryExaminersRequest request) {
        try {
            // Validate the request before proceeding
            validateRequest(request);

            // Add the examiner to the contest entry
            contestEntryExaminersDao.addContestEntryExaminer(mapRequestToModel(request));

            // Update the contest entry status to REVIEWED
            contestEntryDao.updateContestEntryStatus(request.getContestEntryId(), String.valueOf(ContestEntryStatusType.REVIEWED));
        } catch (Exception e) {
            // Log the error or handle it as needed
            throw new RuntimeException("Error adding examiner: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateExaminer(ContestEntryExaminersRequest request) {
        try {
            // Validate the request before proceeding
            validateRequest(request);

            if (contestEntryExaminersDao.existsById(request.getContestEntryId())) {
                // Update the examiner's details
                contestEntryExaminersDao.updateContestEntryExaminer(mapRequestToModel(request));

                // Update the contest entry status to REVIEWED
                contestEntryDao.updateContestEntryStatus(request.getContestEntryId(), String.valueOf(ContestEntryStatusType.REVIEWED));
            }
        } catch (Exception e) {
            // Log the error or handle it as needed
            throw new RuntimeException("Error updating examiner: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteExaminer(int contestEntryId, int examinerId) {
        if (!contestEntryExaminersDao.existsByContestEntryIdAndExaminerId(contestEntryId, examinerId)) {
            throw new NotFoundException("Examiner not found for the given contest entry");
        }

        contestEntryExaminersDao.deleteByContestEntryIdAndExaminerId(contestEntryId, examinerId);
    }

    @Override
    public ContestEntryExaminers getContestEntryExaminerById(int id) {
        if (!contestEntryExaminersDao.existsById(id)) {
            throw new NotFoundException("Contest entry examiner does not exist");
        }
        return contestEntryExaminersDao.getContestEntryExaminerById(id);
    }

    @Override
    public List<ContestEntryExaminers> getContestEntryExaminersByContestEntryId(int contestEntryId) {
        if (!contestEntryDao.existsById(contestEntryId)) {
            throw new NotFoundException("Contest entry does not exist");
        }
        return contestEntryExaminersDao.getContestEntryExaminersByContestEntryId(contestEntryId);
    }

    @Override
    public List<ContestEntryExaminers> getContestEntryExaminersByExaminerId(int examinerId) {
        if (!userDao.existsById(examinerId)) {
            throw new NotFoundException("Examiner does not exist");
        }
        return contestEntryExaminersDao.getAllContestEntryExaminersByExaminerId(examinerId);
    }

    private ContestEntryExaminers mapRequestToModel(ContestEntryExaminersRequest request) {
        ContestEntryExaminers examiner = new ContestEntryExaminers();
        examiner.setContestEntryId(request.getContestEntryId());
        examiner.setExaminerId(request.getExaminerId());
        examiner.setScore(request.getScore());
        examiner.setFeedback(request.getFeedback());
        examiner.setExamDate(new Timestamp(System.currentTimeMillis()));
        return examiner;
    }

    private void validateRequest(ContestEntryExaminersRequest request) {
        if (!contestEntryDao.existsById(request.getContestEntryId())) {
            throw new NotFoundException("Contest entry does not exist");
        }

        if (!userDao.existsById(request.getExaminerId())) {
            throw new NotFoundException("Examiner does not exist");
        }

        if (contestEntryExaminersDao.existsByContestEntryIdAndExaminerId(request.getContestEntryId(), request.getExaminerId())) {
            throw new ConflictException("Examiner has already reviewed this contest entry");
        }
    }
}
