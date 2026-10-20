package seedu.address.model.group;

public class ClassGroup extends Group {

    /**
     * Constructs a {@code Group}.
     */
    public ClassGroup(GroupName groupName, GroupType groupType) {
        super(groupName, groupType);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CLASS;
    }
}
