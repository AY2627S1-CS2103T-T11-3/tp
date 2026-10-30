package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

/** Edits only the name of an existing group. */
public class EditGroupCommand extends Command {
    public static final String COMMAND_WORD = "editgroup";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits a group's name. Parameters: INDEX NEW_GROUP_NAME";
    public static final String MESSAGE_INVALID_GROUP_INDEX = "The group index provided is invalid.";
    public static final String MESSAGE_SUCCESS = "Edited group: %1$s";

    private final Index index;
    private final GroupName newName;

    /**
     * Creates a command to rename the group at {@code index}.
     *
     * @param index index of the group in the displayed group list
     * @param newName replacement group name
     */
    public EditGroupCommand(Index index, GroupName newName) {
        this.index = requireNonNull(index);
        this.newName = requireNonNull(newName);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Group> groups = model.getGroupList();
        if (index.getZeroBased() >= groups.size()) {
            throw new CommandException(MESSAGE_INVALID_GROUP_INDEX);
        }
        Group target = groups.get(index.getZeroBased());
        Group edited = target instanceof ClassGroup ? new ClassGroup(newName) : new DepartmentGroup(newName);
        model.setGroup(target, edited);
        return new CommandResult(String.format(MESSAGE_SUCCESS, newName));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof EditGroupCommand command
                && index.equals(command.index) && newName.equals(command.newName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("newName", newName).toString();
    }
}
