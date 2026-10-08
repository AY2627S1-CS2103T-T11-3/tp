package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class ColleagueToDepartmentCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_existingColleague_updatesDepartment() {
        ColleagueToDepartmentCommand command = new ColleagueToDepartmentCommand("Alice Pauline", "Computer Science");
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person original = expectedModel.getAddressBook().getPersonList().get(0);
        Person updated = new Person(original.getName(), original.getPhone(), original.getEmail(),
                original.getAddress(), original.getTags(), "Computer Science");
        expectedModel.setPerson(original, updated);

        assertCommandSuccess(command, model,
                String.format(ColleagueToDepartmentCommand.MESSAGE_SUCCESS, "Computer Science"), expectedModel);
        assertEquals("Computer Science", model.getAddressBook().getPersonList().get(0).getDepartment());
    }

    @Test
    public void execute_missingColleague_throwsCommandException() {
        ColleagueToDepartmentCommand command =
                new ColleagueToDepartmentCommand("Unknown Person", "Computer Science");
        assertCommandFailure(command, model, String.format(ColleagueToDepartmentCommand.MESSAGE_NOT_FOUND,
                "Unknown Person"));
    }
}
