package university.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import university.entity.Course;
import university.entity.Group;
import university.entity.Student;

public class DataGenerator {

    private static final String GROUP_NAME_PATTERN = "%s%s-%d%d";
    private static final String HYPHEN = "-";
    private static final int ALPHABET_SIZE = 26;
    private static final int FIRST_TITLE_LETTER_ASCII = 65;
    private static final int MIN_GROUP_SIZE = 10;
    private static final int MAX_GROUP_SIZE = 20;
    private static final int MIN_NUMBER_COURSES = 1;
    private static final int MAX_NUMBER_COURSES = 3;
    private static final String INPUT_CANT_NULL = "Input list can't be null!";

    private Random random;

    public DataGenerator(Random random) {
        this.random = random;
    }

    public List<Group> generateGroups(int numberGroups) {
        List<Group> groups = new ArrayList<>();
        while (groups.size() < numberGroups) {
            String groupName = String.format(GROUP_NAME_PATTERN, generateRandomTitleLetter(),
                    generateRandomTitleLetter(), random.nextInt(10), random.nextInt(10));
            Group group = Group.builder()
                    .withName(groupName)
                    .build();
            groups.add(group);
        }
        return groups;
    }

    public List<Student> generateStudents(int numberStudents, int numberGroups, List<String> firstNames,
            List<String> lastNames) {
        if (firstNames == null || lastNames == null) {
            throw new IllegalArgumentException(INPUT_CANT_NULL);
        }
        List<Student> students = new ArrayList<>();
        while (students.size() < numberStudents) {
            String randomFirstName = firstNames.get(random.nextInt(firstNames.size()));
            String randomLastName = lastNames.get(random.nextInt(lastNames.size()));
            Student student = Student.builder()
                    .withFirstName(randomFirstName)
                    .withLastName(randomLastName)
                    .build();
            students.add(student);
        }
        return assignStudentsToGroups(students, numberGroups);
    }

    public List<Course> generateCourses(List<String> courseLines) {
        if (courseLines == null) {
            throw new IllegalArgumentException(INPUT_CANT_NULL);
        }
        try {
            return courseLines.stream().map(line -> line.split(HYPHEN))
                    .map(s -> Course.builder()
                            .withName(s[0])
                            .withDescription(s[1])
                            .build())
                    .collect(Collectors.toList());
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            throw new IllegalArgumentException("Content in courses file is not valid! ", e);
        }
    }

    public List<Student> generateStudentCourses(List<Student> students, List<Course> courses) {
        if (students == null || courses == null) {
            throw new IllegalArgumentException(INPUT_CANT_NULL);
        }
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            Set<Course> studentCourses = assignCourses(courses);
            student = Student.builder()
                    .withId(student.getStudentId())
                    .withGroupId(student.getGroupId())
                    .withFirstName(student.getFirstName())
                    .withLastName(student.getLastName())
                    .withCourses(studentCourses)
                    .build();
            students.set(i, student);
        }
        return students;
    }

    private Set<Course> assignCourses(List<Course> courses) {
        Set<Course> randomCourses = new HashSet<>();
        random.ints(1, courses.size())
            .distinct()
            .limit(random.nextInt(MAX_NUMBER_COURSES) + MIN_NUMBER_COURSES)
            .forEach(id -> randomCourses.add(courses.get(id)));
        return randomCourses;
    }

    private char generateRandomTitleLetter() {
        return (char) (random.nextInt(ALPHABET_SIZE) + FIRST_TITLE_LETTER_ASCII);
    }

    private List<Student> assignStudentsToGroups(List<Student> students, int numberGroups) {
        int studentsCount = 0;
        int groupCount = 0;
        while (groupCount < numberGroups) {
            int groupSize = random.nextInt(MAX_GROUP_SIZE) + MIN_GROUP_SIZE;
            groupCount++;
            for (int i = studentsCount; i < (studentsCount + groupSize); i++) {
                if(i >= students.size()) {
                    break;
                }
                Student student = students.get(i);
                student = Student.builder()
                        .withGroupId(groupCount)
                        .withFirstName(student.getFirstName())
                        .withLastName(student.getLastName())
                        .build();
                students.set(i, student);
            }
            studentsCount += groupSize;
        }
        return students;
    }
}
