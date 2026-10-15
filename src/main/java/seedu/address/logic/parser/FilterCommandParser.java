package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonFilterCriteria;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.person.Role;
import seedu.address.model.tag.Tag;

/**
 * Parses optional name, contact type, department, address, and tag filter criteria.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    // Normalize only separators before prefixes, preserving whitespace inside text phrases.
    private static final Pattern PREFIX_SEPARATOR_PATTERN = Pattern.compile("\\s+(?=(n/|ct/|d/|a/|t/))");

    /**
     * Parses optional criteria into a {@code FilterCommand}.
     * Empty arguments produce criteria that match all persons.
     *
     * @throws ParseException If arguments contain a preamble, duplicate single-value prefixes, or invalid criteria.
     */
    @Override
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String normalizedArgs = " " + PREFIX_SEPARATOR_PATTERN.matcher(args).replaceAll(" ");
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs,
                PREFIX_NAME, PREFIX_CONTACT_TYPE, PREFIX_DEPARTMENT, PREFIX_ADDRESS, PREFIX_TAG);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CONTACT_TYPE, PREFIX_DEPARTMENT, PREFIX_ADDRESS);
        Optional<String> nameKeyword = parseTextCriterion(argMultimap, PREFIX_NAME);
        Optional<String> contactTypeKeyword = parseTextCriterion(argMultimap, PREFIX_CONTACT_TYPE);
        Optional<String> departmentKeyword = parseTextCriterion(argMultimap, PREFIX_DEPARTMENT);
        Optional<String> addressKeyword = parseTextCriterion(argMultimap, PREFIX_ADDRESS);
        Optional<Role> contactType = parseContactType(contactTypeKeyword);
        Set<Tag> requiredTags = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        PersonFilterCriteria criteria = new PersonFilterCriteria(nameKeyword, contactType,
                departmentKeyword, addressKeyword, requiredTags);
        PersonMatchesFilterPredicate predicate = new PersonMatchesFilterPredicate(criteria);
        return new FilterCommand(predicate);
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
