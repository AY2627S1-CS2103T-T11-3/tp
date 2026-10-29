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
    private final PersonId id;
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final String groupId;
    private final Role role;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Creates a person with default role student.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, Role.STUDENT);
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Role role) {
        this(name, phone, email, address, tags, role, null);
    }

    /** Creates a person with an optional group identifier. */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags,
            Role role, String groupId) {
        requireAllNonNull(name, tags, role);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.groupId = groupId;
        this.role = role;
        this.tags.addAll(tags);
        this.id = new PersonId();
    }

    /**
     * Creates a person with a specified person ID instead of an automatically assigned one.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Role role, Integer rawPersonId,
                  String groupId) {
        requireAllNonNull(name, phone, email, address, tags, role, rawPersonId);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.groupId = groupId;
        this.role = role;
        this.tags.addAll(tags);
        this.id = PersonId.createFromRawId(rawPersonId);
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

    /** Returns the identifier of the group this person belongs to, if any. */
    public String getGroupId() {
        return groupId;
    }

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

    public PersonId getId() {
        return id;
    }

    /**
     * Returns true if both persons have the same ID.
     * Name is not used as multiple people can have the same name.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getId().equals(getId());
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

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Person otherPerson = (Person) other;

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && Objects.equals(groupId, otherPerson.groupId)
                && role == otherPerson.role
                && tags.equals(otherPerson.tags)
                && id.equals(otherPerson.id);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, groupId, tags, role, id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("id", id)
                .add("tags", tags)
                .toString();
    }

}
