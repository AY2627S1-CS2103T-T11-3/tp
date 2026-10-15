package seedu.address.model.person;

import java.util.Set;

import seedu.address.model.tag.Tag;

/** A contact who can be linked to one or more students. */
public class Guardian extends Person {

    public Guardian(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        super(name, phone, email, address, tags, Role.GUARDIAN);
    }
}
