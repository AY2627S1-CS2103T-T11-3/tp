package seedu.address.model.person;

import java.util.Set;

import seedu.address.model.tag.Tag;

/** A contact who can be linked to one or more students. */
public class Guardian extends Person {

    public Guardian(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        super(name, phone, email, address, tags, Role.GUARDIAN);
    }

    /**
     * Creates a guardian with a specified person ID instead of an automatically assigned one.
     */
    public Guardian(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Integer rawPersonId) {
        super(name, phone, email, address, tags, Role.GUARDIAN, rawPersonId, null);
    }
}
