package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represent a Group in the address book.
 */
public class Group {

    private final GroupName groupName;
    private final GroupType groupType;

    public Group(GroupName groupName, GroupType groupType) {
        requireAllNonNull(groupName, groupType);
        this.groupName = groupName;
        this.groupType = groupType;
    }

    public GroupName getGroupName() {
        return groupName;
    }

    public GroupType getGroupType() {
        return groupType;
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
                && groupType.equals(otherGroup.groupType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, groupType);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("group name", groupName)
                .add("group type", groupType)
                .toString();
    }
}
