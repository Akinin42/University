package university.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import university.entity.Course;
import university.entity.Group;
import university.entity.Student;

class DataGeneratorTest {

    private static final String GROUP_NAME_PATTERN = "[A-Z]{2}-[0-9]{2}";

    private static DataGenerator generator;
    private static List<String> firstNameLines;
    private static List<String> lastNameLines;
    private static List<String> courseLines;

    @BeforeAll
    static void init() {
        generator = new DataGenerator(new Random());
        firstNameLines = createFirstNameLines();
        lastNameLines = createLastNameLines();
        courseLines = createCourseLines();
    }

    @Test
    void generateStudentCourses_ShouldReturnStudentsWithCourses_WhenInputStudentsWithoutCourses() {
        List<Student> studentsWithoutCourses = createStudents();
        List<Course> courses = createCourses();
        List<Student> studentsWithCourses = generator.generateStudentCourses(studentsWithoutCourses, courses);
        assertTrue(checkCourses(studentsWithCourses));
    }

    @Test
    void generateGroups_ShouldReturnGroupsWithNameEqualsPattern_WhenInputNumberGroups() {
        List<Group> groups = generator.generateGroups(5);
        assertTrue(compareGroupNameAndSize(groups, 5));
    }

    @Test
    void generateGroups_ShouldReturnEmptyList_WhenInputNegative() {
        List<Group> expected = new ArrayList<>();
        List<Group> actual = generator.generateGroups(-5);
        assertEquals(expected, actual);
    }

    @Test
    void generateGroups_ShouldReturnEmptyList_WhenInputZero() {
        List<Group> expected = new ArrayList<>();
        List<Group> actual = generator.generateGroups(0);
        assertEquals(expected, actual);
    }

    @Test
    void generateStudents_ShouldReturnEmptyList_WhenInputNumberStudentsNegative() {
        List<Student> expected = new ArrayList<>();
        List<Student> actual = generator.generateStudents(-5, 10, firstNameLines, lastNameLines);
        assertEquals(expected, actual);
    }

    @Test
    void generateStudents_ShouldThrowIllegalArgumentException_WhenInputListFirstNameNull() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateStudents(-5, 10, null, lastNameLines));
    }

    @Test
    void generateStudents_ShouldThrowIllegalArgumentException_WhenInputListLastNameNull() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateStudents(-5, 10, firstNameLines, null));
    }

    @Test
    void generateStudents_ShouldReturnEmptyList_WhenInputNumberStudentsZero() {
        List<Student> expected = new ArrayList<>();
        List<Student> actual = generator.generateStudents(0, 10, firstNameLines, lastNameLines);
        assertEquals(expected, actual);
    }
    
    @Test
    void generateStudents_ShouldReturnEmptyList_WhenInputNamesEmpty() {
        List<Student> expected = new ArrayList<>();
        List<String> emptyList = new ArrayList<>();
        List<Student> actual = generator.generateStudents(0, 10, emptyList, lastNameLines);
        assertEquals(expected, actual);
    }
     
    @Test
    void generateStudents_ShouldReturnListWithoutGroup_WhenInputNumberGroupNegative() {
        List<Student> studentWihoutGroup = generator.generateStudents(2, -5, firstNameLines, lastNameLines);
        assertTrue(checkGroup(studentWihoutGroup));
    }
    
    @Test
    void generateStudents_ShouldReturnListWithoutGroup_WhenInputNumberGroupZero() {
        List<Student> studentWihoutGroup = generator.generateStudents(2, 0, firstNameLines, lastNameLines);
        assertTrue(checkGroup(studentWihoutGroup));
    }

    @Test
    void generateStudents_ShouldReturnStudentsWithNamesFromInputLines_WhenInputLinesNames() {
        List<Student> students = generator.generateStudents(5, 10, firstNameLines, lastNameLines);
        assertTrue(compareStudentNamesAndNumber(students, 5));
    }

    @Test
    void generateCourses_ShouldThrowIllegalArgumentException_WhenInputListNull() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateCourses(null));
    }

    @Test
    void generateCourses_ShouldThrowIllegalArgumentException_WhenInputListNotSuitPattern() {
        List<String> invalidCourseLine = new ArrayList<>();
        invalidCourseLine.add("invalidline");
        assertThrows(IllegalArgumentException.class, () -> generator.generateCourses(invalidCourseLine));
    }

    @Test
    void generateStudentCourses_ShouldThrowIllegalArgumentException_WhenInputStudentsListNull() {
        List<Course> courses = createCourses();
        assertThrows(IllegalArgumentException.class, () -> generator.generateStudentCourses(null, courses));
    }

    @Test
    void generateStudentCourses_ShouldThrowIllegalArgumentException_WhenInputCoursestsListNull() {
        List<Student> students = createStudents();
        assertThrows(IllegalArgumentException.class, () -> generator.generateStudentCourses(students, null));
    }

    @Test
    void generateCourses_ShouldReturnExpectedList_WhenInputSuitPattern() {
        List<Course> expected = createCourses();
        List<Course> actual = generator.generateCourses(courseLines);
        assertEquals(actual, expected);
    }

    private boolean compareStudentNamesAndNumber(List<Student> students, int expectedNumberStudent) {
        boolean result = false;
        if (students.size() != expectedNumberStudent) {
            return result;
        }
        for (Student student : students) {
            if (firstNameLines.contains(student.getFirstName()) && lastNameLines.contains(student.getLastName())) {
                result = true;
            }
        }
        return result;
    }

    private boolean compareGroupNameAndSize(List<Group> groups, int expectedNumberGroups) {
        boolean result = false;
        if (groups.size() != expectedNumberGroups) {
            return result;
        }
        for (Group group : groups) {
            String groupName = group.getName();
            result = groupName.matches(GROUP_NAME_PATTERN);
        }
        return result;
    }

    private static boolean checkCourses(List<Student> students) {
        boolean result = false;
        for (Student student : students) {
            if (!student.getCourses().isEmpty()) {
                result = true;
            }
        }
        return result;
    }
    
    private static boolean checkGroup(List<Student> students) {
        boolean result = false;
        for (Student student : students) {
            if (student.getGroupId() == null) {
                result = true;
            }
        }
        return result;
    }

    private static List<String> createFirstNameLines() {
        List<String> firstNameLines = new ArrayList<String>();
        firstNameLines.add("Bob");
        firstNameLines.add("Rob");
        firstNameLines.add("Ann");
        return firstNameLines;
    }

    private static List<String> createLastNameLines() {
        List<String> lastNameLines = new ArrayList<String>();
        lastNameLines.add("Lee");
        lastNameLines.add("Hill");
        lastNameLines.add("Smith");
        return lastNameLines;
    }

    private static List<String> createCourseLines() {
        List<String> courseLines = new ArrayList<String>();
        courseLines.add("Math-classroom № 4, duration 12 weeks");
        courseLines.add("Physics-classroom № 7, duration 8 weeks");
        courseLines.add("Biology-classroom № 10, duration 8 weeks");
        courseLines.add("Law-classroom № 1, duration 8 weeks");
        courseLines.add("Art-classroom № 11, duration 8 weeks");
        return courseLines;
    }

    private static List<Course> createCourses() {
        List<Course> courses = new ArrayList<>();
        Course math = Course.builder()
                .withName("Math")
                .withDescription("classroom № 4, duration 12 weeks")
                .build();
        courses.add(math);
        Course physics = Course.builder()
                .withName("Physics")
                .withDescription("classroom № 7, duration 8 weeks")
                .build();
        courses.add(physics);
        Course biology = Course.builder()
                .withName("Biology")
                .withDescription("classroom № 10, duration 8 weeks")
                .build();
        courses.add(biology);
        Course law = Course.builder()
                .withName("Law")
                .withDescription("classroom № 1, duration 8 weeks")
                .build();
        courses.add(law);
        Course art = Course.builder()
                .withName("Art")
                .withDescription("classroom № 11, duration 8 weeks")
                .build();
        courses.add(art);
        return courses;
    }

    private static List<Student> createStudents() {
        List<Student> students = new ArrayList<>();
        Student firstStudent = Student.builder()
                .withId(1)
                .withGroupId(1)
                .withFirstName("Bob")
                .withLastName("Morris")
                .build();
        Student secondStudent = Student.builder()
                .withId(2)
                .withGroupId(1)
                .withFirstName("Rob")
                .withLastName("Starck")
                .build();
        students.add(firstStudent);
        students.add(secondStudent);
        return students;
    }
}
