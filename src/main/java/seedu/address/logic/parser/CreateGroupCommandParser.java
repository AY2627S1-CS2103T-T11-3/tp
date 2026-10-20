package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_TYPE;

import seedu.address.logic.commands.CreateGroupCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;
import seedu.address.model.person.Student;

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

        Group group = switch (groupType.getGType()) {
            case GroupType.GType.CLASS -> new ClassGroup(groupName, groupType);
            case GroupType.GType.DEPARTMENT -> new DepartmentGroup(groupName, groupType);
            default -> throw new ParseException("Invalid group type given");
        };

        return new CreateGroupCommand(group);
    }

}
