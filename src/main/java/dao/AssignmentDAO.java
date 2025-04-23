package dao;

import models.Assignment;
import db.DatabaseSetup;
import models.AssignmentSubmission;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {

    public boolean createAssignment(Assignment assignment) {
        String sql = "INSERT INTO assignments (course_id, title, description, deadline, max_score) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, assignment.getCourseId());
            pstmt.setString(2, assignment.getTitle());
            pstmt.setString(3, assignment.getDescription());
            pstmt.setTimestamp(4, Timestamp.valueOf(assignment.getDeadline()));
            pstmt.setInt(5, assignment.getMaxScore());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        assignment.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating assignment: " + e.getMessage());
            return false;
        }
    }

    public List<Assignment> getCourseAssignments(int courseId) {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignments WHERE course_id = ? ORDER BY deadline ASC";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Assignment assignment = new Assignment(
                            rs.getInt("id"),
                            rs.getInt("course_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getTimestamp("deadline").toLocalDateTime(),
                            rs.getInt("max_score")
                    );
                    assignments.add(assignment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting assignments for course " + courseId + ": " + e.getMessage());
        }

        return assignments;
    }

    public boolean submitAssignment(AssignmentSubmission submission) {
        String sql = "INSERT INTO assignment_submissions " +
                "(assignment_id, student_id, answer, submitted_at) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            submission.setSubmittedAt(LocalDateTime.now());

            pstmt.setInt(1, submission.getAssignmentId());
            pstmt.setInt(2, submission.getStudentId());
            pstmt.setString(3, submission.getAnswer());
            pstmt.setTimestamp(4, Timestamp.valueOf(submission.getSubmittedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        submission.setId(rs.getInt(1)); // Устанавливаем ID новой записи
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error submitting assignment: " + e.getMessage());
            return false;
        }
    }

    public AssignmentSubmission getStudentSubmission(int studentId, int assignmentId) {
        String sql = "SELECT * FROM assignment_submissions " +
                "WHERE student_id = ? AND assignment_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, assignmentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new AssignmentSubmission(
                            rs.getInt("id"),
                            rs.getInt("assignment_id"),
                            rs.getInt("student_id"),
                            rs.getString("answer"),
                            rs.getTimestamp("submitted_at") != null ?
                                    rs.getTimestamp("submitted_at").toLocalDateTime() : null,
                            rs.getInt("score"),
                            rs.getString("feedback")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting student submission: studentId=" + studentId +
                    ", assignmentId=" + assignmentId + ": " + e.getMessage());
        }
        return null;
    }

////    public boolean deleteAssignment(int assignmentId) {
//        String sql = "DELETE FROM assignments WHERE id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, assignmentId);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error deleting assignment: " + e.getMessage());
//            return false;
//        }
//    }


    public List<AssignmentSubmission> getAssignmentSubmissions(int assignmentId) {
        List<AssignmentSubmission> submissions = new ArrayList<>();
        String sql = "SELECT * FROM assignment_submissions WHERE assignment_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, assignmentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AssignmentSubmission submission = new AssignmentSubmission(
                            rs.getInt("id"),
                            rs.getInt("assignment_id"),
                            rs.getInt("student_id"),
                            rs.getString("answer"),
                            rs.getTimestamp("submitted_at") != null ?
                                    rs.getTimestamp("submitted_at").toLocalDateTime() : null,
                            rs.getInt("score"),
                            rs.getString("feedback")
                    );
                    submissions.add(submission);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting submissions for assignment " + assignmentId + ": " + e.getMessage());
        }
        return submissions;
    }

    public Assignment getAssignment(int assignmentId) {
        String sql = "SELECT * FROM assignments WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, assignmentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Assignment(
                            rs.getInt("id"),
                            rs.getInt("course_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getTimestamp("deadline").toLocalDateTime(),
                            rs.getInt("max_score")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting assignment " + assignmentId + ": " + e.getMessage());
        }
        return null;
    }

    public boolean gradeSubmission(int submissionId, int score, String feedback) {
        String sql = "UPDATE assignment_submissions SET score = ?, feedback = ? WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, score);
            pstmt.setString(2, feedback);
            pstmt.setInt(3, submissionId);

            int rowsUpdated = pstmt.executeUpdate();
            boolean success = rowsUpdated > 0;

            if (success) {
                System.out.println("Submission graded successfully!");
            } else {
                System.out.println("Failed to grade submission. Submission ID not found: " + submissionId);
            }
            return success;

        } catch (SQLException e) {
            System.err.println("Error grading submission " + submissionId + ": " + e.getMessage());
            return false;
        }
    }
}
