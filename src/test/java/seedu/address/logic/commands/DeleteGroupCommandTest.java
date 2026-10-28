package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalAddressBook.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_GROUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_GROUP;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Address;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteGroupCommand}.
 */
public class DeleteGroupCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_success() {
        Group groupToDelete = model.getGroupList().get(INDEX_FIRST_GROUP.getZeroBased());
        DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand(INDEX_FIRST_GROUP);

        String expectedMessage = String.format(DeleteGroupCommand.MESSAGE_DELETE_GROUP_SUCCESS,
                groupToDelete.getGroupName());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteGroup(groupToDelete);

        assertCommandSuccess(deleteGroupCommand, model, expectedMessage, expectedModel);
        assertFalse(model.getGroupList().contains(groupToDelete));
    }

    @Test
    public void execute_groupWithMembers_unassignsMembers() {
        AddressBook addressBook = new AddressBook();
        Group group = new ClassGroup(new GroupName("CS2103T T01"));
        Student student = new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"), Set.of(),
                "CS2103T T01", 2);
        Student unassignedStudent = new Student(new Name("Carol Lim"), new Phone("91234569"),
                new Email("carol@example.com"), new Address("123, Jurong West Ave 6, #08-113"), Set.of(),
                null, 2);
        addressBook.addGroup(group);
        addressBook.addPerson(student);
        addressBook.addPerson(unassignedStudent);
        ModelManager memberModel = new ModelManager(addressBook, new UserPrefs());

        DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand(Index.fromOneBased(1));
        String expectedMessage = String.format(DeleteGroupCommand.MESSAGE_DELETE_GROUP_SUCCESS,
                group.getGroupName());

        ModelManager expectedModel = new ModelManager(memberModel.getAddressBook(), new UserPrefs());
        expectedModel.deleteGroup(group);

        assertCommandSuccess(deleteGroupCommand, memberModel, expectedMessage, expectedModel);
        assertTrue(memberModel.getGroupList().isEmpty());
        assertNull(memberModel.getAddressBook().getPersonList().getFirst().getGroupId());
        assertNull(memberModel.getAddressBook().getPersonList().get(1).getGroupId());
    }

    @Test
    public void execute_sameNameDifferentTypes_unassignsOnlyMatchingMembers() throws Exception {
        AddressBook addressBook = new AddressBook();
        Group classGroup = new ClassGroup(new GroupName("Shared"));
        Group departmentGroup = new DepartmentGroup(new GroupName("Shared"));
        Student student = new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"), Set.of(),
                "Shared", 2);
        Colleague colleague = new Colleague(new Name("Bob Tan"), new Phone("91234568"),
                new Email("bob@example.com"), new Address("123, Jurong West Ave 6, #08-112"), Set.of(),
                "Shared");
        addressBook.addGroup(classGroup);
        addressBook.addGroup(departmentGroup);
        addressBook.addPerson(student);
        addressBook.addPerson(colleague);
        ModelManager memberModel = new ModelManager(addressBook, new UserPrefs());

        DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand(Index.fromOneBased(1));

        deleteGroupCommand.execute(memberModel);

        assertNull(memberModel.getAddressBook().getPersonList().get(0).getGroupId());
        assertEquals("Shared", memberModel.getAddressBook().getPersonList().get(1).getGroupId());
        assertEquals(1, memberModel.getGroupList().size());
        assertTrue(memberModel.getGroupList().contains(departmentGroup));

        DeleteGroupCommand deleteDepartmentGroupCommand = new DeleteGroupCommand(Index.fromOneBased(1));
        deleteDepartmentGroupCommand.execute(memberModel);

        assertNull(memberModel.getAddressBook().getPersonList().get(1).getGroupId());
        assertTrue(memberModel.getGroupList().isEmpty());
    }

    @Test
    public void execute_departmentGroup_doesNotUnassignStudentWithSameName() throws Exception {
        AddressBook addressBook = new AddressBook();
        Group departmentGroup = new DepartmentGroup(new GroupName("Shared"));
        Student student = new Student(new Name("Alice Pauline"), new Phone("91234567"),
                new Email("alice@example.com"), new Address("123, Jurong West Ave 6, #08-111"), Set.of(),
                "Shared", 2);
        Colleague colleague = new Colleague(new Name("Bob Tan"), new Phone("91234568"),
                new Email("bob@example.com"), new Address("123, Jurong West Ave 6, #08-112"), Set.of(),
                "Shared");
        addressBook.addGroup(departmentGroup);
        addressBook.addPerson(student);
        addressBook.addPerson(colleague);
        ModelManager memberModel = new ModelManager(addressBook, new UserPrefs());

        new DeleteGroupCommand(Index.fromOneBased(1)).execute(memberModel);

        assertEquals("Shared", memberModel.getAddressBook().getPersonList().get(0).getGroupId());
        assertNull(memberModel.getAddressBook().getPersonList().get(1).getGroupId());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getGroupList().size() + 1);
        DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand(outOfBoundIndex);

        assertCommandFailure(deleteGroupCommand, model, DeleteGroupCommand.MESSAGE_INVALID_GROUP_INDEX);
    }

    @Test
    public void equals() {
        DeleteGroupCommand deleteFirstCommand = new DeleteGroupCommand(INDEX_FIRST_GROUP);
        DeleteGroupCommand deleteSecondCommand = new DeleteGroupCommand(INDEX_SECOND_GROUP);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteGroupCommand deleteFirstCommandCopy = new DeleteGroupCommand(INDEX_FIRST_GROUP);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different command -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand(targetIndex);
        String expected = DeleteGroupCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteGroupCommand.toString());
    }
}
