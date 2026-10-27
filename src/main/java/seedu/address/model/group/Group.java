package seedu.address.model.group;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.PersonId;

/**
 * Represent a Group in the address book.
 */
public abstract class Group {

    private final GroupId id;
    private final GroupName groupName;
    private final Set<PersonId> personIds;

    /**
     * Constructs a {@code Group}.
     */
    public Group(GroupName groupName, Set<PersonId> persondIds) {
        requireAllNonNull(groupName, persondIds);
        this.id = new GroupId();
        this.groupName = groupName;
        this.personIds = Set.copyOf(persondIds);
    }

    public GroupId getId() {
        return id;
    }

    public GroupName getGroupName() {
        return groupName;
    }

    public abstract GroupType getGroupType();

    /**
     * Returns an immutable person ids set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<PersonId> getPersonIds() {
        return Collections.unmodifiableSet(personIds);
    }

    /**
     * Returns true if both groups have the same name and types.
     * This defines a weaker notion of equality between two groups.
     */
    public boolean isSameGroup(Group otherGroup) {
        if (otherGroup == this) {
            return true;
        }

        return otherGroup != null
                && otherGroup.getGroupName().equals(getGroupName())
                && otherGroup.getGroupType().equals(getGroupType());
    }

    /**
     * Returns true if both groupName and groupType are the same.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Group otherGroup)) {
            return false;
        }

        return groupName.equals(otherGroup.groupName)
                && getGroupType() == otherGroup.getGroupType()
                && personIds.equals(otherGroup.personIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, getGroupType());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("group name", groupName)
                .add("person ids", personIds)
                .toString();
    }
}
