package seedu.address.model.group;

import java.util.Set;

/**
 * Represents a department of colleagues
 */
public class DepartmentGroup extends Group {

    /**
     * Constructs a {@code Group}.
     */
    public DepartmentGroup(GroupName groupName, Set<String> personIds) {
        super(groupName, personIds);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.DEPARTMENT;
    }
}
