package com.ntn.culinary.service.impl;

import com.ntn.culinary.dao.AreaDao;
import com.ntn.culinary.exception.ConflictException;
import com.ntn.culinary.exception.NotFoundException;
import com.ntn.culinary.model.Area;
import com.ntn.culinary.request.AreaRequest;
import com.ntn.culinary.response.AreaResponse;
import com.ntn.culinary.service.AreaService;

import java.util.List;

public class AreaServiceImpl implements AreaService {

    private final AreaDao areaDao;

    public AreaServiceImpl(AreaDao areaDao) {
        this.areaDao = areaDao;
    }

    @Override
    public void addArea(AreaRequest areaRequest) {
        validateAreaRequest(areaRequest, false);
        areaDao.insertArea(areaRequest.getName());
    }

    @Override
    public void updateArea(AreaRequest areaRequest) {
        validateAreaRequest(areaRequest, true);
        areaDao.updateArea(mapAreaRequestToArea(areaRequest));

    }

    @Override
    public List<AreaResponse> getAllAreas() {
        return areaDao.getAllAreas().stream()
                .map(
                        area -> new AreaResponse(
                                area.getId(),
                                area.getName()
                        ))
                .toList();
    }

    @Override
    public AreaResponse getAreaById(int id) {
        if (areaDao.existsById(id)) {
            Area area = areaDao.getAreaById(id);
            return new AreaResponse(area.getId(), area.getName());
        } else {
            throw new NotFoundException("Area with ID does not exist.");
        }
    }

    @Override
    public AreaResponse getAreaByName(String name) {
        Area area = areaDao.getAreaByName(name);
        if (area != null) {
            return new AreaResponse(area.getId(), area.getName());
        } else {
            throw new NotFoundException("Area with name does not exist.");
        }
    }

    @Override
    public void deleteAreaById(int id) {
        if (areaDao.existsById(id)) {
            areaDao.deleteAreaById(id);
        } else {
            throw new NotFoundException("Area with ID does not exist.");
        }
    }

    private Area mapAreaRequestToArea(AreaRequest areaRequest) {
        Area area = new Area();
        area.setId(areaRequest.getId());
        area.setName(areaRequest.getName());
        return area;
    }

    private void validateAreaRequest(AreaRequest areaRequest, boolean isUpdate) {
        // CREATE
        if (!isUpdate && areaDao.existsByName(areaRequest.getName())) {
            throw new ConflictException("Area with name already exists.");
        }

        // UPDATE
        if (isUpdate && !areaDao.existsById(areaRequest.getId())) {
            throw new NotFoundException("Area with ID does not exist.");
        }

        if (isUpdate && areaDao.existsAreaWithNameExcludingId(areaRequest.getId(), areaRequest.getName())) {
            throw new ConflictException("Area with name already exists.");
        }
    }
}
