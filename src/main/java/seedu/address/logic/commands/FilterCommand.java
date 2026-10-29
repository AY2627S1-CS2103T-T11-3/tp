package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonMatchesFilterPredicate;

/**
 * Lists persons who satisfy all supplied filter criteria.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_EMPTY_CRITERION = "Filter criterion %s must not be empty.";
    public static final String MESSAGE_INVALID_CONTACT_TYPE =
            "Contact type must be student, guardian, colleague, or an unambiguous prefix of one of these values.";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists persons who satisfy all supplied filter criteria.\n"
            + "Names, addresses, and departments use case-insensitive substring matching.\n"
            + "Contact types accept a full value or unambiguous prefix of student, guardian, or colleague "
            + "(case-insensitive). Tags use case-sensitive exact matching.\n"
            + "All criteria must match. Omit all criteria to list all persons.\n"
            + "Department criteria only match colleagues with a department.\n"
            + "Each prefix except " + PREFIX_TAG + " may appear at most once; repeat " + PREFIX_TAG
            + " to require multiple tags. Supplied values must not be empty.\n"
            + "Parameters: [" + PREFIX_NAME + "NAME] [" + PREFIX_CONTACT_TYPE + "CONTACT_TYPE] ["
            + PREFIX_DEPARTMENT + "DEPARTMENT] [" + PREFIX_ADDRESS + "ADDRESS] [" + PREFIX_TAG + "TAG]...\n"
            + "Examples:\n"
            + COMMAND_WORD + "\n"
            + COMMAND_WORD + " " + PREFIX_NAME + "ali\n"
            + COMMAND_WORD + " " + PREFIX_CONTACT_TYPE + "stu\n"
            + COMMAND_WORD + " " + PREFIX_CONTACT_TYPE + "col " + PREFIX_DEPARTMENT + "food sci\n"
            + COMMAND_WORD + " " + PREFIX_ADDRESS + "Clementi\n"
            + COMMAND_WORD + " " + PREFIX_TAG + "Student\n"
            + COMMAND_WORD + " " + PREFIX_TAG + "Student " + PREFIX_TAG + "Year2\n"
            + COMMAND_WORD + " " + PREFIX_NAME + "ali " + PREFIX_CONTACT_TYPE + "student "
            + PREFIX_ADDRESS + "Clementi " + PREFIX_TAG + "Student";

    private final PersonMatchesFilterPredicate predicate;

    /**
     * Constructs a command that lists persons matching the given predicate.
     *
     * @throws NullPointerException If {@code predicate} is null.
     */
    public FilterCommand(PersonMatchesFilterPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(Messages.getPersonsFoundMessage(model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }

        return predicate.equals(otherFilterCommand.predicate);
    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
