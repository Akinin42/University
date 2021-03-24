package university.entity;

import java.util.Objects;
import java.util.Set;

public class Student{

    private final Integer studentId;
    private final Integer groupId;
    private final String firstName;
    private final String lastName;
    private final Set<Course> courses;

    private Student(Builder builder) {
        this.studentId = builder.studentId;
        this.groupId = builder.groupId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.courses = builder.courses;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getStudentId() {
        return studentId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, groupId, lastName, studentId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Student)) {
            return false;
        }
        Student other = (Student) obj;
        return Objects.equals(firstName, other.firstName) && Objects.equals(groupId, other.groupId)
                && Objects.equals(lastName, other.lastName) && Objects.equals(studentId, other.studentId);
    }

    @Override
    public String toString() {
        return "id " + studentId + ". " + firstName + " " + lastName;
    }

    public static class Builder {

        private Integer studentId;
        private Integer groupId;
        private String firstName;
        private String lastName;
        private Set<Course> courses;

        private Builder() {
        }

        public Builder withId(Integer studentId) {
            this.studentId = studentId;
            return this;
        }

        public Builder withGroupId(Integer groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withCourses(Set<Course> courses) {
            this.courses = courses;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }
}
