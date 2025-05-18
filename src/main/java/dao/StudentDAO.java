package dao;

import models.*;
import models.additional.*;
import java.sql.*;
import java.util.*;

public class StudentDAO {
    private final Connection connection;

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Course> getAvailableCourses(int studentId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT id, title, description FROM courses " +
                "WHERE is_active = true AND id NOT IN " +
                "(SELECT course_id FROM enrollments WHERE user_id = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        0,
                        true
                ));
            }}
        return courses;
    }

    public boolean enrollInCourse(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrollments (user_id, course_id, enrolled_at) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Course> getStudentCourses(int studentId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.id, c.title, c.description FROM courses c " +
                "JOIN enrollments e ON c.id = e.course_id " +
                "WHERE e.user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        0,
                        true
                ));
            }}
        return courses;
    }

    public List<Assignment> getCourseAssignments(int studentId, int courseId) throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT a.id, a.title, a.description, a.deadline, a.max_score, " +
                "s.score, s.submitted_at FROM assignments a " +
                "LEFT JOIN assignment_submissions s ON a.id = s.assignment_id AND s.user_id = ? " +
                "WHERE a.course_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                assignments.add(new Assignment(
                        rs.getInt("id"),
                        courseId,
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("deadline").toLocalDateTime(),
                        rs.getInt("max_score"),
                        rs.getInt("score"),
                        rs.getTimestamp("submitted_at") != null ?
                                rs.getTimestamp("submitted_at").toLocalDateTime() : null
                ));
            }}
        return assignments;
    }

    public boolean submitAssignment(int assignmentId, int studentId, String answer) throws SQLException {
        String sql = "INSERT INTO assignment_submissions " +
                "(assignment_id, user_id, answer, submitted_at) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON DUPLICATE KEY UPDATE answer = VALUES(answer), submitted_at = VALUES(submitted_at)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, studentId);
            stmt.setString(3, answer);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Test> getCourseTests(int studentId, int courseId) throws SQLException {
        List<Test> tests = new ArrayList<>();
        String sql = "SELECT t.id, t.title, t.description, t.time_limit, t.passing_score, " +
                "tr.score FROM tests t " +
                "LEFT JOIN test_results tr ON t.id = tr.test_id AND tr.user_id = ? " +
                "WHERE t.course_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tests.add(new Test(
                        rs.getInt("id"),
                        courseId,
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("time_limit"),
                        rs.getInt("passing_score"),
                        rs.getInt("score")
                ));
            }}
        return tests;
    }

    public Test getTestById(int testId) throws SQLException {
        String sql = "SELECT id, course_id, title, description, time_limit, passing_score " +
                "FROM tests WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, testId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Test(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("time_limit"),
                        rs.getInt("passing_score")
                );
            }}
        return null;
    }

    public boolean checkAnswer(int questionId, String studentAnswer) throws SQLException {
        String sql = "SELECT COUNT(*) as correct_count FROM answer_options " +
                "WHERE question_id = ? AND is_correct = TRUE " +
                "AND id IN (" + studentAnswer.replaceAll("[^0-9,]", "") + ")";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("correct_count") > 0;
        }
    }

    public boolean saveTestResult(int studentId, int testId, int score,
                                  int passingScore, Map<Integer, String> answers) throws SQLException {
        String resultSql = "INSERT INTO test_results " +
                "(user_id, test_id, score, passing_score, completed_at) " +
                "VALUES (?, ?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE score = VALUES(score), completed_at = VALUES(completed_at)";

        String answersSql = "INSERT INTO test_answers " +
                "(test_result_id, question_id, answer) VALUES (?, ?, ?)";

        try (PreparedStatement resultStmt = connection.prepareStatement(resultSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement answersStmt = connection.prepareStatement(answersSql)) {

            resultStmt.setInt(1, studentId);
            resultStmt.setInt(2, testId);
            resultStmt.setInt(3, score);
            resultStmt.setInt(4, passingScore);
            resultStmt.executeUpdate();

            int resultId;
            try (ResultSet rs = resultStmt.getGeneratedKeys()) {
                if (!rs.next()) return false;
                resultId = rs.getInt(1);
            }

            for (Map.Entry<Integer, String> entry : answers.entrySet()) {
                answersStmt.setInt(1, resultId);
                answersStmt.setInt(2, entry.getKey());
                answersStmt.setString(3, entry.getValue());
                answersStmt.addBatch();
            }
            answersStmt.executeBatch();
            return true;
        }
    }

    public List<TestQuestion> getTestQuestions(int testId) throws SQLException {
        List<TestQuestion> questions = new ArrayList<>();
        String sql = "SELECT id, question_text, question_type, points " +
                "FROM test_questions WHERE test_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, testId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                questions.add(new TestQuestion(
                        rs.getInt("id"),
                        testId,
                        rs.getString("question_text"),
                        rs.getString("question_type"),
                        rs.getInt("points")
                ));
            }}
        return questions;
    }

    public List<AnswerOption> getAnswerOptions(int questionId) throws SQLException {
        List<AnswerOption> options = new ArrayList<>();
        String sql = "SELECT id, option_text, is_correct FROM answer_options WHERE question_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                options.add(new AnswerOption(
                        rs.getInt("id"),
                        questionId,
                        rs.getString("option_text"),
                        rs.getBoolean("is_correct")
                ));
            }}
        return options;
    }

    public boolean saveTestResult(int studentId, int testId, int score, int passingScore) throws SQLException {
        String sql = "INSERT INTO test_results " +
                "(user_id, test_id, score, passing_score, completed_at) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON DUPLICATE KEY UPDATE score = VALUES(score), completed_at = VALUES(completed_at)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, testId);
            stmt.setInt(3, score);
            stmt.setInt(4, passingScore);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Webinar> getUpcomingWebinars(int studentId) throws SQLException {
        List<Webinar> webinars = new ArrayList<>();
        String sql = "SELECT w.id, w.title, w.scheduled_at, c.title as course_title " +
                "FROM webinars w " +
                "JOIN courses c ON w.course_id = c.id " +
                "WHERE w.course_id IN (SELECT course_id FROM enrollments WHERE user_id = ?) " +
                "AND w.was_conducted = false " +
                "AND w.scheduled_at > NOW() " +
                "ORDER BY w.scheduled_at";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                webinars.add(new Webinar(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getTimestamp("scheduled_at").toLocalDateTime(),
                        rs.getString("course_title")
                ));
            }}
        return webinars;
    }

    public List<Certificate> getStudentCertificates(int studentId) throws SQLException {
        List<Certificate> certificates = new ArrayList<>();
        String sql = "SELECT c.id, co.title as course_name, c.issue_date, c.final_score " +
                "FROM certificates c " +
                "JOIN courses co ON c.course_id = co.id " +
                "WHERE c.user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                certificates.add(new Certificate(
                        rs.getInt("id"),
                        studentId,
                        rs.getInt("course_id"),
                        rs.getDate("issue_date").toLocalDate(),
                        rs.getInt("final_score"),
                        rs.getString("course_name")
                ));
            }}
        return certificates;
    }

    public Map<String, Double> getStudentProgress(int studentId) throws SQLException {
        Map<String, Double> progress = new HashMap<>();
        String sql = "SELECT c.title, " +
                "COALESCE(AVG(a.score), 0) as avg_assignment_score, " +
                "COALESCE(AVG(t.score), 0) as avg_test_score " +
                "FROM enrollments e " +
                "JOIN courses c ON e.course_id = c.id " +
                "LEFT JOIN assignment_submissions a ON a.user_id = e.user_id AND " +
                "a.assignment_id IN (SELECT id FROM assignments WHERE course_id = c.id) " +
                "LEFT JOIN test_results t ON t.user_id = e.user_id AND " +
                "t.test_id IN (SELECT id FROM tests WHERE course_id = c.id) " +
                "WHERE e.user_id = ? " +
                "GROUP BY c.title";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                progress.put(
                        rs.getString("title"),
                        (rs.getDouble("avg_assignment_score") + rs.getDouble("avg_test_score")) / 2
                );
            }}
        return progress;
    }
}