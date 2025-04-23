package dao;

import models.CourseMaterial;
import db.DatabaseSetup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {
    public boolean createMaterial(CourseMaterial material) {
        String sql = "INSERT INTO course_materials (course_id, title, content, material_type) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, material.getCourseId());
            pstmt.setString(2, material.getTitle());
            pstmt.setString(3, material.getContent());
            pstmt.setString(4, material.getMaterialType());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        material.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating course material: " + e.getMessage());
            return false;
        }
    }

    public List<CourseMaterial> getCourseMaterials(int courseId) {
        List<CourseMaterial> materials = new ArrayList<>();
        String sql = "SELECT * FROM course_materials WHERE course_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CourseMaterial material = new CourseMaterial(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("material_type")
                );
                materials.add(material);
            }
        } catch (SQLException e) {
            System.err.println("Error getting course materials: " + e.getMessage());
        }
        return materials;
    }

////    public boolean deleteMaterial(int materialId) {
//        String sql = "DELETE FROM course_materials WHERE id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, materialId);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error deleting course material: " + e.getMessage());
//            return false;
//        }
//    }
}
