package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    private static final String VALID_REMARK = "Likes to swim.";

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void equals() {
        Remark remark = new Remark(VALID_REMARK);

        // same values -> returns true
        assertTrue(remark.equals(new Remark(VALID_REMARK)));

        // same object -> returns true
        assertTrue(remark.equals(remark));

        // null -> returns false
        assertFalse(remark.equals(null));

        // different type -> returns false
        assertFalse(remark.equals(VALID_REMARK));

        // different value -> returns false
        assertFalse(remark.equals(new Remark("Likes to run.")));
    }

    @Test
    public void hashCode_equalRemarks_sameHashCode() {
        assertEquals(new Remark(VALID_REMARK).hashCode(), new Remark(VALID_REMARK).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals(VALID_REMARK, new Remark(VALID_REMARK).toString());
    }
}
