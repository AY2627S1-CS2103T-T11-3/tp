package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;

/**
 * A utility class to help with building Group objects.
 */
public class GroupBuilder {

    public static final String DEFAULT_GROUP_NAME = "Math";
    public static final GroupType DEFAULT_GROUP_TYPE = GroupType.CLASS;

    private GroupName groupName;
    private GroupType groupType;
    private Set<String> personIds;

    /**
     * Creates a {@code GroupBuilder} with the default details.
     */
    public GroupBuilder() {
        groupName = new GroupName(DEFAULT_GROUP_NAME);
        groupType = DEFAULT_GROUP_TYPE;
        personIds = new HashSet<>();
    }

    /**
     * Initializes the GroupBuilder with the data of {@code groupToCopy}.
     */
    public GroupBuilder(Group groupToCopy) {
        groupName = groupToCopy.getGroupName();
        groupType = groupToCopy.getGroupType();
        personIds = groupToCopy.getPersonIds();
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
    public GroupBuilder withType(GroupType groupType) {
        this.groupType = groupType;
        return this;
    }

    /**
     * Sets the {@code GroupType} of the {@code Group} that we are building.
     */
    public GroupBuilder withPersonIds(Set<String> personIds) {
        this.personIds = Set.copyOf(personIds);
        return this;
    }

    /**
     * Builds the Group
     */
    public Group build() {
        return switch (groupType) {
            case CLASS -> new ClassGroup(groupName, personIds);
            case DEPARTMENT -> new DepartmentGroup(groupName, personIds);
        };
    }

}
