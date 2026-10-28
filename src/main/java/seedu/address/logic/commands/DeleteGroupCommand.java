package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;

/**
 * Deletes a group identified using its displayed index from the address book.
 */
public class DeleteGroupCommand extends Command {

    public static final String COMMAND_WORD = "deletegroup";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the group identified by the index number used in the displayed group list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_INVALID_GROUP_INDEX = "The group index provided is invalid.";

    public static final String MESSAGE_DELETE_GROUP_SUCCESS = "Group deleted successfully.";

    private final Index targetIndex;

    public DeleteGroupCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Group> shownGroupList = model.getGroupList();

        if (targetIndex.getZeroBased() >= shownGroupList.size()) {
            throw new CommandException(MESSAGE_INVALID_GROUP_INDEX);
        }

        Group groupToDelete = shownGroupList.get(targetIndex.getZeroBased());
        model.deleteGroup(groupToDelete);
        return new CommandResult(MESSAGE_DELETE_GROUP_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteGroupCommand otherDeleteGroupCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteGroupCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }

}
