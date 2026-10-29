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
import seedu.address.model.group.GroupType;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;
import seedu.address.model.person.Student;

/** Links a student or colleague to the appropriate group. */
public class LinkGroupCommand extends Command {
    public static final String COMMAND_WORD = "link";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Links a student to a class or a colleague to a department. "
            + "Parameters: pid/PERSON_INDEX gid/GROUP_INDEX";

    public static final String MESSAGE_INVALID_GROUP_INDEX = "The group index provided is invalid.";
    public static final String MESSAGE_MISMATCH_PERSON_ROLE_GROUP_TYPE = "Cannot link %1$s to %2$s.";
    private static final String MESSAGE_LINK_GROUP_SUCCESS = "%s linked to %s: %s";

    private final Index personIndex;
    private final Index groupIndex;

    /** Creates a command to link a named contact to a named group. */
    public LinkGroupCommand(Index personIndex, Index groupIndex) {
        requireAllNonNull(personIndex, groupIndex);
        this.personIndex = personIndex;
        this.groupIndex = groupIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        List<Group> shownGroupList = model.getGroupList();

        if (personIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        if (groupIndex.getZeroBased() >= shownGroupList.size()) {
            throw new CommandException(MESSAGE_INVALID_GROUP_INDEX);
        }

        Person personToLink = lastShownList.get(personIndex.getZeroBased());
        Role personToLinkRole = personToLink.getRole();
        Group groupToLink = shownGroupList.get(groupIndex.getZeroBased());
        GroupType groupToLinkType = groupToLink.getGroupType();

        switch (personToLinkRole) {
            case STUDENT:
                if (groupToLinkType != GroupType.CLASS) {
                    throw new CommandException(
                            String.format(MESSAGE_MISMATCH_PERSON_ROLE_GROUP_TYPE, personToLinkRole, groupToLinkType));
                }
                break;
            case COLLEAGUE:
                if (groupToLinkType != GroupType.DEPARTMENT) {
                    throw new CommandException(
                            String.format(MESSAGE_MISMATCH_PERSON_ROLE_GROUP_TYPE, personToLinkRole, groupToLinkType));
                }
                break;
            case GUARDIAN:
            default:
                throw new CommandException("Selected contact cannot be linked to a class or department.");
        }

        Person linked;
        if (personToLink instanceof Student student) {
            linked = new Student(personToLink.getName(), personToLink.getPhone(), personToLink.getEmail(),
                    personToLink.getAddress(), personToLink.getTags(), personToLink.getId().getRawId(),
                    groupToLink.getGroupName().groupName, student.getYearOfStudy());
        } else {
            linked = new Colleague(personToLink.getName(), personToLink.getPhone(), personToLink.getEmail(),
                    personToLink.getAddress(), personToLink.getTags(), personToLink.getId().getRawId(),
                    groupToLink.getGroupName().groupName);
        }
        model.setPerson(personToLink, linked);
        return new CommandResult(String.format(MESSAGE_LINK_GROUP_SUCCESS, personToLink.getName(),
                groupToLink.getGroupType().toString().toLowerCase(), groupToLink.getGroupName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LinkGroupCommand otherCommand)) {
            return false;
        }
        return personIndex.equals(otherCommand.personIndex) && groupIndex.equals(otherCommand.groupIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personIndex, groupIndex);
    }
}
