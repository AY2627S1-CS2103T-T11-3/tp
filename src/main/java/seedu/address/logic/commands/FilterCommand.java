package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PERSON_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR_OF_STUDY;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.person.Student;

/**
 * Lists persons who satisfy all supplied filter criteria.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_EMPTY_CRITERION = "Filter criterion %s must not be empty.";
    public static final String MESSAGE_INVALID_CONTACT_TYPE =
            "Contact type must be student, guardian, colleague, or an unambiguous prefix of one of these values.";
    public static final String MESSAGE_INVALID_NUMERIC_CRITERION =
            "Filter criterion %s must be a positive integer from 1 to " + Integer.MAX_VALUE + ".";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists matching contacts.\n"
            + "Parameters: [" + PREFIX_NAME + "NAME] [" + PREFIX_CONTACT_TYPE + "CONTACT_TYPE] ["
            + PREFIX_DEPARTMENT + "DEPARTMENT] [" + PREFIX_ADDRESS + "ADDRESS] ["
            + PREFIX_PERSON_ID + "PERSON_ID]... [" + PREFIX_GROUP_ID + "GROUP_ID] ["
            + PREFIX_YEAR_OF_STUDY + "YEAR_OF_STUDY] ["
            + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "Alice " + PREFIX_CONTACT_TYPE + "student";

    private final PersonMatchesFilterPredicate predicate;
    private final Optional<Integer> groupId;

    /**
     * Constructs a command that lists persons matching the given predicate.
     *
     * @throws NullPointerException If {@code predicate} is null.
     */
    public FilterCommand(PersonMatchesFilterPredicate predicate) {
        this(predicate, Optional.empty());
    }

    /**
     * Constructs a command with an optional numeric group restriction.
     *
     * @param predicate The existing person criteria to satisfy.
     * @param groupId The numeric group ID to match, or empty for all groups.
     * @throws NullPointerException If either argument is null.
     */
    public FilterCommand(PersonMatchesFilterPredicate predicate, Optional<Integer> groupId) {
        this.predicate = requireNonNull(predicate);
        this.groupId = requireNonNull(groupId);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(createCombinedPredicate(model));
        return new CommandResult(
                Messages.getPersonsFoundMessage(model.getFilteredPersonList().size()));
    }

    /** Combines the person criteria with membership of the existing group identified by its numeric ID. */
    private Predicate<Person> createCombinedPredicate(Model model) {
        if (groupId.isEmpty()) {
            return predicate;
        }

        Optional<Group> group = model.getGroupList().stream()
                .filter(candidate -> candidate.getId().matches(groupId.get()))
                .findFirst();
        return predicate.and(person -> group.map(matchingGroup -> matchesGroup(person, matchingGroup)).orElse(false));
    }

    /** Matches the stored group name and contact type without relying on the group's legacy member ID set. */
    private boolean matchesGroup(Person person, Group group) {
        if (!group.getGroupName().groupName.equals(person.getGroupId())) {
            return false;
        }

        return switch (group.getGroupType()) {
            case CLASS -> person instanceof Student;
            case DEPARTMENT -> person instanceof Colleague;
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }

        return predicate.equals(otherFilterCommand.predicate)
                && groupId.equals(otherFilterCommand.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, groupId);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("predicate", predicate);
        if (groupId.isPresent()) {
            builder.add("groupId", groupId);
        }
        return builder.toString();
    }
}
