package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.ContestDao;
import com.ntn.culinary.dao.ContestImagesDao;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.Contest;
import com.ntn.culinary.model.ContestImages;
import com.ntn.culinary.request.ContestRequest;
import com.ntn.culinary.response.ContestResponse;
import com.ntn.culinary.service.ContestService;

import java.sql.Date;
import java.util.List;

import static com.ntn.culinary.constant.Cloudinary.CLOUDINARY_URL;
import static com.ntn.culinary.utils.ImageUtils.*;

public class ContestServiceImpl implements ContestService {
    private final ContestDao contestDao;
    private final ContestImagesDao contestImagesDao;

    public ContestServiceImpl(ContestDao contestDao, ContestImagesDao contestImagesDao) {
        this.contestDao = contestDao;
        this.contestImagesDao = contestImagesDao;
    }

    @Override
    public List<ContestResponse> getAllContests() {
        return contestDao.getAllContests().stream()
                .map(this::mapContestToResponse)
                .toList();
    }

    @Override
    public ContestResponse getContestById(int id) {
        Contest contest = contestDao.getContestById(id);
        if (contest == null) {
            throw new NotFoundException("Contest not found");
        }
        return mapContestToResponse(contest);
    }

    @Override
    public void addContest(ContestRequest contestRequest) {
        validateContestRequest(contestRequest);

        Contest contest = mapContestRequestToContest(contestRequest);
        contestDao.addContest(contest);

        int contestId = contestDao.getContestIdByHeadline(contestRequest.getHeadline());
        saveContestImages(contestRequest, contestId, false);
    }

    @Override
    public void updateContest(ContestRequest contestRequest) {
        if (!contestDao.existsById(contestRequest.getId())) {
            throw new NotFoundException("Contest does not exist");
        }

        validateContestRequest(contestRequest);

        Contest contest = mapContestRequestToContest(contestRequest);
        contest.setId(contestRequest.getId());
        contestDao.updateContest(contest);

        saveContestImages(contestRequest, contest.getId(), true);
    }

    @Override
    public void updateContestStatus(int id) {
        Contest contest = contestDao.getContestById(id);

        if (contest == null) {
            throw new NotFoundException("Contest not found");
        }

        contestDao.updateContestStatus(id, !contest.isClosed());
    }

    @Override
    public void deleteContest(int id) {
        if (!contestDao.existsById(id)) {
            throw new NotFoundException("Contest does not exist");
        }

        // Xóa ảnh liên quan đến cuộc thi
        List<ContestImages> images = contestImagesDao.getContestImagesByContestId(id);
        images.forEach(image -> {
            if (image.getImagePath() != null) {
                contestImagesDao.deleteContestImageById(image.getId());
                deleteImage(image.getImagePath(), "contests");
            }
        });

        contestDao.deleteContestById(id);
    }

    private void validateContestRequest(ContestRequest contestRequest) {
        if (contestDao.existsByHeadline(contestRequest.getHeadline())) {
            throw new ConflictException("Contest with headline '" + contestRequest.getHeadline() + "' already exists.");
        }
    }

    private Contest mapContestRequestToContest(ContestRequest contestRequest) {
        Contest contest = new Contest();
        contest.setArticleBody(contestRequest.getArticleBody());
        contest.setHeadline(contestRequest.getHeadline());
        contest.setDescription(contestRequest.getDescription());
        Date now = new Date(System.currentTimeMillis());
        contest.setDatePublished(now);
        contest.setDateModified(now);
        contest.setPrize(contestRequest.getPrize());
        contest.setFree(contestRequest.isFree());
        contest.setClosed(false);
        contest.setAccessRole(contestRequest.getAccessRole());
        return contest;
    }

    private void saveContestImages(ContestRequest contestRequest, int contestId, boolean isUpdate) {
        var imageRequests = contestRequest.getContestImages();

        // Nếu không có ảnh nào được gửi lên, không cần xử lý
        if (imageRequests == null || imageRequests.isEmpty()) {
            return;
        }

        // Tạo slug từ tiêu đề cuộc thi
        String slug = slugify(contestRequest.getHeadline());

        // Nếu là cập nhật, xóa tất cả ảnh cũ trong hệ thống và DB
        if (isUpdate) {
            // Xóa tất cả ảnh cũ trong hệ thống và DB
            List<ContestImages> existingImages = contestImagesDao.getContestImagesByContestId(contestId);
            for (ContestImages image : existingImages) {
                if (image.getImagePath() != null) {
                    deleteImage(image.getImagePath(), "contests");
                }
                contestImagesDao.deleteContestImageById(image.getId());
            }
        }

        // Thêm ảnh mới
        for (var imageRequest : imageRequests) {
            String fileName = null;
            if (imageRequest.getImage() != null && imageRequest.getImage().getSize() > 0) {
                fileName = saveImage(imageRequest.getImage(), slug, "contests");
            }
            contestImagesDao.addContestImage(new ContestImages(contestId, fileName));
        }
    }

    private ContestResponse mapContestToResponse(Contest contest) {
        String url = CLOUDINARY_URL + "contests/";

        List<ContestImages> images = contestImagesDao.getContestImagesByContestId(contest.getId())
                .stream()
                .peek(image -> image.setImagePath(url + image.getImagePath()))
                .toList();

        return new ContestResponse(
                contest.getId(),
                contest.getArticleBody(),
                contest.getHeadline(),
                contest.getDescription(),
                contest.getDatePublished(),
                contest.getDateModified(),
                images,
                contest.getAccessRole(),
                contest.getPrize(),
                contest.isFree(),
                contest.isClosed()
        );
    }
}
