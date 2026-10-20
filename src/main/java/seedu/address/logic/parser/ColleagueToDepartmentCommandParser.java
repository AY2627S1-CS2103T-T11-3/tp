package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.*;

import seedu.address.logic.commands.ColleagueToDepartmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.GroupType;

/** Parses the link-colleague command. */
public class ColleagueToDepartmentCommandParser implements Parser<ColleagueToDepartmentCommand> {
    @Override
    public ColleagueToDepartmentCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DEPARTMENT);
        if (map.getValue(PREFIX_NAME).isEmpty() || map.getValue(PREFIX_DEPARTMENT).isEmpty()
                || !map.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ColleagueToDepartmentCommand.MESSAGE_USAGE));
        }
        map.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DEPARTMENT);

        GroupName departmentName = ParserUtil.parseGroupName(map.getValue(PREFIX_DEPARTMENT).get());
        DepartmentGroup department = new DepartmentGroup(departmentName, GroupType.DEPARTMENT);

        return new ColleagueToDepartmentCommand(map.getValue(PREFIX_NAME).get(), department);
    }
}
