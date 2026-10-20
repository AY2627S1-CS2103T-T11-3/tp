package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_TYPE;

import seedu.address.logic.commands.CreateGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;

/**
 * Parses input arguments and creates a new CreateGroupCommand object
 */
public class CreateGroupCommandParser implements Parser<CreateGroupCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the CreateGroupCommand
     * and returns an CreateGroupCommand object for execution.
     * @param args String of arguments
     * @return CreateGroupCommand object for execution
     * @throws ParseException if the user input does not conform to the expected format
     */
    public CreateGroupCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_GROUP_TYPE);

        if (argMultimap.getValue(PREFIX_GROUP_TYPE).isEmpty()
                || argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CreateGroupCommand.MESSAGE_USAGE));
        }

        GroupName groupName = ParserUtil.parseGroupName(argMultimap.getPreamble());
        GroupType groupType = ParserUtil.parseGroupType(argMultimap.getValue(PREFIX_GROUP_TYPE).get());

        Group group = switch (groupType) {
            case GroupType.CLASS -> new ClassGroup(groupName);
            case GroupType.DEPARTMENT -> new DepartmentGroup(groupName);
            default -> throw new ParseException("Invalid group type given");
        };

        return new CreateGroupCommand(group);
    }

}
