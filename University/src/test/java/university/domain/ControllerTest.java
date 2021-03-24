package university.domain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import university.dao.CourseDao;
import university.dao.GroupDao;
import university.dao.StudentDao;
import university.entity.Course;
import university.entity.Group;
import university.entity.Student;
import university.io.ViewProvider;

class ControllerTest {
    
    private StudentDao studentDaoMock;
    private CourseDao courseDaoMock;
    private GroupDao groupDaoMock;
    private ViewProvider viewProviderMock;
    private Controller controller;
    
    @BeforeEach
    void init() {
        studentDaoMock = mock(StudentDao.class);
        courseDaoMock = mock(CourseDao.class);
        groupDaoMock = mock(GroupDao.class);
        viewProviderMock = mock(ViewProvider.class);
        controller = new Controller(studentDaoMock, courseDaoMock, groupDaoMock, viewProviderMock);
    }
    
    @Test
    void run_ShouldSummonPrintMessageAtLeastOnce_WhenSummon() {     
        controller.run();        
        verify(viewProviderMock, atLeastOnce()).printMessage(anyString());
    }
    
    @Test
    void run_ShouldSummonReadNumberAtLeastOnce_WhenSummon() {     
        controller.run();        
        verify(viewProviderMock, atLeastOnce()).readNumber();
    }
    
    @Test
    void run_ShouldSummonFindAllBySizeEqualsOrLess_WhenInputOne() {        
        when(viewProviderMock.readNumber()).thenReturn(1);
        List<Group> groups = createGroups();
        when(groupDaoMock.findAllBySizeEqualsOrLess(anyInt())).thenReturn(groups);
        controller.run();        
        verify(groupDaoMock).findAllBySizeEqualsOrLess(1);
        verify(viewProviderMock).printMessage(groups.get(0).toString());
    }
    
    @Test
    void run_ShouldOutputInConsoleExpectedMessage_WhenInputOneAndZero() {        
        when(viewProviderMock.readNumber()).thenReturn(1, 0);
        List<Group> groups = new ArrayList<>();
        when(groupDaoMock.findAllBySizeEqualsOrLess(0)).thenReturn(groups);
        controller.run();
        String expectedMessage = "There are no groups with the same or less students";
        verify(viewProviderMock).printMessage(expectedMessage);
    }
    @Test
    void run_ShouldOutputInConsoleExpectedMessageTwice_WhenInputNotRightAndYes() {        
        when(viewProviderMock.readNumber()).thenReturn(7);
        when(viewProviderMock.readString()).thenReturn("y", "n");
        controller.run();        
        verify(viewProviderMock, atLeast(2)).printMessage("Is not right number!");
    }
    
    @Test
    void run_ShouldSummonPrintWithExpectedMessage_WhenInputOneAndGroupsEmpty() {        
        when(viewProviderMock.readNumber()).thenReturn(1);
        List<Group> groups = new ArrayList<>();
        when(groupDaoMock.findAllBySizeEqualsOrLess(anyInt())).thenReturn(groups);        
        controller.run();
        String expectedMessage = "There are no groups with the same or less students";
        verify(viewProviderMock).printMessage(expectedMessage);
    }
    
    @Test
    void run_ShouldSummonAllInvocationsFindStudentsByCourse_WhenInputTwo() {        
        when(viewProviderMock.readNumber()).thenReturn(2);
        when(viewProviderMock.readString()).thenReturn("Test");
        List<Student> students = createStudents();
        when(studentDaoMock.findAllByCourse(anyString())).thenReturn(students);        
        controller.run();
        verify(courseDaoMock).findAll();
        verify(studentDaoMock).findAllByCourse("Test");
        verify(viewProviderMock).printMessage(students.get(0).toString());
    }
    
    @Test
    void run_ShouldSummonPrintWithExpectedMessage_WhenInputTwoAndStudentsEmpty() {        
        when(viewProviderMock.readNumber()).thenReturn(2);
        when(viewProviderMock.readString()).thenReturn("Test");
        List<Student> students = new ArrayList<>();
        when(studentDaoMock.findAllByCourse(anyString())).thenReturn(students);        
        controller.run();
        String expectedMessage = "There are no students on this course yet!";
        verify(viewProviderMock).printMessage(expectedMessage);
    }
    
    @Test
    void run_ShouldSummonSaveStudentDao_WhenInputThree() {        
        when(viewProviderMock.readNumber()).thenReturn(3);
        when(viewProviderMock.readString()).thenReturn("Test");
        Student student = Student.builder()
                .withFirstName("Test")
                .withLastName("Test")
                .build();
        controller.run();        
        verify(studentDaoMock).save(student);
    }
    
    @Test
    void run_ShouldSummonAllInvocationsDeleteStudent_WhenInputFourAndYes() {        
        when(viewProviderMock.readNumber()).thenReturn(4);
        when(viewProviderMock.readString()).thenReturn("y", "n"); 
        controller.run();
        verify(studentDaoMock).findAll();
        verify(studentDaoMock).findById(4);
        verify(studentDaoMock).deleteById(4);        
    }
    
    @Test
    void run_ShouldSummonInsertStudentToCoursesStudentDao_WhenInputFive() {        
        when(viewProviderMock.readNumber()).thenReturn(5);
        when(viewProviderMock.readString()).thenReturn("Test");
        Student student = createStudents().get(0);
        when(studentDaoMock.findById(anyInt())).thenReturn(Optional.ofNullable(student));
        Course course = createCourses().get(0);
        when(courseDaoMock.findByName(anyString())).thenReturn(Optional.ofNullable(course));
        List<Integer> courses = new ArrayList<>();
        courses.add(course.getCourseId());
        controller.run();        
        verify(studentDaoMock).insertStudentToCourses(student, courses);
    }
    
    @Test
    void run_ShouldPrintExpectedMessageToConsole_WhenInputFiveAndStudentHasCourse() {        
        when(viewProviderMock.readNumber()).thenReturn(5);
        when(viewProviderMock.readString()).thenReturn("Law");
        Student student = createStudents().get(0);
        when(studentDaoMock.findById(anyInt())).thenReturn(Optional.ofNullable(student));
        when(studentDaoMock.findAllByCourse("Law")).thenReturn(createStudents());
        Course course = createCourses().get(0);
        when(courseDaoMock.findByName(anyString())).thenReturn(Optional.ofNullable(course));
        List<Integer> courses = new ArrayList<>();
        courses.add(course.getCourseId());
        controller.run();        
        String expectedMessage = "This student already has this course!";
        verify(viewProviderMock).printMessage(expectedMessage);
    }  
    
    @Test
    void run_ShouldSummonDeleteFromCourseStudentDao_WhenInputSix() {
        when(viewProviderMock.readNumber()).thenReturn(6);
        when(viewProviderMock.readString()).thenReturn("Test");
        Course course = createCourses().get(0);
        when(courseDaoMock.findByName(anyString())).thenReturn(Optional.ofNullable(course));
        controller.run();
        verify(studentDaoMock).deleteFromCourse(anyInt(), anyInt());
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
    
    private List<Student> createStudents() {
        List<Student> students = new ArrayList<>();
        Set<Course> courses = new HashSet<Course>();
        Course course = Course.builder()
                .withId(1)
                .withName("Law")
                .withDescription("test-courses")
                .build();
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
