package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;

/**
 * Jackson-friendly version of {@link Group}.
 */
public class JsonAdaptedGroup {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Group's %s field is missing!";

    private final String groupName;
    private final GroupType groupType;
    private final List<String> personIds = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedGroup} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedGroup(
            @JsonProperty("groupName") String groupName,
            @JsonProperty("groupType") GroupType groupType,
            @JsonProperty("personIds") List<String> personIds
    ) {
        this.groupName = groupName;
        this.groupType = groupType;
        this.personIds.addAll(personIds.stream().collect(Collectors.toList()));
    }

    /**
     * Converts a given {@code Group} into this class for Jackson use.
     */
    public JsonAdaptedGroup(Group source) {
        groupName = source.getGroupName().groupName;
        groupType = source.getGroupType();
        personIds.addAll(source.getPersonIds().stream().collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Group} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted group.
     */
    public Group toModelType() throws IllegalValueException {
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

        final Set<String> modelPersonIds = new HashSet<>(personIds);

        return switch (modelGroupType) {
            case CLASS -> new ClassGroup(modelGroupName, modelPersonIds);
            case DEPARTMENT -> new DepartmentGroup(modelGroupName, modelPersonIds);
        };
    }

}
