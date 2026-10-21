package seedu.address.model.group;

import java.util.Set;

/**
 * Represents a class of students
 */
public class ClassGroup extends Group {

    /**
     * Constructs a {@code Group}.
     */
    public ClassGroup(GroupName groupName, Set<String> personIds) {
        super(groupName, personIds);
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.CLASS;
    }
}
