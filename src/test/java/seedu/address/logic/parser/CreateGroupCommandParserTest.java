package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_TYPE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CreateGroupCommand;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupType;
import seedu.address.testutil.GroupBuilder;

public class CreateGroupCommandParserTest {

    private CreateGroupCommandParser parser = new CreateGroupCommandParser();
    private final String nonEmptyGroupName = "Math";
    private final String nonEmptyGroupType = "class";

    @Test
    public void parse_allFieldsPresent_success() {
        String userInput = nonEmptyGroupName + " " + PREFIX_GROUP_TYPE + nonEmptyGroupType;
        Group group = new GroupBuilder().withName(nonEmptyGroupName).withType(GroupType.valueOf(nonEmptyGroupType.toUpperCase())).build();
        CreateGroupCommand expectedCommand = new CreateGroupCommand(group);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, CreateGroupCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, CreateGroupCommand.COMMAND_WORD, expectedMessage);

        // no group type
        assertParseFailure(
                parser,
                CreateGroupCommand.COMMAND_WORD + nonEmptyGroupName,
                expectedMessage
        );
    }

}
