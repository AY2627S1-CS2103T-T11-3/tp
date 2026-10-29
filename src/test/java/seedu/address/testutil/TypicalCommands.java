package seedu.address.testutil;

import java.util.List;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddGroupCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteGroupCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.LinkCommand;
import seedu.address.logic.commands.LinkGroupCommand;
import seedu.address.logic.commands.ListCommand;

/**
 * Shared command names for the offline Help reference tests.
 */
public final class TypicalCommands {
    public static final List<String> ALL_COMMAND_NAMES = List.of(
            AddCommand.COMMAND_WORD, EditCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
            FilterCommand.COMMAND_WORD, AddGroupCommand.COMMAND_WORD, DeleteGroupCommand.COMMAND_WORD,
            LinkGroupCommand.COMMAND_WORD, LinkCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD, HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD);

    private TypicalCommands() {}
}
