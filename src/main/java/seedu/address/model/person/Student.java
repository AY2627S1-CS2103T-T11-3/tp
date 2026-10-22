package seedu.address.model.person;

import java.util.Objects;
import java.util.Set;

import seedu.address.model.tag.Tag;

/**
 * Represents a student.
 */
public class Student extends Person {

    // Data fields
    private Integer yearOfStudy;

    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Integer yearOfStudy) {
        super(name, phone, email, address, tags);
        this.yearOfStudy = yearOfStudy;
    }

    /**
     * Creates a student with an optional class identifier and year of study.
     */
    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, String className,
            Integer yearOfStudy) {
        super(name, phone, email, address, tags, Role.STUDENT, className);
        this.yearOfStudy = yearOfStudy;
    }

    /**
     * Creates a student with a specified person ID instead of an automatically assigned one.
     */
    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Integer rawPersonId,
            String className, Integer yearOfStudy) {
        super(name, phone, email, address, tags, Role.STUDENT, rawPersonId, className);
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
        return Objects.equals(yearOfStudy, student.yearOfStudy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), yearOfStudy);
    }
}
