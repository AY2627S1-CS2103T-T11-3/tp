package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_GROUP;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.LinkGroupCommand;

public class LinkGroupCommandParserTest {
    private final LinkGroupCommandParser parser = new LinkGroupCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() {
        assertParseSuccess(parser, " pid/1 gid/1",
                new LinkGroupCommand(INDEX_FIRST_PERSON, INDEX_FIRST_GROUP));
    }

    @Test
    public void parse_missingPersonIndex_throwsParseException() {
        assertParseFailure(parser, " gid/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, LinkGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingGroupIndex_throwsParseException() {
        assertParseFailure(parser, " pid/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, LinkGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertParseFailure(parser, " pid/1 pid/2 gid/1",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_PERSON_INDEX));
    }

    @Test
    public void parse_preamble_throwsParseException() {
        assertParseFailure(parser, "unexpected pid/1 gid/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, LinkGroupCommand.MESSAGE_USAGE));
    }
}
