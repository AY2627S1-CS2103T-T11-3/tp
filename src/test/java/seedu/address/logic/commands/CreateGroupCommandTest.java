package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_TYPE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_TYPE_BOB;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.testutil.GroupBuilder;

public class CreateGroupCommandTest {

    private Model model = new ModelManager(new AddressBook(), new UserPrefs());

    @Test
    public void execute() {
        Group validGroup = new GroupBuilder().build();
        CommandResult commandResult = new CreateGroupCommand(validGroup).execute(model);

        assertEquals(CreateGroupCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
    }

    @Test
    public void equals() {
        Group validGroupAmy = new GroupBuilder().withName(VALID_GROUP_NAME_AMY).withType(VALID_GROUP_TYPE_AMY).build();
        Group validGroupBob = new GroupBuilder().withName(VALID_GROUP_NAME_BOB).withType(VALID_GROUP_TYPE_BOB).build();
        CreateGroupCommand createGroupCommandAlice = new CreateGroupCommand(validGroupAmy);
        CreateGroupCommand createGroupCommandBob = new CreateGroupCommand(validGroupBob);

        // same object -> returns true
        assertTrue(createGroupCommandAlice.equals(createGroupCommandAlice));

        // same values -> returns true
        Group validGroupAmyCopy = new GroupBuilder().withName(VALID_GROUP_NAME_AMY).withType(VALID_GROUP_TYPE_AMY).build();
        CreateGroupCommand createGroupCommandAliceCopy = new CreateGroupCommand(validGroupAmyCopy);
        assertTrue(createGroupCommandAlice.equals(createGroupCommandAliceCopy));

        // different types -> returns false
        assertFalse(createGroupCommandAlice.equals(1));

        // null -> returns false
        assertFalse(createGroupCommandAlice.equals(null));

        // different person -> returns false
        assertFalse(createGroupCommandAlice.equals(createGroupCommandBob));
    }

}
