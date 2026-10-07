package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_TYPE;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CreateGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class CreateGroupCommandParser implements Parser<CreateGroupCommand> {

    public CreateGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_GROUP_TYPE);

        if (argMultimap.getValue(PREFIX_GROUP_TYPE).isEmpty()
                || argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CreateGroupCommand.MESSAGE_USAGE));
        }

        return new CreateGroupCommand(
                argMultimap.getPreamble(),
                argMultimap.getValue(PREFIX_GROUP_TYPE).orElse("class")
        );
    }

}
