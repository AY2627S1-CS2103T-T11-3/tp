package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.EditGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses editgroup commands. */
public class EditGroupCommandParser implements Parser<EditGroupCommand> {
    @Override
    public EditGroupCommand parse(String args) throws ParseException {
        String[] parts = args.trim().split("\\s+", 2);
        if (parts.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditGroupCommand.MESSAGE_USAGE));
        }
        try {
            return new EditGroupCommand(ParserUtil.parseIndex(parts[0]),
                    ParserUtil.parseGroupName(parts[1].trim()));
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditGroupCommand.MESSAGE_USAGE), e);
        }
    }
}
