package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_NAME_CLASS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_NAME_DEPARTMENT;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupType;
import seedu.address.testutil.GroupBuilder;

public class AddGroupCommandTest {

    private Model model = new ModelManager(new AddressBook(), new UserPrefs());

    @Test
    public void execute() {
        Group validGroup = new GroupBuilder().build();
        CommandResult commandResult = new AddGroupCommand(validGroup).execute(model);

        assertEquals(AddGroupCommand.MESSAGE_SUCCESS, commandResult.getFeedbackToUser());
    }

    @Test
    public void equals() {
        Group validGroupAmy = new GroupBuilder().withName(VALID_GROUP_NAME_CLASS)
                .withType(GroupType.CLASS).build();
        Group validGroupBob = new GroupBuilder().withName(VALID_GROUP_NAME_DEPARTMENT)
                .withType(GroupType.DEPARTMENT).build();
        AddGroupCommand addGroupCommandAlice = new AddGroupCommand(validGroupAmy);
        AddGroupCommand addGroupCommandBob = new AddGroupCommand(validGroupBob);

        // same object -> returns true
        assertTrue(addGroupCommandAlice.equals(addGroupCommandAlice));

        // same values -> returns true
        Group validGroupAmyCopy = new GroupBuilder().withName(VALID_GROUP_NAME_CLASS).withType(GroupType.CLASS).build();
        AddGroupCommand addGroupCommandAliceCopy = new AddGroupCommand(validGroupAmyCopy);
        assertTrue(addGroupCommandAlice.equals(addGroupCommandAliceCopy));

        // different types -> returns false
        assertFalse(addGroupCommandAlice.equals(1));

        // null -> returns false
        assertFalse(addGroupCommandAlice.equals(null));

        // different person -> returns false
        assertFalse(addGroupCommandAlice.equals(addGroupCommandBob));
    }

}
