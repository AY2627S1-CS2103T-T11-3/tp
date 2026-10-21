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

    @Override
    public GroupType getGroupType() {
        return GroupType.DEPARTMENT;
    }
}
