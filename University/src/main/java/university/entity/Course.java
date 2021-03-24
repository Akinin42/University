package university.entity;

import java.util.Objects;

public class Course{

    private final Integer courseId;
    private final String name;
    private final String description;

    private Course(Builder builder) {
        this.courseId = builder.courseId;
        this.name = builder.name;
        this.description = builder.description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Course)) {
            return false;
        }
        Course other = (Course) obj;
        return Objects.equals(courseId, other.courseId);
    }

    @Override
    public String toString() {
        return "Course " + name;
    }

    public static class Builder {

        private Integer courseId;
        private String name;
        private String description;

        private Builder() {
        }

        public Builder withId(Integer courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}
