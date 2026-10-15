package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Stores the immutable criteria used to filter persons.
 * Absent criteria and an empty tag set impose no restrictions.
 */
public final class PersonFilterCriteria {

    private final Optional<String> nameKeyword;
    private final Optional<Role> contactType;
    private final Optional<String> departmentKeyword;
    private final Optional<String> addressKeyword;
    private final Set<Tag> requiredTags;

    /**
     * Constructs filter criteria with a defensive copy of the required tags.
     *
     * @param addressKeyword The optional address substring to match.
     * @param requiredTags The exact, case-sensitive tags that must all be present.
     * @throws NullPointerException If either argument or any required tag is null.
     */
    public PersonFilterCriteria(Optional<String> addressKeyword, Set<Tag> requiredTags) {
        this(Optional.empty(), Optional.empty(), Optional.empty(), addressKeyword, requiredTags);
    }

    /**
     * Constructs criteria with optional text substrings, a resolved contact type, and required tags.
     *
     * @param nameKeyword The optional name substring to match.
     * @param contactType The optional contact type to match exactly.
     * @param departmentKeyword The optional department substring to match.
     * @param addressKeyword The optional address substring to match.
     * @param requiredTags The exact, case-sensitive tags that must all be present.
     * @throws NullPointerException If any argument or required tag is null.
     */
    public PersonFilterCriteria(Optional<String> nameKeyword, Optional<Role> contactType,
            Optional<String> departmentKeyword, Optional<String> addressKeyword, Set<Tag> requiredTags) {
        requireAllNonNull(nameKeyword, contactType, departmentKeyword, addressKeyword, requiredTags);
        this.nameKeyword = nameKeyword;
        this.contactType = contactType;
        this.departmentKeyword = departmentKeyword;
        this.addressKeyword = addressKeyword;
        this.requiredTags = Set.copyOf(requiredTags);
    }

    /** Returns the optional name substring to match. */
    public Optional<String> getNameKeyword() {
        return nameKeyword;
    }

    /** Returns the optional resolved contact type to match. */
    public Optional<Role> getContactType() {
        return contactType;
    }

    /** Returns the optional department substring to match. */
    public Optional<String> getDepartmentKeyword() {
        return departmentKeyword;
    }

    /**
     * Returns the optional address substring to match.
     */
    public Optional<String> getAddressKeyword() {
        return addressKeyword;
    }

    /**
     * Returns an unmodifiable set of the tags that must all be present.
     */
    public Set<Tag> getRequiredTags() {
        return requiredTags;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonFilterCriteria otherCriteria)) {
            return false;
        }

        return nameKeyword.equals(otherCriteria.nameKeyword)
                && contactType.equals(otherCriteria.contactType)
                && departmentKeyword.equals(otherCriteria.departmentKeyword)
                && addressKeyword.equals(otherCriteria.addressKeyword)
                && requiredTags.equals(otherCriteria.requiredTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameKeyword, contactType, departmentKeyword, addressKeyword, requiredTags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeyword", nameKeyword)
                .add("contactType", contactType)
                .add("departmentKeyword", departmentKeyword)
                .add("addressKeyword", addressKeyword)
                .add("requiredTags", requiredTags)
                .toString();
    }
}
