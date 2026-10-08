package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_TYPE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CreateGroupCommand;

public class CreateGroupCommandParserTest {

    private CreateGroupCommandParser parser = new CreateGroupCommandParser();
    private final String nonEmptyGroupName = "CS2103T-T01";
    private final String nonEmptyGroupType = "class";

    @Test
    public void parse_allFieldsPresent_success() {
        String userInput = nonEmptyGroupName + " " + PREFIX_GROUP_TYPE + nonEmptyGroupType;
        CreateGroupCommand expectedCommand = new CreateGroupCommand(nonEmptyGroupName, nonEmptyGroupType);
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
