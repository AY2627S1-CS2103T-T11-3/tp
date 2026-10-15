package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.PersonFilterCriteria;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.person.Role;
import seedu.address.model.tag.Tag;

/**
 * Tests parsing and validation of optional name, contact type, department, address, and tag criteria.
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
    public void parse_nameOnly_preservesPhraseAndTrimsWhitespace() {
        assertParseSuccess(parser, " n/ali", prepareCommand(Optional.of("ali"), Optional.empty(),
                Optional.empty(), Optional.empty(), Set.of()));
        assertParseSuccess(parser, " n/  Alice Tan  \t ", prepareCommand(Optional.of("Alice Tan"), Optional.empty(),
                Optional.empty(), Optional.empty(), Set.of()));
    }

    @Test
    public void parse_departmentOnly_preservesPhraseAndTrimsWhitespace() {
        FilterCommand expectedCommand = prepareCommand(Optional.empty(), Optional.empty(),
                Optional.of("food sci"), Optional.empty(), Set.of());

        assertParseSuccess(parser, " d/food sci", expectedCommand);
        assertParseSuccess(parser, " d/  food sci  \t ", expectedCommand);
    }

    @Test
    public void parse_studentTypeAndPrefixes_returnsStudentFilter() {
        FilterCommand expectedCommand = prepareCommand(Optional.empty(), Optional.of(Role.STUDENT),
                Optional.empty(), Optional.empty(), Set.of());

        for (String contactType : List.of("student", "stu", "s", "STUDENT", "sTu")) {
            assertParseSuccess(parser, " ct/" + contactType, expectedCommand);
        }
    }

    @Test
    public void parse_guardianTypeAndPrefixes_returnsGuardianFilter() {
        FilterCommand expectedCommand = prepareCommand(Optional.empty(), Optional.of(Role.GUARDIAN),
                Optional.empty(), Optional.empty(), Set.of());

        for (String contactType : List.of("guardian", "guar", "g", "GUARDIAN", "gUaR")) {
            assertParseSuccess(parser, " ct/" + contactType, expectedCommand);
        }
    }

    @Test
    public void parse_colleagueTypeAndPrefixes_returnsColleagueFilter() {
        FilterCommand expectedCommand = prepareCommand(Optional.empty(), Optional.of(Role.COLLEAGUE),
                Optional.empty(), Optional.empty(), Set.of());

        for (String contactType : List.of("colleague", "col", "c", "COLLEAGUE", "cOl")) {
            assertParseSuccess(parser, " ct/" + contactType, expectedCommand);
        }
    }

    @Test
    public void parse_invalidContactType_throwsParseException() {
        for (String contactType : List.of("teacher", "tudent", "u", "studentx", "student guardian", "colleague!")) {
            assertParseFailure(parser, " ct/" + contactType, FilterCommand.MESSAGE_INVALID_CONTACT_TYPE);
        }
    }

    @Test
    public void parse_emptyNewCriteria_throwsParseException() {
        for (Prefix prefix : List.of(PREFIX_NAME, PREFIX_CONTACT_TYPE, PREFIX_DEPARTMENT)) {
            String expectedMessage = String.format(FilterCommand.MESSAGE_EMPTY_CRITERION, prefix)
                    + "\n" + FilterCommand.MESSAGE_USAGE;

            assertParseFailure(parser, " " + prefix, expectedMessage);
            assertParseFailure(parser, " " + prefix + "  \t \r \n ", expectedMessage);
            assertParseFailure(parser, " " + prefix + " t/Student", expectedMessage);
        }
    }

    @Test
    public void parse_duplicateNewCriteria_throwsParseException() {
        for (Prefix prefix : List.of(PREFIX_NAME, PREFIX_CONTACT_TYPE, PREFIX_DEPARTMENT)) {
            String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(prefix);
            String firstValue = prefix.equals(PREFIX_CONTACT_TYPE) ? "stu" : "Alice";
            String secondValue = prefix.equals(PREFIX_CONTACT_TYPE) ? "guar" : "Bob";

            assertParseFailure(parser, " " + prefix + firstValue + " " + prefix + firstValue, expectedMessage);
            assertParseFailure(parser, " " + prefix + firstValue + " t/Student " + prefix + secondValue,
                    expectedMessage);
            assertParseFailure(parser, " " + prefix + " " + prefix + firstValue, expectedMessage);
        }
    }

    @Test
    public void parse_allCriteriaInDifferentOrders_returnsSameFilterCommand() {
        FilterCommand expectedCommand = prepareCommand(Optional.of("Alice Tan"), Optional.of(Role.COLLEAGUE),
                Optional.of("food sci"), Optional.of("Clementi Ave"), Set.of(new Tag("Committee"), new Tag("Staff")));

        assertParseSuccess(parser,
                " n/Alice Tan ct/cOl d/food sci a/Clementi Ave t/Committee t/Staff", expectedCommand);
        assertParseSuccess(parser,
                " ct/cOl t/Staff a/Clementi Ave n/Alice Tan t/Committee d/food sci", expectedCommand);
        assertParseSuccess(parser,
                " d/food sci n/Alice Tan t/Committee ct/cOl a/Clementi Ave t/Staff", expectedCommand);
        assertParseSuccess(parser,
                " a/Clementi Ave t/Staff ct/cOl d/food sci t/Committee n/Alice Tan", expectedCommand);
        assertParseSuccess(parser,
                " t/Committee a/Clementi Ave d/food sci t/Staff ct/cOl n/Alice Tan", expectedCommand);
    }

    @Test
    public void parse_allCriteriaWithWhitespace_trimsValues() {
        FilterCommand expectedCommand = prepareCommand(Optional.of("Alice Tan"), Optional.of(Role.COLLEAGUE),
                Optional.of("food sci"), Optional.of("Clementi Ave"), Set.of(new Tag("Committee")));

        assertParseSuccess(parser,
                " \t n/  Alice Tan \t ct/ cOl \r\n d/ food sci \n a/ Clementi Ave \t t/ Committee \t ",
                expectedCommand);
    }

    @Test
    public void parse_internalNameAndDepartmentWhitespace_preservesPhrases() {
        FilterCommand expectedCommand = prepareCommand(Optional.of("Alice  Tan"), Optional.empty(),
                Optional.of("Food  Science"), Optional.empty(), Set.of());

        assertParseSuccess(parser, " n/Alice  Tan d/Food  Science", expectedCommand);
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
        String expectedMessage = String.format(FilterCommand.MESSAGE_EMPTY_CRITERION, PREFIX_ADDRESS)
                + "\n" + FilterCommand.MESSAGE_USAGE;

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

        assertParseFailure(parser, " p/91234567", expectedMessage);
        assertParseFailure(parser, " /t Student", expectedMessage);
    }

    /**
     * Constructs the expected command independently of the parser.
     */
    private FilterCommand prepareCommand(Optional<String> addressKeyword, Set<Tag> requiredTags) {
        return prepareCommand(Optional.empty(), Optional.empty(), Optional.empty(), addressKeyword, requiredTags);
    }

    /**
     * Constructs the expected command from all supported criteria without calling the parser.
     */
    private FilterCommand prepareCommand(Optional<String> nameKeyword, Optional<Role> contactType,
            Optional<String> departmentKeyword, Optional<String> addressKeyword, Set<Tag> requiredTags) {
        PersonFilterCriteria criteria = new PersonFilterCriteria(nameKeyword, contactType, departmentKeyword,
                addressKeyword, requiredTags);
        return new FilterCommand(new PersonMatchesFilterPredicate(criteria));
    }
}
