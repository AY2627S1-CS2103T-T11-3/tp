package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/** Links an existing colleague to a department. */
public class ColleagueToDepartmentCommand extends Command {
    public static final String COMMAND_WORD = "link";
    public static final String MESSAGE_USAGE = "link: Links a colleague to a department. "
            + "Parameters: n/NAME d/DEPARTMENT";
    public static final String MESSAGE_SUCCESS = "Colleague linked to department: %s";
    public static final String MESSAGE_NOT_FOUND = "Colleague not found: %s";
    private final String colleagueName;
    private final String department;

    /** Creates a command linking the named colleague to the given department. */
    public ColleagueToDepartmentCommand(String colleagueName, String department) {
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
        Person linked = new Person(colleague.getName(), colleague.getPhone(), colleague.getEmail(),
                colleague.getAddress(), colleague.getTags(), department);
        model.setPerson(colleague, linked);
        return new CommandResult(String.format(MESSAGE_SUCCESS, department));
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
