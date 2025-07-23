package com.ntn.culinary.dao;

import com.ntn.culinary.model.Area;

import java.util.List;

public interface AreaDao {

    /**
     * Checks if an area exists by its name.
     *
     * @param name the name of the area
     * @return true if the area exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Inserts a new area with the specified name.
     *
     * @param name the name of the area to insert
     */
    void insertArea(String name);

    /**
     * Updates the details of an existing area.
     *
     * @param area the area object containing updated information
     */
    void updateArea(Area area);

    /**
     * Retrieves a list of all areas.
     *
     * @return a list of all areas
     */
    List<Area> getAllAreas();

    /**
     * Retrieves an area by its unique identifier.
     *
     * @param id the id of the area
     * @return the area with the specified id, or null if not found
     */
    Area getAreaById(int id);

    /**
     * Deletes an area by its unique identifier.
     *
     * @param id the id of the area to delete
     */
    void deleteAreaById(int id);

    /**
     * Checks if an area exists by its unique identifier.
     *
     * @param id the id of the area
     * @return true if the area exists, false otherwise
     */
    boolean existsById(int id);

    /**
     * Retrieves an area by its name.
     *
     * @param name the name of the area
     * @return the area with the specified name, or null if not found
     */
    Area getAreaByName(String name);

    /**
     * Checks if an area with the specified name exists, excluding a specific id.
     *
     * @param id   the id to exclude from the check
     * @param name the name of the area to check
     * @return true if an area with the specified name exists excluding the given id, false otherwise
     */
    boolean existsAreaWithNameExcludingId(int id, String name);
}
