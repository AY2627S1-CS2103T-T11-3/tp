package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonMatchesFilterPredicate;

/**
 * Lists persons who satisfy all supplied address and tag criteria.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists persons who satisfy all supplied address and tag criteria.\n"
            + "Addresses use case-insensitive substring matching; tags use case-sensitive exact matching.\n"
            + "All criteria must match. Omit all criteria to list all persons.\n"
            + "Specify at most one " + PREFIX_ADDRESS + " prefix; repeat " + PREFIX_TAG + " to require multiple tags.\n"
            + "Parameters: [" + PREFIX_ADDRESS + "ADDRESS] [" + PREFIX_TAG + "TAG]...\n"
            + "Examples:\n"
            + COMMAND_WORD + "\n"
            + COMMAND_WORD + " " + PREFIX_ADDRESS + "Clementi\n"
            + COMMAND_WORD + " " + PREFIX_TAG + "Student\n"
            + COMMAND_WORD + " " + PREFIX_TAG + "Student " + PREFIX_TAG + "Year2\n"
            + COMMAND_WORD + " " + PREFIX_ADDRESS + "Clementi " + PREFIX_TAG + "Student";

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
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
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
