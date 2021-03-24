package university.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import university.domain.DataGenerator;
import university.entity.Course;
import university.entity.Group;
import university.entity.Student;
import university.io.FileReader;

class DBInitialiserTest {

    private FileReader readerMock;
    private DataGenerator generatorMock;
    private ScriptExecutor executorMock;
    private StudentDao studentDaoMock;
    private CourseDao courseDaoMock;
    private GroupDao groupDaoMock;
    private DBInitialiser initialiser;

    @BeforeEach
    void init() {
        readerMock = mock(FileReader.class);
        generatorMock = mock(DataGenerator.class);
        executorMock = mock(ScriptExecutor.class);
        studentDaoMock = mock(StudentDao.class);
        courseDaoMock = mock(CourseDao.class);
        groupDaoMock = mock(GroupDao.class);
        initialiser = new DBInitialiser(readerMock, generatorMock, executorMock, studentDaoMock, courseDaoMock,
                groupDaoMock);
    }

    @Test
    void initDB_ShouldSummonExecuteScript_WhenSummon() {
        initialiser.initDB();
        verify(executorMock).executeScript(anyString());
    }

    @Test
    void initDB_ShouldSummonReadThrice_WhenSummon() {
        initialiser.initDB();
        verify(readerMock, atLeast(3)).read(anyString());
    }

    @Test
    void initDB_ShouldSummonGeneratorDataMethodsAndSaveResult_WhenSummon() {
        when(generatorMock.generateStudents(anyInt(),anyInt(), anyList(), anyList())).thenReturn(createStudents());
        when(generatorMock.generateGroups(anyInt())).thenReturn(createGroups());
        when(generatorMock.generateCourses(anyList())).thenReturn(createCourses());
        initialiser.initDB();
        verify(generatorMock, atLeastOnce()).generateGroups(anyInt());
        verify(generatorMock, atLeastOnce()).generateCourses(anyList());
        verify(generatorMock, atLeastOnce()).generateStudents(anyInt(),anyInt(), anyList(), anyList());
        verify(studentDaoMock).save(createStudents().get(0));
        verify(groupDaoMock).save(createGroups().get(0));
        verify(courseDaoMock).save(createCourses().get(0));
    }

    @Test
    void initDB_ShouldSummonFindAllDaoClasses_WhenSummon() {
        when(generatorMock.generateStudentCourses(anyList(), anyList())).thenReturn(createStudents());
        List<Integer> courseId = new ArrayList<>();
        courseId.add(1);
        initialiser.initDB();
        verify(courseDaoMock, atLeastOnce()).findAll();
        verify(studentDaoMock, atLeastOnce()).findAll();
        verify(studentDaoMock, atLeastOnce()).insertStudentToCourses(createStudents().get(0), courseId);
    }

    @Test
    void initDB_ShouldThrowIllegalArgumentException_WhenDaoClassNull() {
        DBInitialiser initialiserWithoutGroupDao = new DBInitialiser(readerMock, generatorMock, executorMock,
                studentDaoMock, courseDaoMock, null);
        assertThrows(IllegalArgumentException.class, () -> initialiserWithoutGroupDao.initDB());
    }
    
    @Test
    void initDB_ShouldThrowIllegalArgumentException_WhenFindAllReturnNull() {
        DBInitialiser initialiserWithNullEntities = new DBInitialiser(readerMock, generatorMock, executorMock,
                studentDaoMock, courseDaoMock, groupDaoMock);
        when(generatorMock.generateGroups(anyInt())).thenReturn(null);        
        assertThrows(IllegalArgumentException.class, () -> initialiserWithNullEntities.initDB());
    }

    private List<Student> createStudents() {
        List<Student> students = new ArrayList<>();
        Set<Course> courses = new HashSet<Course>();
        Course course = createCourses().get(0);
        courses.add(course);
        Student student = Student.builder()
                .withId(1)
                .withGroupId(1)
                .withFirstName("Bob")
                .withLastName("Morris")
                .withCourses(courses)
                .build();
        students.add(student);
        return students;
    }

    private static List<Group> createGroups() {
        List<Group> groups = new ArrayList<>();
        Group group = Group.builder()
                .withId(1)
                .withName("AB-22")
                .build();
        groups.add(group);
        return groups;
    }

    private List<Course> createCourses() {
        List<Course> courses = new ArrayList<>();
        Course course = Course.builder()
                .withId(1)
                .withName("Law")
                .withDescription("test-courses")
                .build();
        courses.add(course);
        return courses;
    }
}
