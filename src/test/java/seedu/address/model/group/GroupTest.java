package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_NAME_DEPARTMENT;
import static seedu.address.testutil.TypicalGroups.MATH_DPT;
import static seedu.address.testutil.TypicalGroups.RESPECT_5;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.GroupBuilder;

public class GroupTest {
    @Test
    public void constructor() {
        Group group = new ClassGroup(new GroupName("Computer Science"));
        assertEquals(new GroupName("Computer Science"), group.getGroupName());
    }

    @Test
    public void isSameGroup() {
        // same object -> returns true
        assertTrue(RESPECT_5.isSameGroup(RESPECT_5));

        // null -> returns false
        assertFalse(RESPECT_5.isSameGroup(null));

        // same name, all other attributes different -> returns true
        Group editedRespect5 = new GroupBuilder(RESPECT_5).build();
        assertTrue(RESPECT_5.isSameGroup(editedRespect5));

        // different name, all other attributes same -> returns false
        editedRespect5 = new GroupBuilder(RESPECT_5).withName(VALID_GROUP_NAME_DEPARTMENT).build();
        assertFalse(RESPECT_5.isSameGroup(editedRespect5));

        // name differs in case, all other attributes same -> returns false
        editedRespect5 = new GroupBuilder(RESPECT_5)
                .withName(RESPECT_5.getGroupName().groupName.toLowerCase()).build();
        assertFalse(RESPECT_5.isSameGroup(editedRespect5));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = RESPECT_5.getGroupName().groupName + " ";
        editedRespect5 = new GroupBuilder(RESPECT_5).withName(nameWithTrailingSpaces).build();
        assertFalse(RESPECT_5.isSameGroup(editedRespect5));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Group respect5Copy = new GroupBuilder(RESPECT_5).build();
        assertTrue(RESPECT_5.equals(respect5Copy));

        // same object -> returns true
        assertTrue(RESPECT_5.equals(RESPECT_5));

        // null -> returns false
        assertFalse(RESPECT_5.equals(null));

        // different type -> returns false
        assertFalse(RESPECT_5.equals(5));

        // different group -> returns false
        assertFalse(RESPECT_5.equals(MATH_DPT));

        // different group name -> returns false
        Group editedRespect5 = new GroupBuilder(RESPECT_5).withName(VALID_GROUP_NAME_DEPARTMENT).build();
        assertFalse(RESPECT_5.equals(editedRespect5));

        // different group type -> returns false
        editedRespect5 = new GroupBuilder(RESPECT_5).withType(GroupType.DEPARTMENT).build();
        assertFalse(RESPECT_5.equals(editedRespect5));

        // Membership is stored on Person.groupId, so it does not affect group equality.
        editedRespect5 = new GroupBuilder(RESPECT_5).build();
        assertTrue(RESPECT_5.equals(editedRespect5));
    }

    @Test
    public void toStringMethod() {
        String expected = ClassGroup.class.getCanonicalName() + "{group name=" + RESPECT_5.getGroupName() + "}";
        assertEquals(expected, RESPECT_5.toString());
    }

}
