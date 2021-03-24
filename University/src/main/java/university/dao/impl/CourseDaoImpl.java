package university.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import university.dao.Connector;
import university.dao.CourseDao;
import university.entity.Course;
import university.exceptions.DaoException;

public class CourseDaoImpl extends AbstractCrudImpl<Course> implements CourseDao {

    private static final String SAVE_QUERY = "INSERT INTO courses (course_name, course_description) VALUES(?,?);";
    private static final String FIND_BY_ID_QUERY = "SELECT  * FROM courses WHERE course_id =  ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM courses ORDER BY course_id;";
    private static final String FIND_ALL_PAGINATION_QUERY = "SELECT * FROM courses ORDER BY course_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM courses WHERE course_id = ?;";
    private static final String GET_BY_NAME_QUERY = "SELECT  * FROM courses WHERE course_name =?";
    private static final String GET_BY_STUDENT_ID_QUERY = "SELECT course_name FROM students_to_courses "
            + "INNER JOIN courses ON students_to_courses.course_id = courses.course_id WHERE student_id = ?";

    public CourseDaoImpl(Connector connector) {
        super(connector, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY, DELETE_BY_ID_QUERY);
    }

    @Override
    protected void insert(PreparedStatement statement, Course course) throws SQLException {
        statement.setString(1, course.getName());
        statement.setString(2, course.getDescription());
    }

    @Override
    protected Course createEntityFromResultSet(ResultSet resultSet) throws SQLException {
        return Course.builder()
                .withId(resultSet.getInt("course_id"))
                .withName(resultSet.getString("course_name"))
                .withDescription(resultSet.getString("course_description"))
                .build();
    }

    @Override
    public Optional<Course> findByName(String courseName) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_BY_NAME_QUERY);) {
            statement.setString(1, courseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.ofNullable(createEntityFromResultSet(resultSet)) : Optional.empty();
            }
        } catch (SQLException | NullPointerException e) {
            throw new DaoException("Can't return by name " + courseName, e);
        }
    }

    @Override
    public List<Course> findAllByStudentId(Integer studentId) {
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_BY_STUDENT_ID_QUERY)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery();) {
                List<Course> courses = new ArrayList<>();
                while (resultSet.next()) {
                    Optional<Course> value = findByName(resultSet.getString("course_name"));
                    Course course = value.get();
                    courses.add(course);
                }
                return courses;
            }
        } catch (SQLException e) {
            throw new DaoException("Can't return everything courses by student with id = " + studentId, e);
        }
    }
}
