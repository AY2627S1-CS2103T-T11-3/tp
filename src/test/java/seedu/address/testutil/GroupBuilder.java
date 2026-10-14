package seedu.address.testutil;

import java.util.HashSet;

import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * A utility class to help with building Group objects.
 */
public class GroupBuilder {

    public static final String DEFAULT_GROUP_NAME = "Math";
    public static final String DEFAULT_GROUP_TYPE = "class";

    private GroupName groupName;
    private GroupType groupType;

    /**
     * Creates a {@code GroupBuilder} with the default details.
     */
    public GroupBuilder() {
        groupName = new GroupName(DEFAULT_GROUP_NAME);
        groupType = new GroupType(DEFAULT_GROUP_TYPE);
    }

    /**
     * Initializes the GroupBuilder with the data of {@code groupToCopy}.
     */
    public GroupBuilder(Group groupToCopy) {
        groupName = groupToCopy.getGroupName();
        groupType = groupToCopy.getGroupType();
    }

    /**
     * Sets the {@code GroupName} of the {@code Group} that we are building.
     */
    public GroupBuilder withName(String groupName) {
        this.groupName = new GroupName(groupName);
        return this;
    }

    /**
     * Sets the {@code GroupType} of the {@code Group} that we are building.
     */
    public GroupBuilder withType(String groupType) {
        this.groupType = new GroupType(groupType);
        return this;
    }

    public Group build() {
        return new Group(groupName, groupType);
    }

}
