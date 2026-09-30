package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;

public class RemarkCommandParserTest {
    private final RemarkCommandParser parser = new RemarkCommandParser();
    private final String nonEmptyRemark = "Some remark.";

    @Test
    public void parse_indexSpecified_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_REMARK + nonEmptyRemark;
        assertParseSuccess(parser, userInput, new RemarkCommand(INDEX_FIRST_PERSON, nonEmptyRemark));

        userInput = INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_REMARK;
        assertParseSuccess(parser, userInput, new RemarkCommand(INDEX_FIRST_PERSON, ""));
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD, expectedMessage);
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD + " " + nonEmptyRemark, expectedMessage);
    }
}
