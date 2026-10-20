package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Person;

/** Links an existing colleague to a department. */
public class ColleagueToDepartmentCommand extends Command {
    public static final String COMMAND_WORD = "link-colleague";
    public static final String MESSAGE_USAGE = "link-colleague: Links a colleague to a department. "
            + "Parameters: n/NAME d/DEPARTMENT";
    public static final String MESSAGE_SUCCESS = "%s linked to department: %s";
    public static final String MESSAGE_NOT_FOUND = "Colleague not found: %s";
    public static final String MESSAGE_DEPARTMENT_NOT_FOUND = "Department not found: %s";
    private final String colleagueName;
    private final Group department;

    /** Creates a command linking the named colleague to the given department. */
    public ColleagueToDepartmentCommand(String colleagueName, Group department) {
        this.colleagueName = requireNonNull(colleagueName);
        this.department = requireNonNull(department);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person colleague = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().fullName.equalsIgnoreCase(colleagueName))
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_NOT_FOUND, colleagueName)));

        if (!(colleague instanceof Colleague)) {
            throw new CommandException("Selected contact is not a colleague.");
        }

        Group departmentGroup;
        if (!model.getAddressBook().getGroupList().contains(department)) {
            throw new CommandException(String.format(MESSAGE_DEPARTMENT_NOT_FOUND, department.getGroupName().groupName));
        }
        Colleague linked = new Colleague(colleague.getName(), colleague.getPhone(), colleague.getEmail(),
                colleague.getAddress(), colleague.getTags(), department.getGroupName().groupName);

        model.setPerson(colleague, linked);
        return new CommandResult(String.format(MESSAGE_SUCCESS, colleagueName, department.getGroupName().groupName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ColleagueToDepartmentCommand otherCommand)) {
            return false;
        }
        return colleagueName.equals(otherCommand.colleagueName) && department.equals(otherCommand.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colleagueName, department);
    }
}
