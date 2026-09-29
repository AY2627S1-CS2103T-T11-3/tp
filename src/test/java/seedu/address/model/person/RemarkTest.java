package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class RemarkTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void equals_comparesValue() {
        Remark remark = new Remark("Likes swimming");
        assertEquals(remark, new Remark("Likes swimming"));
        assertEquals(remark.hashCode(), new Remark("Likes swimming").hashCode());
        assertNotEquals(remark, new Remark(""));
        assertNotEquals(remark, null);
        assertEquals("Likes swimming", remark.toString());
    }

    @Test
    public void person_differentRemark_changesEqualityButNotIdentity() {
        Person original = new PersonBuilder().build();
        Person edited = new PersonBuilder(original).withRemark("Likes swimming").build();
        assertNotEquals(original, edited);
        assertTrue(original.isSamePerson(edited));
    }
}
