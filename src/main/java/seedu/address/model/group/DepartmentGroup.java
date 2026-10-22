package seedu.address.model.group;

import java.util.Set;

import seedu.address.model.person.PersonId;

/**
 * Represents a department of colleagues
 */
public class DepartmentGroup extends Group {

    /**
     * Constructs a {@code Group}.
     */
    public DepartmentGroup(GroupName groupName, Set<PersonId> personIds) {
        super(groupName, personIds);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.DEPARTMENT;
    }
}
