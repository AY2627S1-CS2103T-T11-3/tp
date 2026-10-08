package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ColleagueToDepartmentCommand;

public class ColleagueToDepartmentCommandParserTest {

    private final ColleagueToDepartmentCommandParser parser = new ColleagueToDepartmentCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() {
        assertParseSuccess(parser, " n/Alice Pauline d/Computer Science",
                new ColleagueToDepartmentCommand("Alice Pauline", "Computer Science"));
    }

    @Test
    public void parse_missingName_throwsParseException() {
        assertParseFailure(parser, " d/Computer Science",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ColleagueToDepartmentCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingDepartment_throwsParseException() {
        assertParseFailure(parser, " n/Alice Pauline",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ColleagueToDepartmentCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertParseFailure(parser, " n/Alice Pauline n/Bob Choo d/Computer Science",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(
                        CliSyntax.PREFIX_NAME));
    }
}
