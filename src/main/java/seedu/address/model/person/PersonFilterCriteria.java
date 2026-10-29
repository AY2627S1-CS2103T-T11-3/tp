package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Stores the immutable criteria used to filter persons.
 * Absent criteria and empty tag or person ID sets impose no restrictions.
 */
public final class PersonFilterCriteria {

    private final Optional<String> nameKeyword;
    private final Optional<Role> contactType;
    private final Optional<String> departmentKeyword;
    private final Optional<String> addressKeyword;
    private final Set<Tag> requiredTags;
    private final Set<Integer> personIds;
    private final Optional<Integer> yearOfStudy;

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
        this(nameKeyword, contactType, departmentKeyword, addressKeyword, requiredTags,
                Set.of(), Optional.empty());
    }

    /**
     * Constructs criteria with defensive copies of required tags and alternative person IDs.
     *
     * @param nameKeyword The optional name substring to match.
     * @param contactType The optional contact type to match exactly.
     * @param departmentKeyword The optional department substring to match.
     * @param addressKeyword The optional address substring to match.
     * @param requiredTags The exact, case-sensitive tags that must all be present.
     * @param personIds The permanent person IDs to match exactly using OR, not displayed list indices.
     * @param yearOfStudy The optional year to match exactly on students with a year of study.
     * @throws NullPointerException If any argument, required tag, or person ID is null.
     */
    public PersonFilterCriteria(Optional<String> nameKeyword, Optional<Role> contactType,
            Optional<String> departmentKeyword, Optional<String> addressKeyword, Set<Tag> requiredTags,
            Set<Integer> personIds, Optional<Integer> yearOfStudy) {
        requireAllNonNull(nameKeyword, contactType, departmentKeyword, addressKeyword, requiredTags,
                personIds, yearOfStudy);
        this.nameKeyword = nameKeyword;
        this.contactType = contactType;
        this.departmentKeyword = departmentKeyword;
        this.addressKeyword = addressKeyword;
        this.requiredTags = Set.copyOf(requiredTags);
        this.personIds = Set.copyOf(personIds);
        this.yearOfStudy = yearOfStudy;
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

    /** Returns an unmodifiable set of alternative permanent person IDs to match exactly. */
    public Set<Integer> getPersonIds() {
        return personIds;
    }

    /** Returns the optional year of study to match exactly on students. */
    public Optional<Integer> getYearOfStudy() {
        return yearOfStudy;
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
                && requiredTags.equals(otherCriteria.requiredTags)
                && personIds.equals(otherCriteria.personIds)
                && yearOfStudy.equals(otherCriteria.yearOfStudy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameKeyword, contactType, departmentKeyword, addressKeyword, requiredTags,
                personIds, yearOfStudy);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("nameKeyword", nameKeyword)
                .add("contactType", contactType)
                .add("departmentKeyword", departmentKeyword)
                .add("addressKeyword", addressKeyword)
                .add("requiredTags", requiredTags);
        if (!personIds.isEmpty()) {
            builder.add("personIds", personIds);
        }
        if (yearOfStudy.isPresent()) {
            builder.add("yearOfStudy", yearOfStudy);
        }
        return builder.toString();
    }
}
