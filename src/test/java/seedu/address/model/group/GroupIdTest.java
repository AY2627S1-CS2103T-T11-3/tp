package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GroupIdTest {
    @BeforeEach
    public void setUp() {
        GroupId.setNextId(1);
    }

    @Test
    public void match_equalValue_returnsTrue() {
        GroupId id0 = new GroupId();
        assertTrue(id0.matches(1));

        GroupId id1 = new GroupId();
        assertTrue(id1.matches(2));

        GroupId id2 = new GroupId();
        assertTrue(id2.matches(3));
    }

    @Test
    public void match_unequalValue_returnsFalse() {
        GroupId id0 = new GroupId();
        assertFalse(id0.matches(2));

        GroupId id1 = new GroupId();
        assertFalse(id1.matches(3));

        GroupId id2 = new GroupId();
        assertFalse(id2.matches(0));
    }

    @Test
    public void setNextId_constructorBeginsAtSetId() {
        GroupId.setNextId(100);
        GroupId id1 = new GroupId();

        assertTrue(id1.matches(100));

        GroupId.setNextId(200);
        GroupId id2 = new GroupId();
        assertTrue(id2.matches(200));
    }

    @Test
    public void equals() {
        GroupId.setNextId(1);
        GroupId id1 = new GroupId();
        GroupId.setNextId(1);
        GroupId id2 = new GroupId();

        assertEquals(id1, id2);

        GroupId.setNextId(100);
        GroupId id3 = new GroupId();

        // Different IDs should not be equals
        assertNotEquals(id1, id3);

        // IDs should not be equals to null
        assertNotEquals(id1, null);
    }
}
