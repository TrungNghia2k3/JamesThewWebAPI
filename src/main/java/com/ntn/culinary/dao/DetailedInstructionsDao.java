package com.ntn.culinary.dao;

import com.ntn.culinary.model.DetailedInstructions;

import java.util.List;
import java.util.Map;

public interface DetailedInstructionsDao {
    List<DetailedInstructions> getDetailedInstructionsByRecipeId(int recipeId);

    void addDetailedInstructions(DetailedInstructions detailedInstructions);

    void deleteDetailedInstructionsById(int id);

    void updateDetailedInstructions(DetailedInstructions detailedInstructions);

    boolean existsById(int id);

    // Improve (Batch retrieval)
    public Map<Integer, List<DetailedInstructions>> getDetailedInstructionsByRecipeIds(List<Integer> recipeIds);
}
