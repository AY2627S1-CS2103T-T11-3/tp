package seedu.address.model.group;

/**
 * Represents a class of students
 */
public class ClassGroup extends Group {

    /**
     * Constructs a {@code Group}.
     */
    public ClassGroup(GroupName groupName) {
        super(groupName);
    }

    public ClassGroup(GroupName groupName, Integer groupId) {
        super(groupName, groupId);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CLASS;
    }
}
