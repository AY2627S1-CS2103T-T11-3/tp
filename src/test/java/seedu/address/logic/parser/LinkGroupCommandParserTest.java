package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.LinkGroupCommand;

public class LinkGroupCommandParserTest {
    private final LinkGroupCommandParser parser = new LinkGroupCommandParser();

    @Test
    public void parse_validArgs_returnsCommand() {
        assertParseSuccess(parser, " n/Alice Pauline d/CS2103T T01",
                new LinkGroupCommand("Alice Pauline", "CS2103T T01"));
    }

    @Test
    public void parse_missingName_throwsParseException() {
        assertParseFailure(parser, " d/CS2103T T01",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, LinkGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingGroup_throwsParseException() {
        assertParseFailure(parser, " n/Alice Pauline",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, LinkGroupCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertParseFailure(parser, " n/Alice n/Bob d/CS2103T T01",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
    }
}
