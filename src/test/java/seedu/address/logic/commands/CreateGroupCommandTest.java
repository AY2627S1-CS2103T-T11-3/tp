package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_TYPE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_TYPE_BOB;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class CreateGroupCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute() {
        CommandResult commandResult = new CreateGroupCommand(
                VALID_GROUP_NAME_AMY,
                VALID_GROUP_TYPE_AMY
        ).execute(model);

        assertEquals(CreateGroupCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
    }

    @Test
    public void equals() {
        CreateGroupCommand createGroupCommandAlice = new CreateGroupCommand(
                VALID_GROUP_NAME_AMY,
                VALID_GROUP_TYPE_AMY
        );
        CreateGroupCommand createGroupCommandBob = new CreateGroupCommand(
                VALID_GROUP_NAME_BOB,
                VALID_GROUP_TYPE_BOB
        );

        // same object -> returns true
        assertTrue(createGroupCommandAlice.equals(createGroupCommandAlice));

        // same values -> returns true
        CreateGroupCommand createGroupCommandAliceCopy = new CreateGroupCommand(
                VALID_GROUP_NAME_AMY,
                VALID_GROUP_TYPE_AMY
        );
        assertTrue(createGroupCommandAlice.equals(createGroupCommandAliceCopy));

        // different types -> returns false
        assertFalse(createGroupCommandAlice.equals(1));

        // null -> returns false
        assertFalse(createGroupCommandAlice.equals(null));

        // different person -> returns false
        assertFalse(createGroupCommandAlice.equals(createGroupCommandBob));
    }

}
