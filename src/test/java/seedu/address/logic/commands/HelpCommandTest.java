package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.logic.commands.HelpCommand.SHOWING_HELP_MESSAGE;
import static seedu.address.testutil.TypicalAddressBook.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(SHOWING_HELP_MESSAGE, true, false);
        assertCommandSuccess(new HelpCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_populatedFilteredModel_preservesContactsGroupsAndFilter() {
        Model populatedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedPopulatedModel = new ModelManager(populatedModel.getAddressBook(), new UserPrefs());
        showPersonAtIndex(populatedModel, INDEX_FIRST_PERSON);
        showPersonAtIndex(expectedPopulatedModel, INDEX_FIRST_PERSON);
        CommandResult expectedResult = new CommandResult(SHOWING_HELP_MESSAGE, true, false);
        HelpCommand command = new HelpCommand();

        assertCommandSuccess(command, populatedModel, expectedResult, expectedPopulatedModel);
        assertEquals(expectedPopulatedModel.getAddressBook().getGroupList(),
                populatedModel.getAddressBook().getGroupList());
    }
}
