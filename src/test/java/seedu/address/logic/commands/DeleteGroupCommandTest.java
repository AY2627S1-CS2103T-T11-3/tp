package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalAddressBook.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_GROUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_GROUP;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;

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

        String expectedMessage = DeleteGroupCommand.MESSAGE_DELETE_GROUP_SUCCESS;

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteGroup(groupToDelete);

        assertCommandSuccess(deleteGroupCommand, model, expectedMessage, expectedModel);
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

        // different person -> returns false
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
