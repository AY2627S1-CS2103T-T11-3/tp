package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalAddressBook.getTypicalAddressBook;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;
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
    public void execute_existingColleague_updatesDepartment() throws Exception {
        ColleagueToDepartmentCommand command = new ColleagueToDepartmentCommand("Alice Pauline", "Computer Science");
        AddressBook ab = new AddressBook();
        ab.addGroup(new Group(new GroupName("Computer Science"), new GroupType("department"), new HashSet<>()));
        ab.addPerson(new Colleague(new Name("Alice Pauline"), new Phone("91234567"), new Email("alice@example.com"),
                new Address("123, Jurong West Ave 6, #08-111"), new HashSet<Tag>(), null));
        Model expectedModel = new ModelManager(ab, new UserPrefs());
        Person original = expectedModel.getAddressBook().getPersonList().get(0);

        assertCommandSuccess(command, expectedModel,
                String.format(ColleagueToDepartmentCommand.MESSAGE_SUCCESS, "Alice Pauline", "Computer Science"),
                expectedModel);
        Person firstPerson = expectedModel.getAddressBook().getPersonList().getFirst();
        assertInstanceOf(Colleague.class, firstPerson);
        assertEquals("Computer Science", ((Colleague) firstPerson).getDepartment());
        assertEquals("Computer Science", firstPerson.getGroupId());
    }

    @Test
    public void execute_missingDepartment_throwsCommandException() {
        AddressBook ab = new AddressBook();
        ab.addPerson(new Colleague(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"),
                new HashSet<Tag>(), null));
        Model colleagueModel = new ModelManager(ab, new UserPrefs());
        ColleagueToDepartmentCommand command =
                new ColleagueToDepartmentCommand("Alice Pauline", "Missing Department");

        assertCommandFailure(command, colleagueModel,
                String.format(ColleagueToDepartmentCommand.MESSAGE_DEPARTMENT_NOT_FOUND, "Missing Department"));
    }

    @Test
    public void execute_missingColleague_throwsCommandException() {
        ColleagueToDepartmentCommand command =
                new ColleagueToDepartmentCommand("Unknown Person", "Computer Science");
        assertCommandFailure(command, model, String.format(ColleagueToDepartmentCommand.MESSAGE_NOT_FOUND,
                "Unknown Person"));
    }
}
