package seedu.address.model.person;

import java.util.Set;

import seedu.address.model.tag.Tag;

/**
 * Represents a student.
 */
public class Student extends Person {

    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        super(name, phone, email, address, tags);
    }

    /** Creates a student with an optional class identifier. */
    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, String className) {
        super(name, phone, email, address, tags, Role.STUDENT, className);
    }

    /**
     * Creates a student with a specified person ID instead of an automatically assigned one.
     */
    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Integer rawPersonId,
            String className) {
        super(name, phone, email, address, tags, Role.STUDENT, rawPersonId, className);
    }
}
