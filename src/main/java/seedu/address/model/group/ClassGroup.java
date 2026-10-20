package seedu.address.model.group;

public class ClassGroup extends Group {

    /**
     * Constructs a {@code Group}.
     */
    public ClassGroup(GroupName groupName) {
        super(groupName);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CLASS;
    }
}
