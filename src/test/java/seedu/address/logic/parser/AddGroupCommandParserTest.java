package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_TYPE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddGroupCommand;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupType;
import seedu.address.testutil.GroupBuilder;

public class AddGroupCommandParserTest {

    private AddGroupCommandParser parser = new AddGroupCommandParser();
    private final String nonEmptyGroupName = "Math";
    private final String nonEmptyGroupType = "class";

    @Test
    public void parse_allFieldsPresent_success() {
        String userInput = nonEmptyGroupName + " " + PREFIX_GROUP_TYPE + nonEmptyGroupType;
        Group group = new GroupBuilder().withName(nonEmptyGroupName)
                .withType(GroupType.valueOf(nonEmptyGroupType.toUpperCase())).build();
        AddGroupCommand expectedCommand = new AddGroupCommand(group);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_departmentGroup_success() {
        String userInput = nonEmptyGroupName + " " + PREFIX_GROUP_TYPE + "department";
        Group group = new GroupBuilder().withName(nonEmptyGroupName)
                .withType(GroupType.DEPARTMENT).build();
        AddGroupCommand expectedCommand = new AddGroupCommand(group);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddGroupCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, AddGroupCommand.COMMAND_WORD, expectedMessage);

        // no group type
        assertParseFailure(
                parser,
                AddGroupCommand.COMMAND_WORD + nonEmptyGroupName,
                expectedMessage
        );
    }

}
