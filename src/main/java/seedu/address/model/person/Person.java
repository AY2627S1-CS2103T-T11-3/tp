package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
<<<<<<< HEAD
    private final String groupId;
    private final String department;
=======
>>>>>>> 48572617d8cec197d4d453942455e7073b6fc20a
    private final Role role;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Create a person with default role student.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, Role.STUDENT);
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Role role) {
        requireAllNonNull(name, phone, email, address, tags, role);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
<<<<<<< HEAD
        this.groupId = department;
        this.department = department;
=======
>>>>>>> 48572617d8cec197d4d453942455e7073b6fc20a
        this.role = role;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

<<<<<<< HEAD
    public String getDepartment() {
        return department;
    }

    /** Returns the identifier of the group this person belongs to, if any. */
    public String getGroupId() {
        return groupId;
    }

=======
>>>>>>> 48572617d8cec197d4d453942455e7073b6fc20a
    public Role getRole() {
        return role;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
<<<<<<< HEAD
                && Objects.equals(groupId, otherPerson.groupId)
                && Objects.equals(department, otherPerson.department)
=======
>>>>>>> 48572617d8cec197d4d453942455e7073b6fc20a
                && role == otherPerson.role
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
<<<<<<< HEAD
        return Objects.hash(name, phone, email, address, groupId, department, tags, role);
=======
        return Objects.hash(name, phone, email, address, tags, role);
>>>>>>> 48572617d8cec197d4d453942455e7073b6fc20a
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }

}
