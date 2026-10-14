package seedu.address.model.person;

import org.junit.jupiter.api.Test;
import seedu.address.testutil.PersonBuilder;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

public class ColleagueTest {
    @Test
    public void equals() {
        Person alice = new Colleague(new Name("Alice Pauline"), new Phone("91234567"), new Email("alice@example.com"),
                new Address("123, Jurong West Ave 6, #08-111"), new HashSet<>(), "Computer Science");
        Person aliceCopy = new Colleague(new Name("Alice Pauline"), new Phone("91234567"), new Email("alice@example.com"),
                new Address("123, Jurong West Ave 6, #08-111"), new HashSet<>(), "Computer Science");

        Person bob = new Person(new Name("Bob"), new Phone("99887766"), new Email("bob@example.org"),
                new Address("456, Banana Lane, #01-23"), new HashSet<>());

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

        // TODO: Add more tests for each field when PersonBuilder is updated
    }
}
