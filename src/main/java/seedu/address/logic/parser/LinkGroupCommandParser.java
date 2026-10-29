package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PERSON_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.LinkGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the generic link command. */
public class LinkGroupCommandParser implements Parser<LinkGroupCommand> {
    @Override
    public LinkGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_PERSON_INDEX, PREFIX_GROUP_INDEX);

        if (map.getValue(PREFIX_PERSON_INDEX).isEmpty() || map.getValue(PREFIX_GROUP_INDEX).isEmpty()
                || !map.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    LinkGroupCommand.MESSAGE_USAGE));
        }

        map.verifyNoDuplicatePrefixesFor(PREFIX_PERSON_INDEX, PREFIX_GROUP_INDEX);
        Index personIndex = ParserUtil.parseIndex(map.getValue(PREFIX_PERSON_INDEX).get());
        Index groupIndex = ParserUtil.parseIndex(map.getValue(PREFIX_GROUP_INDEX).get());

        return new LinkGroupCommand(personIndex, groupIndex);
    }
}
