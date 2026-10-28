package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class StudentTest {
    @Test
    public void equals() {
        Person alice = new Student(new Name("Alice Pauline"), new Phone("91234567"), new Email("alice@example.com"),
                new Address("123, Jurong West Ave 6, #08-111"), new HashSet<>(), 1, "Computer Science", 2016);
        Person aliceCopy = new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"), new HashSet<>(), 1,
                "Computer Science", 2016);
        Person aliceNoYearOfStudy = new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"), new HashSet<>(), 1,
                "Computer Science", null);
        Person aliceNoYearOfStudyCopy = new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"), new HashSet<>(), 1,
                "Computer Science", null);


        Person bob = new Person(new Name("Bob"), new Phone("99887766"), new Email("bob@example.org"),
                new Address("456, Banana Lane, #01-23"), new HashSet<>(), Role.STUDENT, 2, null);

        // same values -> returns true
        assertTrue(alice.equals(aliceCopy));

        // same object -> returns true
        assertTrue(alice.equals(alice));

        // null -> returns false
        assertFalse(alice.equals(null));

        // different type -> returns false
        assertFalse(alice.equals(5));

        // different person -> returns false
        assertFalse(alice.equals(bob));

        // one has no year of study, the other does -> return false
        assertNotEquals(alice, aliceNoYearOfStudy);
        assertNotEquals(aliceNoYearOfStudy, alice);

        // both have no year of study but are otherwise the same -> return true
        assertEquals(aliceNoYearOfStudy, aliceNoYearOfStudyCopy);
    }
}
