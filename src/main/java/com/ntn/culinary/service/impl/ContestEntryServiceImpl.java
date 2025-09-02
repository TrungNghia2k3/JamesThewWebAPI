package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.*;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.ContestEntry;
import com.ntn.culinary.model.ContestEntryInstruction;
import com.ntn.culinary.request.ContestEntryRequest;
import com.ntn.culinary.request.DeleteContestEntryRequest;
import com.ntn.culinary.response.ContestEntryResponse;
import com.ntn.culinary.service.ContestEntryService;
import com.ntn.culinary.service.ImageService;

import javax.servlet.http.Part;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ntn.culinary.constant.Cloudinary.CLOUDINARY_URL;
import static com.ntn.culinary.utils.StringUtils.slugify;

public class ContestEntryServiceImpl implements ContestEntryService {
    private final ContestEntryDao contestEntryDao;
    private final ContestEntryInstructionsDao contestEntryInstructionsDao;
    private final UserDao userDao;
    private final CategoryDao categoryDao;
    private final AreaDao areaDao;
    private final ContestDao contestDao;
    private final ImageService imageService;

    public ContestEntryServiceImpl(ContestEntryDao contestEntryDao, ContestEntryInstructionsDao contestEntryInstructionsDao, UserDao userDao, CategoryDao categoryDao, AreaDao areaDao, ContestDao contestDao, ImageService imageService) {
        this.contestEntryDao = contestEntryDao;
        this.contestEntryInstructionsDao = contestEntryInstructionsDao;
        this.userDao = userDao;
        this.categoryDao = categoryDao;
        this.areaDao = areaDao;
        this.contestDao = contestDao;
        this.imageService = imageService;
    }

    @Override
    public void addContestEntry(ContestEntryRequest contestEntryRequest, Part imagePart) {

        // Validate the contest entry request
        validateContestEntryRequest(contestEntryRequest, false);

        if (contestEntryDao.existsByUserIdAndContestIdAndName(contestEntryRequest.getUserId(), contestEntryRequest.getContestId(), contestEntryRequest.getName()))
            throw new ConflictException("Contest entry with the same name already exists for this user and contest.");

        if (imagePart != null && imagePart.getSize() > 0) {
            String slug = slugify(contestEntryRequest.getName());
            String fileName = imageService.uploadImage(imagePart, slug, "contest_entries");
            contestEntryRequest.setImage(fileName);
        }

        // Map the request to a ContestEntry model
        ContestEntry contestEntry = mapRequestToContestEntry(contestEntryRequest);

        // insert contest entry
        contestEntryDao.addContestEntry(contestEntry);

        // get the contest entry ID
        int contestEntryId = contestEntryDao.getContestEntryIdByUserIdAndContestIdAndName(contestEntryRequest.getUserId(), contestEntryRequest.getContestId(), contestEntryRequest.getName());

        // insert contest entry instructions
        for (ContestEntryInstruction instructions : contestEntry.getContestEntryInstructions()) {

            contestEntryInstructionsDao.addContestEntryInstructions(
                    new ContestEntryInstruction(
                            contestEntryId,
                            instructions.getStepNumber(),
                            instructions.getName(),
                            instructions.getText(),
                            instructions.getImage()
                    )
            );
        }
    }

    @Override
    public void updateContestEntry(ContestEntryRequest contestEntryRequest, Part imagePart) {
        // Validate request
        validateContestEntryRequest(contestEntryRequest, true);

        // Tìm contest entry hiện tại
        ContestEntry existing = contestEntryDao.getContestEntryByUserIdAndContestIdAndName(
                contestEntryRequest.getUserId(),
                contestEntryRequest.getContestId(),
                contestEntryRequest.getName()
        );

        if (existing == null) {
            throw new NotFoundException("Contest entry not found.");
        }

        // Xử lý ảnh nếu có ảnh mới
        String fileName = existing.getImage();
        if (imagePart != null && imagePart.getSize() > 0) {
            if (fileName != null) {
                imageService.deleteImage(fileName, "contest_entries");
            }
            String slug = slugify(contestEntryRequest.getName());
            fileName = imageService.uploadImage(imagePart, slug, "contest_entries");
        }

        // Map thông tin từ request sang model
        ContestEntry updatedEntry = mapRequestToContestEntry(contestEntryRequest);
        updatedEntry.setId(existing.getId()); // cần thiết để DAO biết update cái nào

        // Cập nhật contest entry chính
        contestEntryDao.updateContestEntry(updatedEntry, fileName);

        // ======= XỬ LÝ DANH SÁCH INSTRUCTIONS =======
        List<ContestEntryInstruction> clientList = updatedEntry.getContestEntryInstructions();
        List<ContestEntryInstruction> dbList = contestEntryInstructionsDao.getContestEntryInstructionsByContestEntryId(existing.getId());

        // Map DB instructions by ID
        Map<Integer, ContestEntryInstruction> dbMap = dbList.stream()
                .collect(Collectors.toMap(ContestEntryInstruction::getId, i -> i));

        // Track ID client gửi lên
        Set<Integer> clientIds = new HashSet<>();

        for (ContestEntryInstruction instruction : clientList) {
            instruction.setContestEntryId(existing.getId());

            int id = instruction.getId(); // vẫn là int

            if (id <= 0 || !dbMap.containsKey(id)) {
                // id <= 0 (ví dụ -1) hoặc không tồn tại trong DB ⇒ thêm mới
                contestEntryInstructionsDao.addContestEntryInstructions(instruction);
            } else {
                // tồn tại trong DB ⇒ cập nhật
                contestEntryInstructionsDao.updateContestEntryInstructions(instruction);
                clientIds.add(id);
            }
        }

        // XÓA những cái không nằm trong danh sách client gửi lên
        for (ContestEntryInstruction dbInstruction : dbList) {
            if (!clientIds.contains(dbInstruction.getId())) {
                contestEntryInstructionsDao.deleteContestEntryInstructionById(dbInstruction.getId());
            }
        }

    }

    @Override
    public void deleteContestEntry(DeleteContestEntryRequest request) {
        // Check if the contest entry exists
        if (!contestEntryDao.existsByUserIdAndContestIdAndName(request.getUserId(), request.getContestId(), request.getName())) {
            throw new NotFoundException("Contest entry with the specified user ID, contest ID, and name does not exist.");
        }

        // Get the contest entry
        ContestEntry contestEntry = contestEntryDao.getContestEntryByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName());

        if (contestEntry == null) {
            throw new NotFoundException("Contest entry not found.");
        }

        // Delete contest entry instructions
        List<ContestEntryInstruction> instructions =
                contestEntryInstructionsDao.getContestEntryInstructionsByContestEntryId(contestEntry.getId());
        for (ContestEntryInstruction instruction : instructions) {
            contestEntryInstructionsDao.deleteContestEntryInstructionById(instruction.getId());
        }

        // Delete the image if it exists
        if (contestEntry.getImage() != null) {
            imageService.deleteImage(contestEntry.getImage(), "contest_entries");
        }

        // Delete the contest entry
        contestEntryDao.deleteContestEntryByUserIdAndContestIdAndName(
                request.getUserId(), request.getContestId(), request.getName());
    }

    @Override
    public ContestEntryResponse getContestEntryByUserIdAndContestId(int userId, int contestId) {
        ContestEntry contestEntry = contestEntryDao.getContestEntryByUserIdAndContestId(userId, contestId);
        if (contestEntry == null) {
            throw new NotFoundException("Contest entry not found for the specified user ID and contest ID.");
        }
        return mapContestEntryToResponse(contestEntry);
    }

    @Override
    public ContestEntryResponse getContestEntryById(int id) {
        ContestEntry contestEntry = contestEntryDao.getContestEntryById(id);
        if (contestEntry == null) {
            throw new NotFoundException("Contest entry with the specified ID does not exist.");
        }
        return mapContestEntryToResponse(contestEntry);
    }

    @Override
    public List<ContestEntryResponse> getContestEntriesByContestId(int contestId) {
        List<ContestEntry> contestEntries = contestEntryDao.getContestEntryByContestId(contestId);
        if (contestEntries.isEmpty()) {
            throw new NotFoundException("No contest entries found for the specified contest ID.");
        }
        return contestEntries.stream().map(this::mapContestEntryToResponse).toList();
    }

    @Override
    public List<ContestEntryResponse> getContestEntriesByUserId(int userId) {
        List<ContestEntry> contestEntries = contestEntryDao.getContestEntriesByUserId(userId);
        if (contestEntries.isEmpty()) {
            throw new NotFoundException("No contest entries found for the specified user ID.");
        }
        return contestEntries.stream().map(this::mapContestEntryToResponse).toList();
    }

    private void validateContestEntryRequest(ContestEntryRequest request, boolean isUpdate) {

        if (!userDao.existsById(request.getUserId())) {
            throw new NotFoundException("User with ID does not exist.");
        }

        if (!contestDao.existsById(request.getContestId())) {
            throw new NotFoundException("Contest with ID does not exist.");
        }

        if (!categoryDao.existsByName(request.getCategory())) {
            throw new NotFoundException("Category does not exist.");
        }

        if (!areaDao.existsByName(request.getArea())) {
            throw new NotFoundException("Area does not exist.");
        }

        if (contestDao.isContestClosed(request.getContestId())) {
            throw new ConflictException("Cannot update contest entry as the contest is closed.");
        }

        // CREATE
        if (!isUpdate && contestEntryDao.existsByNameAndContestId(request.getName(), request.getContestId())) {
            throw new ConflictException("Contest entry with the same name already exists for this contest.");
        }

        // UPDATE
        if (isUpdate && !contestEntryDao.existsContestEntryWithNameExcludingId(
                request.getName(), request.getContestId(), request.getId())) {
            throw new ConflictException("Contest entry with the same name already exists for this contest, excluding the current entry.");
        }
    }

    private ContestEntry mapRequestToContestEntry(ContestEntryRequest request) {
        ContestEntry contestEntry = new ContestEntry();
        contestEntry.setContestId(request.getContestId());
        contestEntry.setUserId(request.getUserId());
        contestEntry.setName(request.getName());
        contestEntry.setIngredients(request.getIngredients());
        contestEntry.setInstructions(request.getInstructions());
        contestEntry.setImage(request.getImage());
        contestEntry.setPrepareTime(request.getPrepareTime());
        contestEntry.setCookingTime(request.getCookingTime());
        contestEntry.setYield(request.getYield());
        contestEntry.setCategory(request.getCategory());
        contestEntry.setArea(request.getArea());
        contestEntry.setShortDescription(request.getShortDescription());
        contestEntry.setDateCreated(new Date(System.currentTimeMillis()));
        contestEntry.setDateModified(new Date(System.currentTimeMillis()));
        contestEntry.setStatus("PENDING");
        contestEntry.setContestEntryInstructions(request.getContestEntryInstructions());

        return contestEntry;
    }

    private ContestEntryResponse mapContestEntryToResponse(ContestEntry contestEntry) {

        String imageUrl = CLOUDINARY_URL + "contest_entry/" + contestEntry.getImage();

        ContestEntryResponse response = new ContestEntryResponse();
        response.setId(contestEntry.getId());
        response.setContestId(contestEntry.getContestId());
        response.setUserId(contestEntry.getUserId());
        response.setName(contestEntry.getName());
        response.setIngredients(contestEntry.getIngredients());
        response.setInstructions(contestEntry.getInstructions());
        response.setImage(imageUrl);
        response.setPrepareTime(contestEntry.getPrepareTime());
        response.setCookingTime(contestEntry.getCookingTime());
        response.setYield(contestEntry.getYield());
        response.setCategory(contestEntry.getCategory());
        response.setArea(contestEntry.getArea());
        response.setShortDescription(contestEntry.getShortDescription());
        response.setDateCreated(contestEntry.getDateCreated());
        response.setDateModified(contestEntry.getDateModified());
        response.setStatus(contestEntry.getStatus());
        response.setContestEntryInstructions(contestEntryInstructionsDao.getContestEntryInstructionsByContestEntryId(contestEntry.getId()));

        return response;
    }
}
