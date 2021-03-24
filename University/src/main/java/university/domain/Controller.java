package university.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import university.dao.CourseDao;
import university.dao.GroupDao;
import university.dao.StudentDao;
import university.entity.Course;
import university.entity.Group;
import university.entity.Student;
import university.io.ViewProvider;

public class Controller {

    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final GroupDao groupDao;
    private final ViewProvider viewProvider;

    private static final String QUERY_MENU = "Hello, select a request by entering a number\n"
            + "1. Find all groups with less or equals student count\n" + "2. Find all students related to course\n"
            + "3. Add new student\n" + "4. Delete student by id\n" + "5. Add a student to the course\n"
            + "6. Remove the student from one course\n";

    public Controller(StudentDao studentDao, CourseDao courseDao, GroupDao groupDao, ViewProvider viewProvider) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.groupDao = groupDao;
        this.viewProvider = viewProvider;
    }

    public void run() {
        viewProvider.printMessage(QUERY_MENU);
        int inputNumber = viewProvider.readNumber();
        switch (inputNumber) {
        case 1:
            findGroupsBySize();
            break;
        case 2:
            findStudentsByCourse();
            break;
        case 3:
            addStudent();
            break;
        case 4:
            deleteStudent();
            break;
        case 5:
            addStudentToCourse();
            break;
        case 6:
            removeStudentFromCourse();
            break;
        default:
            viewProvider.printMessage("Is not right number!");
        }
        repeatInput();
    }

    private void findGroupsBySize() {
        viewProvider.printMessage("Enter the number of students...");
        int maxGroupSize = viewProvider.readNumber();
        List<Group> groups = groupDao.findAllBySizeEqualsOrLess(maxGroupSize);
        if (groups.isEmpty()) {
            viewProvider.printMessage("There are no groups with the same or less students");
        }
        groups.forEach(group -> viewProvider.printMessage(group.toString()));
    }

    private void findStudentsByCourse() {
        selectCourseName();
        String courseName = viewProvider.readString();
        validateCourseName(courseName);
        List<Student> students = studentDao.findAllByCourse(courseName);
        if (students.isEmpty()) {
            viewProvider.printMessage("There are no students on this course yet!");
        }        
        students.forEach(s -> viewProvider.printMessage(s.toString()));
    }

    private void addStudent() {
        viewProvider.printMessage("Enter student first name...");
        String firstName = viewProvider.readString();
        viewProvider.printMessage("Enter student lastName...");
        String lastName = viewProvider.readString();
        Student student = Student.builder()
                .withFirstName(firstName)
                .withLastName(lastName)
                .build();
        studentDao.save(student);
        viewProvider.printMessage("Student added successfully!");
    }

    private void deleteStudent() {
        showStudentsList();
        int studentId = viewProvider.readNumber();
        validateStudentId(studentId);
        studentDao.deleteById(studentId);
        viewProvider.printMessage("Student deleted successfully!");
    }

    private void addStudentToCourse() {
        selectCourseName();
        String courseName = viewProvider.readString();
        validateCourseName(courseName);
        showStudentsList();
        int studentId = viewProvider.readNumber();
        validateStudentId(studentId);
        Student student = studentDao.findById(studentId).get();
        Course course = courseDao.findByName(courseName).get();
        Set<Course> studentCourses = new HashSet<>();
        studentCourses.add(course);
        if (!studentDao.findAllByCourse(courseName).contains(student)) {
            List<Integer> coursesId = new ArrayList<>();
            studentCourses.forEach(c -> coursesId.add(c.getCourseId()));
            studentDao.insertStudentToCourses(student, coursesId);
            viewProvider.printMessage("Student added to course successfully!");                    
        }
        viewProvider.printMessage("This student already has this course!");
    }

    private void removeStudentFromCourse() {
        showStudentsList();
        int studentId = viewProvider.readNumber();
        validateStudentId(studentId);
        viewProvider.printMessage("This student has courses: ");
        List<Course> studentCourses = courseDao.findAllByStudentId(studentId);
        studentCourses.forEach(c -> viewProvider.printMessage(c.toString()));
        viewProvider.printMessage("Enter the course from which you want to remove the student...");
        String courseName = viewProvider.readString();
        validateCourseName(courseName);
        Course course = courseDao.findByName(courseName).get();
        studentDao.deleteFromCourse(studentId, course.getCourseId());
        viewProvider.printMessage("Student deleted from course successfully!");
    }

    private void selectCourseName() {
        viewProvider.printMessage("Select a course name...");
        List<Course> courses = courseDao.findAll();
        courses.forEach(course -> viewProvider.printMessage(course.toString()));
        viewProvider.printMessage("Enter the name of the course...");
    }

    private void showStudentsList() {
        viewProvider.printMessage("Do you want to see a list of all students? [y] - yes, [n] - no");
        String input = viewProvider.readString();
        if ("y".equals(input)) {
            List<Student> students = studentDao.findAll();
            students.forEach(s -> viewProvider.printMessage(s.toString()));
        }
        viewProvider.printMessage("Enter student id...");
    }

    private void validateCourseName(String courseName) {
        if (courseDao.findByName(courseName).equals(Optional.empty())) {
            viewProvider.printMessage("This course is not exist!");
        }
    }

    private void validateStudentId(int studentId) {
        if (studentDao.findById(studentId).equals(Optional.empty())) {
            viewProvider.printMessage("This student is not exist!");
        }
    }

    private void repeatInput() {
        viewProvider.printMessage("Would you like to make another request? Input: [y] - yes, [n] - no");
        String input = viewProvider.readString();
        if ("y".equals(input)) {
            run();
        }
    }
}
