package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.group.Group;

/**
 * Tests whether a person satisfies all supplied filter criteria.
 * Text uses case-insensitive substring matching; contact types and tags match exactly.
 */
public class PersonMatchesFilterPredicate implements BiPredicate<Person, List<Group>> {

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
    public boolean test(Person person, List<Group> groupList) {
        requireNonNull(person);
        return matchesName(person)
                && matchesContactType(person)
                && matchesDepartment(person, groupList)
                && matchesAddress(person)
                && matchesTags(person);
    }

    /** Returns true if the name contains the keyword, or no name criterion was supplied. */
    private boolean matchesName(Person person) {
        return criteria.getNameKeyword()
                .map(keyword -> containsIgnoreCase(person.getName().fullName, keyword))
                .orElse(true);
    }

    /** Returns true if the contact has the resolved role, or no contact type was supplied. */
    private boolean matchesContactType(Person person) {
        return criteria.getContactType()
                .map(role -> person.getRole() == role)
                .orElse(true);
    }

    /** Returns false for contacts without a department when a department criterion is supplied. */
    private boolean matchesDepartment(Person person, List<Group> groupList) {
        return criteria.getDepartmentKeyword()
                .map(keyword -> person instanceof Colleague colleague
                        && groupList.stream()
                                .anyMatch(g -> g.getId().equals(colleague.getGroupId())
                                && containsIgnoreCase(g.getGroupName().groupName, keyword)))
                .orElse(true);
    }

    /**
     * Returns true if the address contains the keyword, or no address criterion was supplied.
     */
    private boolean matchesAddress(Person person) {
        return criteria.getAddressKeyword()
                .map(keyword -> containsIgnoreCase(person.getAddress().value, keyword))
                .orElse(true);
    }

    /**
     * Returns true if all required tags are present, or no tags were required.
     */
    private boolean matchesTags(Person person) {
        return person.getTags().containsAll(criteria.getRequiredTags());
    }

    /** Matches a contiguous substring independently of the system locale. */
    private static boolean containsIgnoreCase(String value, String keyword) {
        return value.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
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
