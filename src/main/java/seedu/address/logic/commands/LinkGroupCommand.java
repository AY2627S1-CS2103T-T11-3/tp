package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Person;
import seedu.address.model.person.Student;

/** Links a student or colleague to the appropriate group. */
public class LinkGroupCommand extends Command {
    public static final String COMMAND_WORD = "link";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Links a student to a class or a colleague to a department. "
            + "Parameters: n/NAME d/GROUP";
    public static final String MESSAGE_NOT_FOUND = "Contact not found: %s";
    public static final String MESSAGE_GROUP_NOT_FOUND = "Group not found: %s";

    private final String personName;
    private final String groupName;

    /** Creates a command to link a named contact to a named group. */
    public LinkGroupCommand(String personName, String groupName) {
        this.personName = requireNonNull(personName);
        this.groupName = requireNonNull(groupName);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person person = model.getAddressBook().getPersonList().stream()
                .filter(candidate -> candidate.getName().fullName.equalsIgnoreCase(personName))
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_NOT_FOUND, personName)));

        String groupType = person instanceof Student ? "class"
                : person instanceof Colleague ? "department" : null;
        if (groupType == null) {
            throw new CommandException("Selected contact cannot be linked to a class or department.");
        }

        Group group;
        try {
            group = groupType.equals("class")
                    ? new ClassGroup(new GroupName(groupName), Set.of())
                    : new DepartmentGroup(new GroupName(groupName), Set.of());
        } catch (IllegalArgumentException e) {
            throw new CommandException(String.format(MESSAGE_GROUP_NOT_FOUND, groupName));
        }
        if (!model.getGroupList().contains(group)) {
            throw new CommandException(String.format(MESSAGE_GROUP_NOT_FOUND, groupName));
        }

        Person linked;
        if (person instanceof Student student) {
            linked = new Student(person.getName(), person.getPhone(), person.getEmail(),
                    person.getAddress(), person.getTags(), person.getId().getRawId(), groupName,
                    student.getYearOfStudy());
        } else {
            linked = new Colleague(person.getName(), person.getPhone(), person.getEmail(),
                    person.getAddress(), person.getTags(), person.getId().getRawId(), groupName);
        }
        model.setPerson(person, linked);
        return new CommandResult(String.format("%s linked to %s: %s", personName, groupType, groupName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LinkGroupCommand otherCommand)) {
            return false;
        }
        return personName.equals(otherCommand.personName) && groupName.equals(otherCommand.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personName, groupName);
    }
}
