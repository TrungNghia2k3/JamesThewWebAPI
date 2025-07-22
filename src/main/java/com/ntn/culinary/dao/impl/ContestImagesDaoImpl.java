package com.ntn.culinary.dao.impl;

import com.ntn.culinary.dao.ContestImagesDao;
import com.ntn.culinary.model.ContestImages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.ntn.culinary.utils.DatabaseUtils.getConnection;

public class ContestImagesDaoImpl implements ContestImagesDao {

    @Override
    public List<ContestImages> getContestImagesByContestId(int contestId) {

        String SELECT_CONTEST_BY_CONTEST_ID_QUERY = """
                SELECT * FROM contest_images WHERE contest_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_CONTEST_BY_CONTEST_ID_QUERY)) {

            stmt.setInt(1, contestId);

            List<ContestImages> images = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ContestImages image = new ContestImages();
                    image.setId(rs.getInt("id"));
                    image.setContestId(rs.getInt("contest_id"));
                    image.setImagePath(rs.getString("image_path"));
                    images.add(image);
                }
                return images;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }

    @Override
    public void addContestImage(ContestImages contestImages) {
        String INSERT_CONTEST_IMAGE_QUERY = """
                INSERT INTO contest_images (contest_id, image_path)
                VALUES (?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_CONTEST_IMAGE_QUERY)) {

            stmt.setInt(1, contestImages.getContestId());
            stmt.setString(2, contestImages.getImagePath());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding contest image", e);
        }
    }

    @Override
    public void deleteContestImageById(int id) {
        String DELETE_CONTEST_IMAGE_QUERY = """
                DELETE FROM contest_images WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_CONTEST_IMAGE_QUERY)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting contest image", e);
        }
    }

    @Override
    public boolean existsById(int id) {
        String EXIST_BY_ID_QUERY = "SELECT 1 FROM contest_images WHERE id = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXIST_BY_ID_QUERY)) {

            stmt.setInt(1, id);
            return stmt.executeQuery().next();
        } catch (SQLException ex) {
            throw new RuntimeException("SQLException: " + ex.getMessage());
        }
    }
}
