package seedu.address.logic.commands;

import seedu.address.model.Model;

public class CreateGroupCommand extends Command {

    public static final String COMMAND_WORD = "create_group";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Creates a new group. Duplicate group names "
            + "are not allowed.\n"
            + "Parameters: GROUP_NAME gt/ [GROUP_TYPE]\n"
            + "Example: " + COMMAND_WORD + "CS2103T-T01"
            + "gt/ class";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult("create_group command executed successfully");
    }

}
