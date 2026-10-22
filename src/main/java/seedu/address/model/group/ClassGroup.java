package seedu.address.model.group;

import seedu.address.model.person.PersonId;

import java.util.Set;

/**
 * Represents a class of students
 */
public class ClassGroup extends Group {

    /**
     * Constructs a {@code Group}.
     */
    public ClassGroup(GroupName groupName, Set<PersonId> personIds) {
        super(groupName, personIds);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CLASS;
    }
}
