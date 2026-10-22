package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedGroup.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.RESPECT_5;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;
import seedu.address.model.person.PersonId;

public class JsonAdaptedGroupTest {
    private static final String VALID_GROUPNAME = RESPECT_5.getGroupName().toString();
    private static final GroupType VALID_GROUPTYPE = RESPECT_5.getGroupType();
    private static final List<PersonId> VALID_PERSONIDS = RESPECT_5.getPersonIds().stream().toList();

    private static final String INVALID_GROUPNAME = "M@th";

    @Test
    public void toModelType_validGroupDetails_returnsGroup() throws Exception {
        JsonAdaptedGroup group = new JsonAdaptedGroup(RESPECT_5);
        assertEquals(RESPECT_5, group.toModelType());
    }

    @Test
    public void toModelType_invalidGroupName_throwsIllegalValueException() {
        JsonAdaptedGroup group = new JsonAdaptedGroup(INVALID_GROUPNAME, VALID_GROUPTYPE, VALID_PERSONIDS);
        String expectedMessage = GroupName.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, group::toModelType);
    }

    @Test
    public void toModelType_nullGroupName_throwsIllegalValueException() {
        JsonAdaptedGroup group = new JsonAdaptedGroup(null, VALID_GROUPTYPE, VALID_PERSONIDS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, GroupName.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, group::toModelType);
    }

    @Test
    public void toModelType_nullGroupType_throwsIllegalValueException() {
        JsonAdaptedGroup group = new JsonAdaptedGroup(VALID_GROUPNAME, null, VALID_PERSONIDS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, GroupType.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, group::toModelType);
    }

}
