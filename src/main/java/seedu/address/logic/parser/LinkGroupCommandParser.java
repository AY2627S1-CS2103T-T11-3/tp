package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.LinkGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the generic link command. */
public class LinkGroupCommandParser implements Parser<LinkGroupCommand> {
    @Override
    public LinkGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DEPARTMENT);
        if (map.getValue(PREFIX_NAME).isEmpty() || map.getValue(PREFIX_DEPARTMENT).isEmpty()
                || !map.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    LinkGroupCommand.MESSAGE_USAGE));
        }
        map.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DEPARTMENT);
        return new LinkGroupCommand(map.getValue(PREFIX_NAME).get(), map.getValue(PREFIX_DEPARTMENT).get());
    }
}
