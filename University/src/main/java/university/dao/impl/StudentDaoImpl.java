package university.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import university.dao.Connector;
import university.dao.StudentDao;
import university.entity.Student;
import university.exceptions.DaoException;

public class StudentDaoImpl extends AbstractCrudImpl<Student> implements StudentDao {

    private static final String SAVE_QUERY = "INSERT INTO students (group_id, first_name, last_name) VALUES(?,?,?);";
    private static final String FIND_BY_ID_QUERY = "SELECT  * FROM students WHERE student_id =  ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM students ORDER BY student_id;";
    private static final String FIND_ALL_PAGINATION_QUERY = "SELECT * FROM students ORDER BY student_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM students WHERE student_id = ?;";
    private static final String FIND_ALL_BY_COURSE_QUERY = "SELECT * FROM students "
            + "INNER JOIN students_to_courses ON students_to_courses.student_id = students.student_id "
            + "INNER JOIN courses ON students_to_courses.course_id = courses.course_id WHERE course_name = ?;";
    private static final String DELETE_FROM_COURSE_QUERY = "DELETE FROM students_to_courses WHERE student_id = ? AND course_id = ?;";
    private static final String SAVE_STUDENT_TO_COURSES_QUERY = "INSERT INTO students_to_courses (student_id, course_id) VALUES(?,?);";

    public StudentDaoImpl(Connector connector) {
        super(connector, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY, DELETE_BY_ID_QUERY);
    }

    @Override
    protected void insert(PreparedStatement statement, Student student) throws SQLException {
        statement.setObject(1, student.getGroupId());
        statement.setString(2, student.getFirstName());
        statement.setString(3, student.getLastName());
    }

    @Override
    protected Student createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        return Student.builder()
                .withId(resultSet.getInt("student_id"))
                .withGroupId(resultSet.getInt("group_id"))
                .withFirstName(resultSet.getString("first_name"))
                .withLastName(resultSet.getString("last_name"))
                .build();
    }

    @Override
    public List<Student> findAllByCourse(String courseName) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_COURSE_QUERY)) {
            statement.setString(1, courseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Student> students = new ArrayList<>();
                while (resultSet.next()) {
                    students.add(createEntityFromResultSet(resultSet));
                }
                return students;
            }
        } catch (SQLException | NullPointerException e) {
            throw new DaoException("Can't return everyone students by course " + courseName, e);
        }
    }

    @Override
    public void deleteFromCourse(int studentId, int courseId) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_FROM_COURSE_QUERY)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Can't remove student by id " + studentId + " from course by id " + courseId, e);
        }
    }

    @Override
    public void insertStudentToCourses(Student student, List<Integer> coursesId) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(SAVE_STUDENT_TO_COURSES_QUERY)) {
            for (Integer courseId : coursesId) {
                statement.setInt(1, student.getStudentId());
                statement.setInt(2, courseId);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DaoException("Can't insert to course student by id " + student.getStudentId(), e);
        }

    }
}
