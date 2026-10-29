package seedu.address.model.person;

import java.util.Objects;
import java.util.Set;

import seedu.address.model.group.GroupId;
import seedu.address.model.tag.Tag;

/**
 * Represents a colleague.
 */
public class Colleague extends Person {

    /**
     * Creates a new colleague with an optional department.
     */
    public Colleague(Name name, Phone phone, Email email, Address address, Set<Tag> tags, GroupId groupId) {
        super(name, phone, email, address, tags, Role.COLLEAGUE, groupId);
    }

    /**
     * Creates a new colleague with a specified person ID instead of an automatically assigned one.
     */
    public Colleague(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Integer rawPersonId,
                     GroupId groupId) {
        super(name, phone, email, address, tags, Role.COLLEAGUE, rawPersonId, groupId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
