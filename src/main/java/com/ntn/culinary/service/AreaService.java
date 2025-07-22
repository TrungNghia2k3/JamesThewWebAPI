package com.ntn.culinary.service;

import com.ntn.culinary.request.AreaRequest;
import com.ntn.culinary.response.AreaResponse;

import java.util.List;

public interface AreaService {
    /**
         * Adds a new area with the specified name.
         *
         * @param areaRequest the request object containing area information
         */
        void addArea(AreaRequest areaRequest);

        /**
         * Updates an existing area with the provided request data.
         *
         * @param areaRequest the request object containing updated area information
         */
        void updateArea(AreaRequest areaRequest);

        /**
         * Retrieves a list of all areas.
         *
         * @return a list of area responses
         */
        List<AreaResponse> getAllAreas();

        /**
         * Retrieves an area by its unique identifier.
         *
         * @param id the unique identifier of the area
         * @return the area response, or null if not found
         */
        AreaResponse getAreaById(int id);

        /**
         * Retrieves an area by its name.
         *
         * @param name the name of the area
         * @return the area response, or null if not found
         */
        AreaResponse getAreaByName(String name);

        /**
         * Deletes an area by its unique identifier.
         *
         * @param id the unique identifier of the area to delete
         */
        void deleteAreaById(int id);
}
