package seedu.address.model.group;

public class DepartmentGroup extends Group {

    /**
     * Constructs a {@code Group}.
     */
    public DepartmentGroup(GroupName groupName, GroupType groupType) {
        super(groupName, groupType);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.DEPARTMENT;
    }
}
