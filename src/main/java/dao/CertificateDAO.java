package dao;

import models.Certificate;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import db.DatabaseSetup;

public class CertificateDAO {

    public boolean issueCertificate(Certificate certificate) {
        String sql = "INSERT INTO certificates (user_id, course_id, certificate_number, issue_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, certificate.getUserId());
            pstmt.setInt(2, certificate.getCourseId());
            pstmt.setString(3, certificate.getCertificateNumber());
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при выдаче сертификата: " + e.getMessage());
            return false;
        }
    }

    public List<Certificate> getUserCertificates(int userId) {
        List<Certificate> certificates = new ArrayList<>();
        String sql = "SELECT * FROM certificates WHERE user_id = ? ORDER BY issue_date DESC";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Certificate cert = new Certificate(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("course_id"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        rs.getString("certificate_number")
                );
                certificates.add(cert);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user certificates: " + e.getMessage());
        }
        return certificates;
    }

    public List<Certificate> getAllCertificates() {
        List<Certificate> certificates = new ArrayList<>();
        String sql = "SELECT * FROM certificates ORDER BY issue_date DESC";

        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Certificate cert = new Certificate(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("course_id"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        rs.getString("certificate_number")
                );
                certificates.add(cert);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all certificates: " + e.getMessage());
        }
        return certificates;
    }
}
