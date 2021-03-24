package university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import university.dao.Connector;
import university.dao.ConnectorH2;
import university.entity.Student;
import university.exceptions.DaoException;
import university.utils.TestUtil;

class StudentDaoImplTest {

    private static StudentDaoImpl studentDao;

    @BeforeAll
    static void init() {
        Connector connector = new ConnectorH2("h2");
        studentDao = new StudentDaoImpl(connector);
    }

    @BeforeEach
    void createTables() {
        TestUtil.executeScript("\\inittestdb.sql");
    }

    @Test
    void save_ShouldSaveStudent_WhenInputValidStudent() {
        int numberRowBeforeSave = TestUtil.getNumberRow("SELECT COUNT(*) FROM students;");
        Student student = Student.builder()
                .withFirstName("Bob")
                .withLastName("Still")
                .build();
        studentDao.save(student);
        int numberRowAfterSave = TestUtil.getNumberRow("SELECT COUNT(*) FROM students;");
        assertEquals(numberRowBeforeSave + 1, numberRowAfterSave);
    }

    @Test
    void save_ShouldThrowDaoException_WhenInputInvalidStudent() {
        Student invalidStudent = Student.builder()
                .withFirstName(null)
                .build();
        assertThrows(DaoException.class, () -> studentDao.save(invalidStudent));
    }
    
    @Test
    void save_ShouldThrowDaoException_WhenInputNull() {        
        assertThrows(DaoException.class, () -> studentDao.save(null));
    }

    @Test
    void findById_ShouldReturnExpectedStudent_WhenInputExistentId() {
        Student expected = createStudent();
        Student actual = studentDao.findById(2).get();
        assertEquals(expected, actual);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenInputIdNotExists() {
        Optional<Student> expected = Optional.empty();
        Optional<Student> actual = studentDao.findById(10);
        assertEquals(expected, actual);
    }
    
    @Test
    void findById_ShouldThrowDaoException_WhenProblemWithConnect() {
        Connector fakeConnector = mock(Connector.class);        
        when(fakeConnector.getConnection()).thenThrow(DaoException.class);
        StudentDaoImpl studentDao = new StudentDaoImpl(fakeConnector);        
        assertThrows(DaoException.class, () -> studentDao.findById(0));              
    }

    @Test
    void findAll_ShouldReturnExpectedStudents_WhenStudentsTableNotEmpty() {
        List<Student> expected = createStudents();
        List<Student> actual = studentDao.findAll();
        assertEquals(expected, actual);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenStudentsTableEmpty() {
        TestUtil.queryToDB("DELETE FROM students");
        List<Student> expected = new ArrayList<>();
        List<Student> actual = studentDao.findAll();
        assertEquals(expected, actual);
    }

    @Test
    void findAll_ShouldReturnExpectedStudents_WhenInputLimitAndOffset() {
        List<Student> expected = createStudents();
        expected.remove(5);
        List<Student> actual = studentDao.findAll(5, 0);
        assertEquals(expected, actual);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenInputOffsetMoreTableSize() {
        List<Student> expected = new ArrayList<>();
        List<Student> actual = studentDao.findAll(2, 10);
        assertEquals(expected, actual);
    }

    @Test
    void deleteById_ShouldDeleteStudentWithInputId_WhenThisStudentExists() {
        int numberRowBeforeDelete = TestUtil.getNumberRow("SELECT COUNT(*) FROM students;");
        studentDao.deleteById(1);
        int numberRowAfterDelete = TestUtil.getNumberRow("SELECT COUNT(*) FROM students;");
        assertEquals(numberRowBeforeDelete - 1, numberRowAfterDelete);
    }
    
    @Test
    void findAllByCourse_ShouldReturnExpectedStudents_WhenInputCourseName() {
        List<Student> expected = createStudents();
        expected.remove(5);
        expected.remove(4);
        List<Student> actual = studentDao.findAllByCourse("Law");
        assertEquals(expected, actual);
    }

    @Test
    void findAllByCourse_ShouldReturnEmptyList_WhenInputNull() {
        List<Student> expected = new ArrayList<>();
        List<Student> actual = studentDao.findAllByCourse(null);
        assertEquals(expected, actual);
    }

    @Test
    void findAllByCourse_ShouldReturnEmptyList_WhenInputNotexistentCourseName() {
        List<Student> expected = new ArrayList<>();
        List<Student> actual = studentDao.findAllByCourse("Notexistent");
        assertEquals(expected, actual);
    }

    @Test
    void deleteFromCourse_ShoudDeleteStudentFromCourses_WhenInputExistentStudentIdAndCourseid() {
        int numberRowBeforeDelete = TestUtil
                .getNumberRow("SELECT COUNT(*) FROM students_to_courses WHERE course_id = 1;");
        studentDao.deleteFromCourse(1, 1);
        int numberRowAfterDelete = TestUtil
                .getNumberRow("SELECT COUNT(*) FROM students_to_courses WHERE course_id = 1;");
        assertEquals(numberRowBeforeDelete - 1, numberRowAfterDelete);
    }

    @Test
    void insertStudentToCourses_ShouldAddStudentToCourse_WhenInputValidStudentAndCourseid() {
        int numberRowBeforeAdd = TestUtil.getNumberRow("SELECT COUNT(*) FROM students_to_courses WHERE course_id = 3;");
        Student student = createStudent();
        List<Integer> coursesId = new ArrayList<>();
        coursesId.add(3);
        studentDao.insertStudentToCourses(student, coursesId);
        int numberRowAfterAdd = TestUtil.getNumberRow("SELECT COUNT(*) FROM students_to_courses WHERE course_id = 3;");
        assertEquals(numberRowBeforeAdd + 1, numberRowAfterAdd);
    }

    @Test
    void insertStudentToCourses_ShouldThrowDaoException_WhenInputStudentHasInputCourses() {
        Student student = createStudent();
        List<Integer> coursesId = new ArrayList<>();
        coursesId.add(1);
        assertThrows(DaoException.class, () -> studentDao.insertStudentToCourses(student, coursesId));
    }
    
    @Test
    void findAllByCourse_ShouldThrowDaoException_WhenConnectIsNot() {
        Connector connectorMock = mock(Connector.class);
        StudentDaoImpl courseDaoWithFakeConnector = new StudentDaoImpl(connectorMock);
        assertThrows(DaoException.class, () -> courseDaoWithFakeConnector.findAllByCourse(""));
    }

    private List<Student> createStudents() {
        List<Student> students = new ArrayList<>();
        Student student = Student.builder()
                .withId(1)
                .withGroupId(1)
                .withFirstName("Bob")
                .withLastName("Morris")
                .build();
        students.add(student);
        student = Student.builder()
                .withId(2)
                .withGroupId(1)
                .withFirstName("Rob")
                .withLastName("Brown")
                .build();
        students.add(student);
        student = Student.builder()
                .withId(3)
                .withGroupId(1)
                .withFirstName("Ann")
                .withLastName("Wood")
                .build();
        students.add(student);
        student = Student.builder()
                .withId(4)
                .withGroupId(2)
                .withFirstName("Jonh")
                .withLastName("Stark")
                .build();
        students.add(student);
        student = Student.builder()
                .withId(5)
                .withGroupId(2)
                .withFirstName("Tom")
                .withLastName("Lee")
                .build();
        students.add(student);
        student = Student.builder()
                .withId(6)
                .withGroupId(2)
                .withFirstName("Jinn")
                .withLastName("Grey")
                .build();
        students.add(student);
        return students;
    }

    private Student createStudent() {
        return Student.builder()
                .withId(2)
                .withGroupId(1)
                .withFirstName("Rob")
                .withLastName("Brown")
                .build();
    }
}
