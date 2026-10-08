package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Stores the immutable address and tag criteria used to filter persons.
 * An absent address keyword and an empty tag set impose no restrictions.
 */
public final class PersonFilterCriteria {

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
        requireAllNonNull(addressKeyword, requiredTags);
        this.addressKeyword = addressKeyword;
        this.requiredTags = Set.copyOf(requiredTags);
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

        return addressKeyword.equals(otherCriteria.addressKeyword)
                && requiredTags.equals(otherCriteria.requiredTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressKeyword, requiredTags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("addressKeyword", addressKeyword)
                .add("requiredTags", requiredTags)
                .toString();
    }
}
