package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersonIdTest {
    @BeforeEach
    public void setUp() {
        PersonId.setNextId(0);
    }

    @Test
    public void match_equalValue_returnsTrue() {
        PersonId id0 = new PersonId();
        assertTrue(id0.matches(0));

        PersonId id1 = new PersonId();
        assertTrue(id1.matches(1));

        PersonId id2 = new PersonId();
        assertTrue(id2.matches(2));
    }

    @Test
    public void match_equalValue_returnsFalse() {
        PersonId id0 = new PersonId();
        assertFalse(id0.matches(123));

        PersonId id1 = new PersonId();
        assertFalse(id1.matches(123));

        PersonId id2 = new PersonId();
        assertFalse(id2.matches(123));
    }

    @Test
    public void setNextId_constructorBeginsAtSetId() {
        PersonId.setNextId(100);
        PersonId id1 = new PersonId();

        assertTrue(id1.matches(100));

        PersonId.setNextId(200);
        PersonId id2 = new PersonId();
        assertTrue(id2.matches(200));
    }

    @Test
    public void equals() {
        PersonId id1 = new PersonId();
        PersonId.setNextId(0);
        PersonId id2 = new PersonId();
        assertEquals(id1, id2);
    }
}
