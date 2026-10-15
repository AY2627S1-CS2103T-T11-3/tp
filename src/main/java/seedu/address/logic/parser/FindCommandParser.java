package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonFilterCriteria;
import seedu.address.model.person.PersonMatchesFilterPredicate;

/**
 * Parses the legacy name-search alias using the same criteria and predicate as {@code filter n/NAME}.
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.strip();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.of(trimmedArgs), Optional.empty(),
                Optional.empty(), Optional.empty(), Set.of());
        return new FindCommand(new PersonMatchesFilterPredicate(criteria));
    }

}
