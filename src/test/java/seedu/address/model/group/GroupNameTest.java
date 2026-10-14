package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Name;

public class GroupNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupName(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new GroupName(invalidName));
    }

    @Test
    public void isValidGroupName() {
        // null name
        assertThrows(NullPointerException.class, () -> GroupName.isValidGroupName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("english class*")); // contains non-alphanumeric characters

        // valid name
        assertTrue(Name.isValidName("literature department")); // alphabets only
        assertTrue(Name.isValidName("2103")); // numbers only
        assertTrue(Name.isValidName("2103t")); // alphanumeric characters
        assertTrue(Name.isValidName("Science Faculty")); // with capital letters
        assertTrue(Name.isValidName("Department of Math and Statistics")); // long names
    }

    @Test
    public void equals() {
        GroupName name = new GroupName("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new GroupName("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new GroupName("Other Valid Name")));
    }

}
