package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.model.Model;

public class CreateGroupCommand extends Command {

    public static final String COMMAND_WORD = "create_group";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Creates a new group. Duplicate group names "
            + "are not allowed.\n"
            + "Parameters: GROUP_NAME gt/ [GROUP_TYPE]\n"
            + "Example: " + COMMAND_WORD + " CS2103T-T01 "
            + "gt/ class";

    private final String groupName;
    private final String groupType;

    /**
     * Creates a CreateGroupCommand to add the specified {@code Group}
     * @param groupName name of the group
     * @param groupType must be one of "class" or "department"
     */
    public CreateGroupCommand(String groupName, String groupType) {
        requireAllNonNull(groupName, groupType);

        this.groupName = groupName;
        this.groupType = groupType;

        System.out.println(groupName);
        System.out.println(groupType);
    }

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult("create_group command executed successfully");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CreateGroupCommand otherCreateGroupCommand)) {
            return false;
        }

        return groupName.equals(otherCreateGroupCommand.groupName);
    }

}
