package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PERSON_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR_OF_STUDY;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonFilterCriteria;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.person.Role;
import seedu.address.model.tag.Tag;

/**
 * Parses optional text, contact type, tag, person ID, group ID, and year-of-study filter criteria.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    // Normalize only separators before prefixes, preserving whitespace inside text phrases.
    private static final Pattern PREFIX_SEPARATOR_PATTERN = Pattern.compile("\\s+(?=(n/|ct/|d/|a/|t/|id/|gid/|yos/))");
    // Reject unknown parameter-like tokens instead of allowing them to be absorbed into a preceding value.
    private static final Pattern PREFIX_TOKEN_PATTERN = Pattern.compile("(?<!\\S)([A-Za-z][A-Za-z0-9]*/)");
    private static final Set<String> SUPPORTED_PREFIXES = Set.of(
            PREFIX_NAME.getPrefix(), PREFIX_CONTACT_TYPE.getPrefix(), PREFIX_DEPARTMENT.getPrefix(),
            PREFIX_ADDRESS.getPrefix(), PREFIX_TAG.getPrefix(), PREFIX_PERSON_ID.getPrefix(),
            PREFIX_GROUP_ID.getPrefix(), PREFIX_YEAR_OF_STUDY.getPrefix());

    /**
     * Parses optional criteria into a {@code FilterCommand}.
     * Empty arguments produce criteria that match all persons.
     *
     * @throws ParseException If arguments contain a preamble, duplicate single-value prefixes, or invalid criteria.
     */
    @Override
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        verifyNoUnsupportedPrefixes(args);
        String normalizedArgs = " " + PREFIX_SEPARATOR_PATTERN.matcher(args).replaceAll(" ");
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs,
                PREFIX_NAME, PREFIX_CONTACT_TYPE, PREFIX_DEPARTMENT, PREFIX_ADDRESS, PREFIX_TAG,
                PREFIX_PERSON_ID, PREFIX_GROUP_ID, PREFIX_YEAR_OF_STUDY);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CONTACT_TYPE, PREFIX_DEPARTMENT, PREFIX_ADDRESS,
                PREFIX_GROUP_ID, PREFIX_YEAR_OF_STUDY);
        Optional<String> nameKeyword = parseTextCriterion(argMultimap, PREFIX_NAME);
        Optional<String> contactTypeKeyword = parseTextCriterion(argMultimap, PREFIX_CONTACT_TYPE);
        Optional<String> departmentKeyword = parseTextCriterion(argMultimap, PREFIX_DEPARTMENT);
        Optional<String> addressKeyword = parseTextCriterion(argMultimap, PREFIX_ADDRESS);
        Set<Integer> personIds = parsePersonIds(argMultimap);
        Optional<Integer> groupId = parseNumericCriterion(argMultimap, PREFIX_GROUP_ID);
        Optional<Integer> yearOfStudy = parseNumericCriterion(argMultimap, PREFIX_YEAR_OF_STUDY);
        Optional<Role> contactType = parseContactType(contactTypeKeyword);
        Set<Tag> requiredTags = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        PersonFilterCriteria criteria = new PersonFilterCriteria(nameKeyword, contactType,
                departmentKeyword, addressKeyword, requiredTags, personIds, yearOfStudy);
        PersonMatchesFilterPredicate predicate = new PersonMatchesFilterPredicate(criteria);
        return new FilterCommand(predicate, groupId);
    }

    /**
     * Rejects unknown prefix-shaped tokens before the tokenizer can absorb them into a preceding value.
     */
    private static void verifyNoUnsupportedPrefixes(String args) throws ParseException {
        Matcher matcher = PREFIX_TOKEN_PATTERN.matcher(args);
        while (matcher.find()) {
            if (!SUPPORTED_PREFIXES.contains(matcher.group(1))) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
            }
        }
    }

    /**
     * Returns a stripped text criterion if present, rejecting a blank value.
     */
    private Optional<String> parseTextCriterion(ArgumentMultimap argMultimap, Prefix prefix) throws ParseException {
        Optional<String> keyword = argMultimap.getValue(prefix).map(String::strip);
        if (keyword.isPresent() && keyword.get().isBlank()) {
            throw new ParseException(String.format(FilterCommand.MESSAGE_EMPTY_CRITERION, prefix)
                    + "\n" + FilterCommand.MESSAGE_USAGE);
        }

        return keyword;
    }

    /** Parses all supplied person IDs, deduplicating equal numeric values. */
    private Set<Integer> parsePersonIds(ArgumentMultimap argMultimap) throws ParseException {
        Set<Integer> personIds = new HashSet<>();
        for (String value : argMultimap.getAllValues(PREFIX_PERSON_ID)) {
            personIds.add(parsePositiveInteger(value, PREFIX_PERSON_ID));
        }
        return personIds;
    }

    /** Parses an optional positive integer using the existing numeric validation and leading-zero rules. */
    private Optional<Integer> parseNumericCriterion(ArgumentMultimap argMultimap, Prefix prefix) throws ParseException {
        Optional<String> keyword = argMultimap.getValue(prefix);
        if (keyword.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(parsePositiveInteger(keyword.get(), prefix));
    }

    /** Parses a required positive integer, rejecting blank, malformed, or out-of-range values. */
    private int parsePositiveInteger(String value, Prefix prefix) throws ParseException {
        String keyword = value.strip();
        if (keyword.isBlank()) {
            throw new ParseException(String.format(FilterCommand.MESSAGE_EMPTY_CRITERION, prefix)
                    + "\n" + FilterCommand.MESSAGE_USAGE);
        }
        if (!StringUtil.isNonZeroUnsignedInteger(keyword)) {
            throw new ParseException(String.format(FilterCommand.MESSAGE_INVALID_NUMERIC_CRITERION, prefix));
        }

        return Integer.parseInt(keyword);
    }

    /** Resolves a full contact type or an unambiguous prefix without changing add-command validation. */
    private Optional<Role> parseContactType(Optional<String> keyword) throws ParseException {
        if (keyword.isEmpty()) {
            return Optional.empty();
        }

        String normalizedKeyword = keyword.get().toLowerCase(Locale.ROOT);
        List<Role> matches = Arrays.stream(Role.values())
                .filter(role -> role.name().toLowerCase(Locale.ROOT).startsWith(normalizedKeyword))
                .toList();
        if (matches.size() != 1) {
            throw new ParseException(FilterCommand.MESSAGE_INVALID_CONTACT_TYPE);
        }

        return Optional.of(matches.getFirst());
    }
}
