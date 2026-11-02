package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupId;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;

/**
 * Jackson-friendly version of {@link Group}.
 */
public class JsonAdaptedGroup {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Group's %s field is missing!";

    private final Integer groupId;
    private final String groupName;
    private final GroupType groupType;

    /**
     * Constructs a {@code JsonAdaptedGroup} with the given group details.
     * Legacy {@code personIds} fields in existing JSON files are ignored because
     * membership is now represented by each person's {@code groupId}.
     */
    @JsonCreator
    public JsonAdaptedGroup(
            @JsonProperty("groupId") Integer groupId,
            @JsonProperty("groupName") String groupName,
            @JsonProperty("groupType") GroupType groupType
    ) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupType = groupType;
    }

    /**
     * Converts a given {@code Group} into this class for Jackson use.
     */
    public JsonAdaptedGroup(Group source) {
        groupId = source.getId().getRawId();
        groupName = source.getGroupName().groupName;
        groupType = source.getGroupType();
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Group} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted group.
     */
    public Group toModelType() throws IllegalValueException {
        if (groupId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    GroupId.class.getSimpleName()));
        }
        final GroupId modelGroupId = GroupId.createFromRawId(groupId);

        if (groupName == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    GroupName.class.getSimpleName()));
        }
        if (!GroupName.isValidGroupName(groupName)) {
            throw new IllegalValueException(GroupName.MESSAGE_CONSTRAINTS);
        }
        final GroupName modelGroupName = new GroupName(groupName);

        if (groupType == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    GroupType.class.getSimpleName()));
        }
        final GroupType modelGroupType = groupType;

        return switch (modelGroupType) {
            case CLASS -> new ClassGroup(modelGroupName, groupId);
            case DEPARTMENT -> new DepartmentGroup(modelGroupName, groupId);
        };
    }

}
