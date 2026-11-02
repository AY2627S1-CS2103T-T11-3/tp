package seedu.address.model.group;

/**
 * Represents a department of colleagues
 */
public class DepartmentGroup extends Group {

    /**
     * Constructs a {@code Group}.
     */
    public DepartmentGroup(GroupName groupName) {
        super(groupName);
    }

    public DepartmentGroup(GroupName groupName, Integer groupId) {
        super(groupName, groupId);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.DEPARTMENT;
    }
}
