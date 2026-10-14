package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Address;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class ColleagueToDepartmentCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_existingColleague_updatesDepartment() {
        ColleagueToDepartmentCommand command = new ColleagueToDepartmentCommand("Alice Pauline", "Computer Science");
        // TODO: Change the test typical persons to have roles
        AddressBook ab = new AddressBook();
        ab.addPerson(new Colleague(new Name("Alice Pauline"), new Phone("91234567"), new Email("alice@example.com"),
                new Address("123, Jurong West Ave 6, #08-111"), new HashSet<Tag>(), "Computer Science"));
        Model expectedModel = new ModelManager(ab, new UserPrefs());
        Person original = expectedModel.getAddressBook().getPersonList().get(0);
        Person updated = new Colleague(original.getName(), original.getPhone(), original.getEmail(),
                original.getAddress(), original.getTags(), "Computer Science");
        expectedModel.setPerson(original, updated);

        assertCommandSuccess(command, expectedModel,
                String.format(ColleagueToDepartmentCommand.MESSAGE_SUCCESS, "Alice Pauline", "Computer Science"),
                expectedModel);
        Person firstPerson = expectedModel.getAddressBook().getPersonList().getFirst();
        assertInstanceOf(Colleague.class, firstPerson);
        assertEquals("Computer Science", ((Colleague) firstPerson).getDepartment());
    }

    @Test
    public void execute_missingColleague_throwsCommandException() {
        ColleagueToDepartmentCommand command =
                new ColleagueToDepartmentCommand("Unknown Person", "Computer Science");
        assertCommandFailure(command, model, String.format(ColleagueToDepartmentCommand.MESSAGE_NOT_FOUND,
                "Unknown Person"));
    }
}
