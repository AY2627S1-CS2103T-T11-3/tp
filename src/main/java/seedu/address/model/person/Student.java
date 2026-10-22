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

    /**
     * Creates a student with a specified person ID instead of an automatically assigned one.
     */
    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Integer rawPersonId) {
        super(name, phone, email, address, tags, Role.STUDENT, rawPersonId, null);
    }
}
