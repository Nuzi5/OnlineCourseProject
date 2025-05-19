package dao;

import models.*;
import models.additional.*;
import java.sql.*;
import java.util.*;

public class TeacherDAO {
    private final Connection connection;

    public TeacherDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Course> getTeacherCourses(int teacherId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.id, c.title, c.description, c.is_active " +
                "FROM courses c " +
                "JOIN course_teachers ct ON c.id = ct.course_id " +
                "WHERE ct.teacher_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        teacherId,
                        rs.getBoolean("is_active")
                ));
            }
        }
        return courses;
    }

    public List<Assignment> getCourseAssignments(int courseId) throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT id, title, description, deadline, max_score " +
                "FROM assignments WHERE course_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                assignments.add(new Assignment(
                        rs.getInt("id"),
                        courseId,
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("deadline").toLocalDateTime(),
                        rs.getInt("max_score")
                ));
            }}
        return assignments;
    }

    public List<AssignmentSubmission> getAssignmentSubmissions(int assignmentId) throws SQLException {
        List<AssignmentSubmission> submissions = new ArrayList<>();
        String sql = "SELECT s.id, s.assignment_id, s.student_id, u.full_name, s.answer, " +
                "s.score, s.submitted_at, s.graded_at " +
                "FROM assignment_submissions s " +
                "JOIN users u ON s.student_id = u.id " +
                "WHERE s.assignment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                submissions.add(new AssignmentSubmission(
                        rs.getInt("id"),
                        rs.getInt("assignment_id"),
                        rs.getInt("student_id"),
                        rs.getString("answer"),
                        rs.getTimestamp("submitted_at").toLocalDateTime(),
                        rs.getInt("score"),
                        rs.getString("full_name")
                ));
            }}
        return submissions;
    }

    public boolean gradeAssignment(int submissionId, int score, int gradedBy) throws SQLException {
        String sql = "UPDATE assignment_submissions SET score = ?, graded_by = ?, graded_at = NOW() " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, score);
            stmt.setInt(2, gradedBy);
            stmt.setInt(3, submissionId);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<Test> getCourseTests(int courseId) throws SQLException {
        List<Test> tests = new ArrayList<>();
        String sql = "SELECT id, title, description, time_limit, passing_score " +
                "FROM tests WHERE course_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tests.add(new Test(
                        rs.getInt("id"),
                        courseId,
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("time_limit"),
                        rs.getInt("passing_score")
                ));
            }}
        return tests;
    }

    public int createTest(int courseId, String title, String description,
                          Integer timeLimit, Integer passingScore, int createdBy) throws SQLException {
        String sql = "INSERT INTO tests (course_id, title, description, time_limit, passing_score, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, courseId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setObject(4, timeLimit);
            stmt.setObject(5, passingScore);
            stmt.setInt(6, createdBy);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                connection.rollback();
                throw new SQLException("Не удалось создать тест");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    connection.rollback();
                    throw new SQLException("Не удалось получить ID созданного теста");
                }
            }
        }
    }
    public int addTestQuestion(int testId, String questionText, String questionType, int points) throws SQLException {
        String sql = "INSERT INTO test_questions (test_id, question_text, question_type, points) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, testId);
            stmt.setString(2, questionText);
            stmt.setString(3, questionType);
            stmt.setInt(4, points);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Не удалось добавить вопрос");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Не удалось получить ID вопроса");
                }
            }
        }
    }


    public boolean addAnswerOption(int questionId, String optionText, boolean isCorrect) throws SQLException {
        String sql = "INSERT INTO answer_options (question_id, option_text, is_correct) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            stmt.setString(2, optionText);
            stmt.setBoolean(3, isCorrect);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<TestResult> getTestResults(int testId) throws SQLException {
        List<TestResult> results = new ArrayList<>();
        String sql = "SELECT tr.id, u.full_name, tr.score, tr.passing_score, tr.completed_at " +
                "FROM test_results tr " +
                "JOIN users u ON tr.student_id = u.id " +
                "WHERE tr.test_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, testId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new TestResult(
                        rs.getInt("id"),
                        testId,
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getInt("score"),
                        rs.getInt("passing_score"),
                        rs.getTimestamp("completed_at").toLocalDateTime()
                ));
            }}
        return results;
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

    public List<Test> getTestsWithTextQuestions(int courseId) throws SQLException {
        String sql = "SELECT DISTINCT t.id, t.title " +
                "FROM tests t " +
                "JOIN test_questions q ON t.id = q.test_id " +
                "WHERE t.course_id = ? AND q.question_type = 'text'";

        List<Test> tests = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tests.add(new Test(rs.getInt("id"), rs.getString("title")));
            }}
        return tests;
    }

    public List<TextAnswerForReview> getTextAnswersForReview(int testId) throws SQLException {
        String sql = "SELECT a.id as answer_id, u.full_name as student_name, " +
                "q.question_text, q.points as max_points, a.answer " +
                "FROM test_answers a " +
                "JOIN test_results r ON a.test_result_id = r.id " +
                "JOIN users u ON r.student_id = u.id " +
                "JOIN test_questions q ON a.question_id = q.id " +
                "WHERE q.question_type = 'text' AND r.test_id = ? " +
                "AND NOT EXISTS (SELECT 1 FROM text_answer_grades g WHERE g.answer_id = a.id)";

        List<TextAnswerForReview> answers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, testId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                answers.add(new TextAnswerForReview(
                        rs.getInt("answer_id"),
                        rs.getString("student_name"),
                        rs.getString("question_text"),
                        rs.getString("answer"),
                        rs.getInt("max_points")
                ));
            }}
        return answers;
    }

    public boolean saveTextAnswerScore(int answerId, int score, int teacherId) throws SQLException {
        String sql = "INSERT INTO text_answer_grades (answer_id, score, graded_by, graded_at) " +
                "VALUES (?, ?, ?, NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, answerId);
            stmt.setInt(2, score);
            stmt.setInt(3, teacherId);
            return stmt.executeUpdate() > 0;
        }}
}
