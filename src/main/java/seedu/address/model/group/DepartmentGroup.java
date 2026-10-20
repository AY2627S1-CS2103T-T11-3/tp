package seedu.address.model.group;

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
