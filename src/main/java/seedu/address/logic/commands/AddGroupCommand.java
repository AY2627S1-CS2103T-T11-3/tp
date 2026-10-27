package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.model.Model;
import seedu.address.model.group.Group;

/**
 * Creates a group in the address book.
 * A group can be either a class or a department.
 */
public class AddGroupCommand extends Command {

    public static final String COMMAND_WORD = "addgroup";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Creates a new group. Duplicate group names "
            + "are not allowed.\n"
            + "Parameters: GROUP_NAME gt/ [GROUP_TYPE]\n"
            + "Example: " + COMMAND_WORD + " CS2103T-T01 "
            + "gt/ class";

    public static final String MESSAGE_SUCCESS = "addgroup command executed successfully";

    private final Group toAdd;

    /**
     * Creates a CreateGroupCommand to add the specified {@code Group}
     */
    public AddGroupCommand(Group group) {
        requireAllNonNull(group);
        this.toAdd = group;
    }

    @Override
    public CommandResult execute(Model model) {
        model.addGroup(toAdd);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddGroupCommand otherAddGroupCommand)) {
            return false;
        }

        return toAdd.equals(otherAddGroupCommand.toAdd);
    }

}
