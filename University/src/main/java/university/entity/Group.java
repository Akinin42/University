package university.entity;

import java.util.Objects;

public class Group{

    private final Integer groupId;
    private final String name;

    private Group(Builder builder) {
        this.groupId = builder.groupId;
        this.name = builder.name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        Group other = (Group) obj;
        return Objects.equals(groupId, other.groupId) && Objects.equals(name, other.name);
    }    

    @Override
    public String toString() {
        return "Group " + name;
    }
    
    public static class Builder {

        private Integer groupId;
        private String name;

        private Builder() {
        }

        public Builder withId(Integer groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Group build() {
            return new Group(this);
        }
    }
}
