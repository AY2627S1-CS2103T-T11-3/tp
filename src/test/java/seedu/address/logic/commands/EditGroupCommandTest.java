package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.GroupName;

public class EditGroupCommandTest {
    @Test
    public void execute_validIndex_editsName() {
        AddressBook book = new AddressBook();
        book.addGroup(new ClassGroup(new GroupName("Old Class")));
        Model model = new ModelManager(book, new UserPrefs());

        assertCommandSuccess(new EditGroupCommand(Index.fromOneBased(1), new GroupName("New Class")), model,
                "Edited group: New Class", model);
        assertEquals(new GroupName("New Class"), model.getGroupList().getFirst().getGroupName());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        assertCommandFailure(new EditGroupCommand(Index.fromOneBased(1), new GroupName("New Class")), model,
                EditGroupCommand.MESSAGE_INVALID_GROUP_INDEX);
    }
}
