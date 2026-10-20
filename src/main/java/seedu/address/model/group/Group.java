package seedu.address.model.group;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represent a Group in the address book.
 */
public abstract class Group {

    private final GroupName groupName;

    /**
     * Constructs a {@code Group}.
     */
    public Group(GroupName groupName) {
        requireAllNonNull(groupName);
        this.groupName = groupName;
    }

    public GroupName getGroupName() {
        return groupName;
    }

    public abstract GroupType getGroupType();

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
                && getGroupType() == otherGroup.getGroupType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, getGroupType());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("group name", groupName)
                .toString();
    }
}
