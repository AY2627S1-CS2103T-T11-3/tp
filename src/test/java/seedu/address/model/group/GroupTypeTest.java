package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.*;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GroupTypeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupType(null));
    }

    @Test
    public void constructor_invalidGroupType_throwsIllegalArgumentException() {
        String invalidGroupType = "invalid";
        assertThrows(IllegalArgumentException.class, () -> new GroupType(invalidGroupType));
    }

    @Test
    public void mapStringToGType() {
        // invalid type
        String invalidGroupType = "invalid type";
        assertThrows(IllegalArgumentException.class, () -> GroupType.mapStringToGType(invalidGroupType));

        // valid type
        assertEquals(GroupType.GType.CLASS, GroupType.mapStringToGType("class"));
        assertEquals(GroupType.GType.DEPARTMENT, GroupType.mapStringToGType("department"));
    }

    @Test
    public void equals() {
        GroupType type = new GroupType("class");

        // same values -> returns true
        assertTrue(type.equals(new GroupType("class")));

        // same object -> returns true
        assertTrue(type.equals(type));

        // null -> returns false
        assertFalse(type.equals(null));

        // different types -> returns false
        assertFalse(type.equals(5.0f));

        // different values -> returns false
        assertFalse(type.equals(new GroupType("department")));
    }

}
