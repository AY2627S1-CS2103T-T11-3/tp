package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_GROUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;
import seedu.address.model.person.Address;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Email;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Student;

public class LinkGroupCommandTest {

    @Test
    public void execute_existingStudent_updatesClass() throws Exception {
        AddressBook addressBook = new AddressBook();
        addressBook.addGroup(new ClassGroup(new GroupName("CS2103T T01")));
        addressBook.addPerson(new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"),
                new HashSet<>(), null));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandSuccess(new LinkGroupCommand(INDEX_FIRST_PERSON, INDEX_FIRST_GROUP), model,
                "Alice Pauline linked to class: CS2103T T01", model);
        Person linked = model.getAddressBook().getPersonList().getFirst();
        assertInstanceOf(Student.class, linked);
        assertEquals("CS2103T T01", linked.getGroupId());
    }

    @Test
    public void execute_existingColleague_updatesDepartment() throws Exception {
        AddressBook addressBook = new AddressBook();
        addressBook.addGroup(new DepartmentGroup(new GroupName("Computer Science")));
        addressBook.addPerson(new Colleague(new Name("Bob Tan"), new Phone("91234568"),
                new Email("bob@example.com"), new Address("123, Jurong West Ave 6, #08-112"),
                new HashSet<>(), null));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandSuccess(new LinkGroupCommand(INDEX_FIRST_PERSON, INDEX_FIRST_GROUP), model,
                "Bob Tan linked to department: Computer Science", model);
        assertEquals("Computer Science", model.getAddressBook().getPersonList().getFirst().getGroupId());
    }

    @Test
    public void execute_invalidGroupIndex_throwsCommandException() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"),
                new HashSet<>(), null));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandFailure(new LinkGroupCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2)), model,
                LinkGroupCommand.MESSAGE_INVALID_GROUP_INDEX);
    }

    @Test
    public void execute_wrongGroupType_throwsCommandException() {
        AddressBook addressBook = new AddressBook();
        addressBook.addGroup(new DepartmentGroup(new GroupName("Computer Science")));
        addressBook.addPerson(new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"),
                new HashSet<>(), null));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandFailure(new LinkGroupCommand(INDEX_FIRST_PERSON, INDEX_FIRST_GROUP), model,
                String.format(LinkGroupCommand.MESSAGE_MISMATCH_PERSON_ROLE_GROUP_TYPE,
                        Role.STUDENT.toString().toLowerCase(), GroupType.DEPARTMENT.toString().toLowerCase()));
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        AddressBook addressBook = new AddressBook();
        addressBook.addGroup(new ClassGroup(new GroupName("CS2103T T01")));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandFailure(new LinkGroupCommand(Index.fromOneBased(2), INDEX_FIRST_GROUP), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_guardian_throwsCommandException() {
        AddressBook addressBook = new AddressBook();
        addressBook.addGroup(new ClassGroup(new GroupName("CS2103T T01")));
        addressBook.addPerson(new Guardian(new Name("Carol Lim"), new Phone("91234569"),
                new Email("carol@example.com"), new Address("123, Jurong West Ave 6, #08-113"),
                new HashSet<>()));
        Model model = new ModelManager(addressBook, new UserPrefs());

        assertCommandFailure(new LinkGroupCommand(INDEX_FIRST_PERSON, INDEX_FIRST_GROUP), model,
                LinkGroupCommand.MESSAGE_INVALID_PERSON_ROLE);
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        LinkGroupCommand command = new LinkGroupCommand(INDEX_FIRST_PERSON, INDEX_FIRST_GROUP);

        assertTrue(command.equals(command));
    }

    @Test
    public void equals_unrelatedObject_returnsFalse() {
        LinkGroupCommand command = new LinkGroupCommand(INDEX_FIRST_PERSON, INDEX_FIRST_GROUP);

        assertFalse(command.equals("not a command"));
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        LinkGroupCommand first = new LinkGroupCommand(INDEX_FIRST_PERSON, INDEX_FIRST_GROUP);
        LinkGroupCommand second = new LinkGroupCommand(INDEX_FIRST_PERSON, INDEX_FIRST_GROUP);

        assertEquals(first.hashCode(), second.hashCode());
    }

}
