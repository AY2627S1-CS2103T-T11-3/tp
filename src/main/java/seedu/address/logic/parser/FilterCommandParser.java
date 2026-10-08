package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonFilterCriteria;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses address and tag criteria into a command that filters persons.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses optional address and tag criteria into a {@code FilterCommand}.
     * Empty arguments produce criteria that match all persons.
     *
     * @throws ParseException If arguments contain a preamble, duplicate address prefixes, or invalid criteria.
     */
    @Override
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ADDRESS, PREFIX_TAG);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ADDRESS);
        Optional<String> addressKeyword = parseAddressKeyword(argMultimap);
        Set<Tag> requiredTags = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        PersonFilterCriteria criteria = new PersonFilterCriteria(addressKeyword, requiredTags);
        PersonMatchesFilterPredicate predicate = new PersonMatchesFilterPredicate(criteria);
        return new FilterCommand(predicate);
    }

    /**
     * Returns the trimmed address keyword if present, rejecting a blank address criterion.
     */
    private Optional<String> parseAddressKeyword(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> addressKeyword = argMultimap.getValue(PREFIX_ADDRESS).map(String::trim);
        if (addressKeyword.isPresent() && addressKeyword.get().isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        return addressKeyword;
    }
}
