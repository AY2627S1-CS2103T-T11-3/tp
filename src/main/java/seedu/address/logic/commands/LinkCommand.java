package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;

/** Links contacts selected from the currently displayed list. */
public class LinkCommand extends Command {
    public static final String COMMAND_WORD = "link-guardian";
    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Links a guardian to a student using their "
                    + "indices in the displayed contact list.\n"
                    + "Parameters: s/STUDENT_INDEX g/GUARDIAN_INDEX\n"
                    + "Example: " + COMMAND_WORD + " s/1 g/2";
    private final Index guardianIndex;
    private final Index studentIndex;

    /**
     * Creates a command linking contacts at the given displayed-list indices.
     */
    public LinkCommand(Index guardianIndex, Index studentIndex) {
        requireAllNonNull(guardianIndex, studentIndex);
        this.guardianIndex = guardianIndex;
        this.studentIndex = studentIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);

        List<Person> contacts = model.getFilteredPersonList();

        if (guardianIndex.getZeroBased() >= contacts.size()) {
            throw new CommandException("Guardian contact does not exist.");
        }
        if (studentIndex.getZeroBased() >= contacts.size()) {
            throw new CommandException("Student contact does not exist.");
        }

        Person selectedGuardian = contacts.get(guardianIndex.getZeroBased());
        Person student = contacts.get(studentIndex.getZeroBased());

        if (!(selectedGuardian instanceof Guardian)
                || selectedGuardian.getRole() != Role.GUARDIAN) {
            throw new CommandException("Selected contact is not a guardian.");
        }
        if (student.getRole() != Role.STUDENT) {
            throw new CommandException("Selected contact is not a student.");
        }
        Guardian guardian = (Guardian) selectedGuardian;

        try {
            model.linkGuardianToStudent(guardian, student);
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage(), e);
        }

        return new CommandResult(String.format(
                "Linked guardian %s to student %s.",
                guardian.getName(), student.getName()));
    }
}
