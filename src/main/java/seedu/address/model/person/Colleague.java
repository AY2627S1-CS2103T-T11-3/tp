package seedu.address.model.person;

import java.util.Objects;
import java.util.Set;

import seedu.address.model.tag.Tag;

/**
 * Represents a colleague.
 */
public class Colleague extends Person {

    // Data fields
    private final String department;

    /**
     * Creates a new colleague with an optional department.
     */
    public Colleague(Name name, Phone phone, Email email, Address address, Set<Tag> tags, String department) {
        super(name, phone, email, address, tags, Role.COLLEAGUE);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        Colleague otherColleague = (Colleague) o;
        return Objects.equals(department, otherColleague.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), department);
    }
}
