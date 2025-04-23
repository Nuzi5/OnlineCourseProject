package dao;

import models.Test;
import models.TestQuestion;
import models.AnswerOption;
import db.DatabaseSetup;
import models.TestResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestDAO {
    public boolean createTest(Test test) {
        String sql = "INSERT INTO tests (course_id, title, description, time_limit, passing_score, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, test.getCourseId());
            pstmt.setString(2, test.getTitle());
            pstmt.setString(3, test.getDescription());
            pstmt.setInt(4, test.getTimeLimit());
            pstmt.setInt(5, test.getPassingScore());
            pstmt.setInt(6, test.getCreatedBy());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        test.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating test: " + e.getMessage());
            return false;
        }
    }

////    public boolean updateTest(Test test) {
//        String sql = "UPDATE tests SET title = ?, description = ?, time_limit = ?, passing_score = ? " +
//                "WHERE id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, test.getTitle());
//            pstmt.setString(2, test.getDescription());
//            pstmt.setInt(3, test.getTimeLimit());
//            pstmt.setInt(4, test.getPassingScore());
//            pstmt.setInt(5, test.getId());
//
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error updating test: " + e.getMessage());
//            return false;
//        }
//    }

////    public boolean deleteTest(int testId) {
//        String sql = "DELETE FROM tests WHERE id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, testId);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error deleting test: " + e.getMessage());
//            return false;
//        }
//    }

    public Test getTestById(int testId) {
        String sql = "SELECT * FROM tests WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, testId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Test(
                            rs.getInt("id"),
                            rs.getInt("course_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("time_limit"),
                            rs.getInt("passing_score")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting test: " + e.getMessage());
        }
        return null;
    }

    public List<Test> getCourseTests(int courseId) {
        List<Test> tests = new ArrayList<>();
        String sql = "SELECT * FROM tests WHERE course_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Test test = new Test(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("time_limit"),
                        rs.getInt("passing_score")
                );
                tests.add(test);
            }
        } catch (SQLException e) {
            System.err.println("Error getting course tests: " + e.getMessage());
        }
        return tests;
    }

    public boolean addQuestionToTest(TestQuestion question) {
        String sql = "INSERT INTO test_questions (test_id, question, question_type, points) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, question.getTestId());
            pstmt.setString(2, question.getQuestionText());
            pstmt.setString(3, question.getQuestionType());
            pstmt.setInt(4, question.getPoints());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        question.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding test question: " + e.getMessage());
            return false;
        }
    }

////    public boolean updateQuestion(TestQuestion question) {
//        String sql = "UPDATE test_questions SET question = ?, question_type = ?, points = ? " +
//                "WHERE id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, question.getQuestionText());
//            pstmt.setString(2, question.getQuestionType());
//            pstmt.setInt(3, question.getPoints());
//            pstmt.setInt(4, question.getId());
//
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error updating question: " + e.getMessage());
//            return false;
//        }
//    }

////    public boolean deleteQuestion(int questionId) {
//        String sql = "DELETE FROM test_questions WHERE id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, questionId);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error deleting question: " + e.getMessage());
//            return false;
//        }
//    }

    public List<TestQuestion> getTestQuestions(int testId) {
        List<TestQuestion> questions = new ArrayList<>();
        String sql = "SELECT * FROM test_questions WHERE test_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, testId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                TestQuestion question = new TestQuestion(
                        rs.getInt("id"),
                        rs.getInt("test_id"),
                        rs.getString("question"),
                        rs.getString("question_type"),
                        rs.getInt("points")
                );
                question.setAnswerOptions(getAnswerOptions(question.getId()));
                questions.add(question);
            }
        } catch (SQLException e) {
            System.err.println("Error getting test questions: " + e.getMessage());
        }
        return questions;
    }

    public boolean addAnswerOption(AnswerOption option) {
        String sql = "INSERT INTO answer_options (question_id, option_text, is_correct) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, option.getQuestionId());
            pstmt.setString(2, option.getOptionText());
            pstmt.setBoolean(3, option.isCorrect());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        option.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding answer option: " + e.getMessage());
            return false;
        }
    }

////    public boolean updateAnswerOption(AnswerOption option) {
//        String sql = "UPDATE answer_options SET option_text = ?, is_correct = ? " +
//                "WHERE id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, option.getOptionText());
//            pstmt.setBoolean(2, option.isCorrect());
//            pstmt.setInt(3, option.getId());
//
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error updating answer option: " + e.getMessage());
//            return false;
//        }
//    }

////    public boolean deleteAnswerOption(int optionId) {
//        String sql = "DELETE FROM answer_options WHERE id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, optionId);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            System.err.println("Error deleting answer option: " + e.getMessage());
//            return false;
//        }
//    }

    public List<AnswerOption> getAnswerOptions(int questionId) {
        List<AnswerOption> options = new ArrayList<>();
        String sql = "SELECT * FROM answer_options WHERE question_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AnswerOption option = new AnswerOption(
                        rs.getInt("id"),
                        rs.getInt("question_id"),
                        rs.getString("option_text"),
                        rs.getBoolean("is_correct")
                );
                options.add(option);
            }
        } catch (SQLException e) {
            System.err.println("Error getting answer options: " + e.getMessage());
        }
        return options;
    }

    public boolean saveTestResult(int studentId, int testId, int score) {
        String sql = "INSERT INTO test_results (student_id, test_id, score, completed_at) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, testId);
            pstmt.setInt(3, score);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving test result: " + e.getMessage());
            return false;
        }
    }

////    public Integer getStudentTestScore(int studentId, int testId) {
//        String sql = "SELECT score FROM test_results WHERE student_id = ? AND test_id = ?";
//
//        try (Connection conn = DatabaseSetup.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, studentId);
//            pstmt.setInt(2, testId);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt("score");
//                }
//            }
//        } catch (SQLException e) {
//            System.err.println("Error getting test score: " + e.getMessage());
//        }
//        return null;
//    }

    public TestResult getStudentResult(int studentId, int testId) {
        String sql = "SELECT * FROM test_results WHERE student_id = ? AND test_id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, testId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Test test = getTestById(testId);
                    return new TestResult(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("test_id"),
                            rs.getInt("score"),
                            test != null ? test.getPassingScore() : 0,
                            rs.getTimestamp("completed_at").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting student result: studentId=" + studentId +
                    ", testId=" + testId + ": " + e.getMessage());
        }

        return null;
    }
    public Test getTest(int testId) {
        String sql = "SELECT * FROM tests WHERE id = ?";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, testId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Test test = new Test(
                            rs.getInt("id"),
                            rs.getInt("course_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("time_limit"),
                            rs.getInt("passing_score"),
                            rs.getInt("created_by")
                    );

                    test.setQuestions(getTestQuestions(testId));
                    return test;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting test with ID " + testId + ": " + e.getMessage());
        }
        return null;
    }
}