package seedu.address.model.person;

import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.group.GroupId;
import seedu.address.model.tag.Tag;

/**
 * Represents a student.
 */
public class Student extends Person {

    // Data fields
    private Integer yearOfStudy;

    /**
     * Creates a student with optional year of study.
     */
    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Integer yearOfStudy) {
        super(name, phone, email, address, tags);
        this.yearOfStudy = yearOfStudy;
    }

    /**
     * Creates a student with an optional class identifier and year of study.
     */
    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, GroupId groupId,
            Integer yearOfStudy) {
        super(name, phone, email, address, tags, Role.STUDENT, groupId);
        this.yearOfStudy = yearOfStudy;
    }

    /**
     * Creates a student with a specified person ID instead of an automatically assigned one.
     */
    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Integer rawPersonId,
            GroupId groupId, Integer yearOfStudy) {
        super(name, phone, email, address, tags, Role.STUDENT, rawPersonId, groupId);
        this.yearOfStudy = yearOfStudy;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Student student = (Student) o;
        if (yearOfStudy == null && student.yearOfStudy == null) {
            return true;
        }

        if (yearOfStudy != null && student.yearOfStudy == null || yearOfStudy == null && student.yearOfStudy != null) {
            return false;
        }
        return yearOfStudy.equals(((Student) o).yearOfStudy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), yearOfStudy);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", getName())
                .add("phone", getPhone())
                .add("email", getEmail())
                .add("address", getAddress())
                .add("id", getId())
                .add("tags", getTags())
                .add("yearOfStudy", getYearOfStudy())
                .toString();
    }
}
