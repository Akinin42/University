package university.dao;

import java.util.ArrayList;
import java.util.List;
import university.domain.DataGenerator;
import university.entity.Course;
import university.entity.Group;
import university.entity.Student;
import university.io.FileReader;

public class DBInitialiser {

    private static final int NUMBER_GROUPS = 10;
    private static final int NUMBER_STUDENTS = 200;
    private static final String SQL_SCRIPT_FILE = "schema.sql";
    private static final String FIRST_NAMES_FILE = "firstNames.txt";
    private static final String LAST_NAMES_FILE = "lastNames.txt";
    private static final String COURSES_FILE = "courses.txt";

    private final FileReader reader;
    private final DataGenerator generator;
    private final ScriptExecutor executor;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final GroupDao groupDao;

    public DBInitialiser(FileReader reader, DataGenerator generator, ScriptExecutor executor, StudentDao studentDao,
            CourseDao courseDao, GroupDao groupDao) {
        this.reader = reader;
        this.generator = generator;
        this.executor = executor;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.groupDao = groupDao;
    }

    public void initDB() {
        executor.executeScript(SQL_SCRIPT_FILE);
        generateEntities();
        saveStudentsToCourses();
    }

    private void generateEntities() {
        List<String> contentFirstNames = reader.read(FIRST_NAMES_FILE);
        List<String> contentLastNames = reader.read(LAST_NAMES_FILE);
        List<String> contentCourses = reader.read(COURSES_FILE);
        List<Group> groups = generator.generateGroups(NUMBER_GROUPS);
        List<Course> courses = generator.generateCourses(contentCourses);
        List<Student> students = generator.generateStudents(NUMBER_STUDENTS, NUMBER_GROUPS, contentFirstNames, contentLastNames);
        saveAll(groups, groupDao);
        saveAll(students, studentDao);
        saveAll(courses, courseDao);
    }

    private void saveStudentsToCourses() {
        List<Course> courses = courseDao.findAll();
        List<Student> students = studentDao.findAll();
        students = generator.generateStudentCourses(students, courses);
        for (Student student : students) {
            List<Integer> coursesId = new ArrayList<>();
            for (Course course : student.getCourses()) {
                coursesId.add(course.getCourseId());
            }
            studentDao.insertStudentToCourses(student, coursesId);
        }
    }

    private <T> void saveAll(List<T> entities, CrudDao<T, Integer> entityDao) {
        if (entities == null) {
            throw new IllegalArgumentException("Input list of entities can't be null!");
        }
        if (entityDao == null) {
            throw new IllegalArgumentException("Input dao instance can't be null!");
        }
        for (T entity : entities) {
            entityDao.save(entity);
        }
    }
}
