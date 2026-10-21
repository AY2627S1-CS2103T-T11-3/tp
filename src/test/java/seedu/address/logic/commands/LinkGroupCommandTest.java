package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

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
import seedu.address.model.person.Student;

public class LinkGroupCommandTest {

    @Test
    public void execute_existingStudent_updatesClass() throws Exception {
        AddressBook addressBook = new AddressBook();
        addressBook.addGroup(new Group(new GroupName("CS2103T T01"), new GroupType("class")));
        addressBook.addPerson(new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"),
                new HashSet<>()));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandSuccess(new LinkGroupCommand("Alice Pauline", "CS2103T T01"), model,
                "Alice Pauline linked to class: CS2103T T01", model);
        Person linked = model.getAddressBook().getPersonList().getFirst();
        assertInstanceOf(Student.class, linked);
        assertEquals("CS2103T T01", linked.getGroupId());
    }

    @Test
    public void execute_existingColleague_updatesDepartment() throws Exception {
        AddressBook addressBook = new AddressBook();
        addressBook.addGroup(new Group(new GroupName("Computer Science"), new GroupType("department")));
        addressBook.addPerson(new Colleague(new Name("Bob Tan"), new Phone("91234568"),
                new Email("bob@example.com"), new Address("123, Jurong West Ave 6, #08-112"),
                new HashSet<>(), null));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandSuccess(new LinkGroupCommand("Bob Tan", "Computer Science"), model,
                "Bob Tan linked to department: Computer Science", model);
        assertEquals("Computer Science", model.getAddressBook().getPersonList().getFirst().getGroupId());
    }

    @Test
    public void execute_missingGroup_throwsCommandException() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"),
                new HashSet<>()));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandFailure(new LinkGroupCommand("Alice Pauline", "Missing Class"), model,
                "Group not found: Missing Class");
    }

    @Test
    public void execute_wrongGroupType_throwsCommandException() {
        AddressBook addressBook = new AddressBook();
        addressBook.addGroup(new Group(new GroupName("Computer Science"), new GroupType("department")));
        addressBook.addPerson(new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"),
                new HashSet<>()));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandFailure(new LinkGroupCommand("Alice Pauline", "Computer Science"), model,
                "Group not found: Computer Science");
    }

    @Test
    public void execute_missingPerson_throwsCommandException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandFailure(new LinkGroupCommand("Unknown Person", "Computer Science"), model,
                "Contact not found: Unknown Person");
    }

    @Test
    public void execute_invalidGroupName_throwsCommandException() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"),
                new HashSet<>()));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandFailure(new LinkGroupCommand("Alice Pauline", "!invalid"), model,
                "Group not found: !invalid");
    }

}
