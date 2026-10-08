package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person satisfies all supplied address and tag criteria.
 * Addresses use case-insensitive substring matching, while tags use case-sensitive exact matching.
 */
public class PersonMatchesFilterPredicate implements Predicate<Person> {

    private final PersonFilterCriteria criteria;

    /**
     * Constructs a predicate for the given filter criteria.
     *
     * @throws NullPointerException If {@code criteria} is null.
     */
    public PersonMatchesFilterPredicate(PersonFilterCriteria criteria) {
        this.criteria = requireNonNull(criteria);
    }

    @Override
    public boolean test(Person person) {
        return matchesAddress(person) && matchesTags(person);
    }

    /**
     * Returns true if the address contains the keyword, or no address criterion was supplied.
     */
    private boolean matchesAddress(Person person) {
        return criteria.getAddressKeyword()
                .map(keyword -> person.getAddress().value.toLowerCase(Locale.ROOT)
                        .contains(keyword.toLowerCase(Locale.ROOT)))
                .orElse(true);
    }

    /**
     * Returns true if all required tags are present, or no tags were required.
     */
    private boolean matchesTags(Person person) {
        return person.getTags().containsAll(criteria.getRequiredTags());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonMatchesFilterPredicate otherPredicate)) {
            return false;
        }

        return criteria.equals(otherPredicate.criteria);
    }

    @Override
    public int hashCode() {
        return criteria.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("criteria", criteria)
                .toString();
    }
}
