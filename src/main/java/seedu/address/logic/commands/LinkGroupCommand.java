package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
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

    private final Index personIndex;
    private final String groupName;

    /** Creates a command to link a named contact to a named group. */
    public LinkGroupCommand(Index personIndex, String groupName) {
        requireAllNonNull(personIndex, groupName);
        this.personIndex = personIndex;
        this.groupName = requireNonNull(groupName);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        List<Group> shownGroupList = model.getGroupList();

        if (personIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToLink = lastShownList.get(personIndex.getZeroBased());

        String groupType = personToLink instanceof Student ? "class"
                : personToLink instanceof Colleague ? "department" : null;
        if (groupType == null) {
            throw new CommandException("Selected contact cannot be linked to a class or department.");
        }

        Group group;
        try {
            group = groupType.equals("class")
                    ? new ClassGroup(new GroupName(groupName))
                    : new DepartmentGroup(new GroupName(groupName));
        } catch (IllegalArgumentException e) {
            throw new CommandException(String.format(MESSAGE_GROUP_NOT_FOUND, groupName));
        }
        if (!model.getGroupList().contains(group)) {
            throw new CommandException(String.format(MESSAGE_GROUP_NOT_FOUND, groupName));
        }

        Person linked;
        if (personToLink instanceof Student student) {
            linked = new Student(personToLink.getName(), personToLink.getPhone(), personToLink.getEmail(),
                    personToLink.getAddress(), personToLink.getTags(), personToLink.getId().getRawId(), groupName,
                    student.getYearOfStudy());
        } else {
            linked = new Colleague(personToLink.getName(), personToLink.getPhone(), personToLink.getEmail(),
                    personToLink.getAddress(), personToLink.getTags(), personToLink.getId().getRawId(), groupName);
        }
        model.setPerson(personToLink, linked);
        return new CommandResult(String.format("%s linked to %s: %s", personToLink.getName(), groupType, groupName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LinkGroupCommand otherCommand)) {
            return false;
        }
        return personIndex.equals(otherCommand.personIndex) && groupName.equals(otherCommand.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personIndex, groupName);
    }
}
