package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.LinkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses student and guardian indices into a LinkCommand.
 */
public class LinkCommandParser implements Parser<LinkCommand> {

    /**
     * Parses arguments in the form {@code s/STUDENT_INDEX g/GUARDIAN_INDEX}.
     *
     * @throws ParseException if either index is missing or invalid, a prefix
     *         is repeated, or unexpected text appears before the prefixes.
     */
    @Override
    public LinkCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_STUDENT, PREFIX_GUARDIAN);

        if (argMultimap.getValue(PREFIX_STUDENT).isEmpty()
                || argMultimap.getValue(PREFIX_GUARDIAN).isEmpty()
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, LinkCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_STUDENT, PREFIX_GUARDIAN);

        Index studentIndex =
                ParserUtil.parseIndex(argMultimap.getValue(PREFIX_STUDENT).get());
        Index guardianIndex =
                ParserUtil.parseIndex(argMultimap.getValue(PREFIX_GUARDIAN).get());

        // LinkCommand's constructor takes the guardian index first.
        return new LinkCommand(guardianIndex, studentIndex);
    }
}
