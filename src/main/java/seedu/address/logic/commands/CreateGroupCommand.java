package seedu.address.logic.commands;

import seedu.address.model.Model;

public class CreateGroupCommand extends Command {

    public static final String COMMAND_WORD = "create_group";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult("create_group command executed successfully");
    }

}
