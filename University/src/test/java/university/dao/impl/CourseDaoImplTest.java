package university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import university.dao.Connector;
import university.dao.ConnectorH2;
import university.entity.Course;
import university.exceptions.DaoException;
import university.utils.TestUtil;

class CourseDaoImplTest {

    private static CourseDaoImpl courseDao;

    @BeforeAll
    static void init() {
        Connector connector = new ConnectorH2("h2");
        courseDao = new CourseDaoImpl(connector);
    }

    @BeforeEach
    void createTablesAndData() {
        TestUtil.executeScript("\\inittestdb.sql");
    }

    @Test
    void save_ShouldSaveCourse_WhenInputValidCourse() {
        int numberRowBeforeSave = TestUtil.getNumberRow("SELECT COUNT(*) FROM courses;");
        Course course = Course.builder()
                .withName("Test course")
                .withDescription("Test description")
                .build();
        courseDao.save(course);
        int numberRowAfterSave = TestUtil.getNumberRow("SELECT COUNT(*) FROM courses;");
        assertEquals(numberRowBeforeSave + 1, numberRowAfterSave);
    }

    @Test
    void save_ShouldThrowDaoException_WhenInputInvalidCourse() {
        Course invalidCourse = Course.builder()
                .withName(null)
                .build();
        assertThrows(DaoException.class, () -> courseDao.save(invalidCourse));
    }
    
    @Test
    void save_ShouldThrowDaoException_WhenInputNull() {
        assertThrows(DaoException.class, () -> courseDao.save(null));
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenInputIdNotExists() {
        Optional<Course> expected = Optional.empty();
        Optional<Course> actual = courseDao.findById(10);
        assertEquals(expected, actual);
    }

    @Test
    void findById_ShouldReturnExpectedCourse_WhenInputExistentId() {
        Course expected = Course.builder()
                .withId(1)
                .withName("Law")
                .withDescription("test-courses")
                .build();
        Course actual = courseDao.findById(1).get();
        assertEquals(expected, actual);
    }
    
    @Test
    void findById_ShouldThrowDaoException_WhenConnectIsNot() {
        Connector connectorMock = mock(Connector.class);
        CourseDaoImpl courseDaoWithFakeConnector = new CourseDaoImpl(connectorMock);
        assertThrows(DaoException.class, () -> courseDaoWithFakeConnector.findById(1));
    }

    @Test
    void findAll_ShouldReturnExpectedCourses_WhenCoursesTableNotEmpty() {
        List<Course> expected = createCourses();
        List<Course> actual = courseDao.findAll();
        assertEquals(expected, actual);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenCoursesTableEmpty() {
        TestUtil.queryToDB("DELETE FROM courses");
        List<Course> expected = new ArrayList<>();
        List<Course> actual = courseDao.findAll();
        assertEquals(expected, actual);
    }
    
    @Test
    void findAll_ShouldThrowDaoException_WhenConnectNull() {
        CourseDaoImpl courseDaoWithFakeConnector = new CourseDaoImpl(null);
        assertThrows(DaoException.class, () -> courseDaoWithFakeConnector.findAll());
    }

    @Test
    void findAll_ShouldReturnExpectedCourses_WhenInputLimitAndOffset() {
        List<Course> expected = createCourses();
        expected.remove(2);
        List<Course> actual = courseDao.findAll(2, 0);
        assertEquals(expected, actual);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenInputOffsetMoreTableSize() {
        List<Course> expected = new ArrayList<>();
        List<Course> actual = courseDao.findAll(2, 10);
        assertEquals(expected, actual);
    }

    @Test
    void deleteById_ShouldDeleteCourseWithInputId_WhenThisCourseExists() {
        int numberRowBeforeDelete = TestUtil.getNumberRow("SELECT COUNT(*) FROM courses;");
        courseDao.deleteById(1);
        int numberRowAfterDelete = TestUtil.getNumberRow("SELECT COUNT(*) FROM courses;");
        assertEquals(numberRowBeforeDelete - 1, numberRowAfterDelete);
    }
    
    @Test
    void deleteById_ShouldThrowDaoException_WhenConnectNull() {
        CourseDaoImpl courseDaoWithFakeConnector = new CourseDaoImpl(null);
        assertThrows(DaoException.class, () -> courseDaoWithFakeConnector.deleteById(1));
    }

    @Test
    void findByName_ShouldReturnExpectedCourse_WhenThisCourseNameExists() {
        Course expected = Course.builder()
                .withId(1)
                .withName("Law")
                .withDescription("test-courses")
                .build();
        Course actual = courseDao.findByName("Law").get();
        assertEquals(expected, actual);
    }
    
    @Test
    void findByName_ShouldThrowDaoException_WhenConnectorNull() {        
        CourseDaoImpl courseDao = new CourseDaoImpl(null);
        assertThrows(DaoException.class, () -> courseDao.findByName(""));
    }

    @Test
    void findByName_ShouldReturnEmptyOptional_WhenInputCourseNameNotExists() {
        Optional<Course> expected = Optional.empty();
        String notexistentName = "NotExistenName";
        Optional<Course> actual = courseDao.findByName(notexistentName);
        assertEquals(expected, actual);
    }

    @Test
    void findAllByStudentId_ShouldReturnExpectedCourses_WhenInputExistentStudentId() {
        List<Course> expected = createCourses();
        List<Course> actual = courseDao.findAllByStudentId(1);
        assertEquals(expected, actual);
    }

    @Test
    void findAllByStudentId_ShouldReturnExpectedCourses_WhenInputStudentIdDontHaveCourses() {
        List<Course> expected = new ArrayList<>();
        List<Course> actual = courseDao.findAllByStudentId(5);
        assertEquals(expected, actual);
    }

    private List<Course> createCourses() {
        List<Course> courses = new ArrayList<>();
        Course course = Course.builder()
                .withId(1)
                .withName("Law")
                .withDescription("test-courses")
                .build();
        courses.add(course);
        course = Course.builder()
                .withId(2)
                .withName("Math")
                .withDescription("test-courses")
                .build();
        courses.add(course);
        course = Course.builder()
                .withId(3)
                .withName("Art")
                .withDescription("test-courses")
                .build();
        courses.add(course);
        return courses;
    }
}
