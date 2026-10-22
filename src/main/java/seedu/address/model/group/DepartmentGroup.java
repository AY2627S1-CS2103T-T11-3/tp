package seedu.address.model.group;

import seedu.address.model.person.PersonId;

import java.util.Set;

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
