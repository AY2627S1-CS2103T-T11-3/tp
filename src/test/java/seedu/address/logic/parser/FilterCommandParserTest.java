package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.PersonFilterCriteria;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.tag.Tag;

/**
 * Tests parsing and validation of optional address and tag filter criteria.
 */
public class FilterCommandParserTest {

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_emptyArgs_returnsUnrestrictedFilter() {
        FilterCommand expectedCommand = prepareCommand(Optional.empty(), Set.of());

        assertParseSuccess(parser, "", expectedCommand);
        assertParseSuccess(parser, " \t \r \n ", expectedCommand);
    }

    @Test
    public void parse_addressOnly_preservesPhrase() {
        FilterCommand expectedCommand = prepareCommand(Optional.of("Clementi Ave"), Set.of());

        assertParseSuccess(parser, " a/Clementi Ave", expectedCommand);
    }

    @Test
    public void parse_tagOnly_preservesCase() {
        assertParseSuccess(parser, " t/Student", prepareCommand(Optional.empty(), Set.of(new Tag("Student"))));
        assertParseSuccess(parser, " t/sTuDeNt", prepareCommand(Optional.empty(), Set.of(new Tag("sTuDeNt"))));
    }

    @Test
    public void parse_multipleTags_returnsAllRequiredTags() {
        FilterCommand expectedCommand = prepareCommand(Optional.empty(), Set.of(new Tag("Student"), new Tag("Year2")));

        assertParseSuccess(parser, " t/Student t/Year2", expectedCommand);
    }

    @Test
    public void parse_duplicateTags_deduplicatesExactValues() {
        FilterCommand expectedCommand = prepareCommand(Optional.empty(), Set.of(new Tag("Student")));

        assertParseSuccess(parser, " t/Student t/Student", expectedCommand);
    }

    @Test
    public void parse_differentlyCasedTags_preservesBothValues() {
        FilterCommand expectedCommand =
                prepareCommand(Optional.empty(), Set.of(new Tag("Student"), new Tag("student")));

        assertParseSuccess(parser, " t/Student t/student", expectedCommand);
    }

    @Test
    public void parse_combinedCriteria_returnsFilterCommand() {
        FilterCommand expectedCommand =
                prepareCommand(Optional.of("Clementi Ave"), Set.of(new Tag("Student"), new Tag("Year2")));

        assertParseSuccess(parser, " a/Clementi Ave t/Student t/Year2", expectedCommand);
        assertParseSuccess(parser, " t/Student a/Clementi Ave t/Year2", expectedCommand);
    }

    @Test
    public void parse_surroundingWhitespace_trimsValues() {
        FilterCommand expectedCommand =
                prepareCommand(Optional.of("cLeMeNtI Ave"), Set.of(new Tag("Student"), new Tag("Year2")));

        assertParseSuccess(parser, " \t a/  cLeMeNtI Ave  t/  Student  t/ Year2  \t ", expectedCommand);
    }

    @Test
    public void parse_internalAddressWhitespace_preservesPhrase() {
        FilterCommand expectedCommand = prepareCommand(Optional.of("Clementi  Ave"), Set.of());

        assertParseSuccess(parser, " a/Clementi  Ave", expectedCommand);
    }

    @Test
    public void parse_emptyAddress_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);

        assertParseFailure(parser, " a/", expectedMessage);
        assertParseFailure(parser, " a/     ", expectedMessage);
        assertParseFailure(parser, " a/ t/Student", expectedMessage);
    }

    @Test
    public void parse_emptyTag_throwsParseException() {
        assertParseFailure(parser, " t/", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/     ", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " a/Clementi t/", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_emptyTagAmongValidTags_throwsParseException() {
        assertParseFailure(parser, " t/Student t/", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/ t/Student", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/Student t/ t/Year2", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, " t/Student!", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/Year 2", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/year-2", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/Student t/Year2!", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateAddress_throwsParseException() {
        String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS);

        assertParseFailure(parser, " a/Clementi a/Bugis", expectedMessage);
        assertParseFailure(parser, " a/Clementi a/Clementi", expectedMessage);
        assertParseFailure(parser, " a/ a/Clementi", expectedMessage);
        assertParseFailure(parser, " a/Clementi t/Student a/Bugis", expectedMessage);
    }

    @Test
    public void parse_unexpectedPreamble_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);

        assertParseFailure(parser, " Clementi", expectedMessage);
        assertParseFailure(parser, " unexpected a/Clementi", expectedMessage);
        assertParseFailure(parser, " unexpected t/Student", expectedMessage);
    }

    @Test
    public void parse_unsupportedOrReversedPrefix_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);

        assertParseFailure(parser, " n/Alice", expectedMessage);
        assertParseFailure(parser, " /t Student", expectedMessage);
    }

    /**
     * Constructs the expected command independently of the parser.
     */
    private FilterCommand prepareCommand(Optional<String> addressKeyword, Set<Tag> requiredTags) {
        PersonFilterCriteria criteria = new PersonFilterCriteria(addressKeyword, requiredTags);
        return new FilterCommand(new PersonMatchesFilterPredicate(criteria));
    }
}
