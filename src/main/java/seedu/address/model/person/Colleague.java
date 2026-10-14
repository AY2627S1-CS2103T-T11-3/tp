package seedu.address.model.person;

import java.util.Set;

import seedu.address.model.tag.Tag;

/**
 * Represents a colleague.
 */
public class Colleague extends Person {
    public Colleague(Name name, Phone phone, Email email, Address address, Set<Tag> tags, String department) {
        super(name, phone, email, address, tags, Role.COLLEAGUE, department);
    }
}
