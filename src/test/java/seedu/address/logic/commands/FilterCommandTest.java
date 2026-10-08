package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonFilterCriteria;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.tag.Tag;

/**
 * Contains integration tests for the interaction between {@code FilterCommand} and the model.
 */
public class FilterCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FilterCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        FilterCommand command = prepareCommand(Optional.empty(), Set.of());

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_emptyCriteria_listsAllPersons() {
        assertFilterSuccess(prepareCommand(Optional.empty(), Set.of()), getTypicalPersons());
    }

    @Test
    public void execute_emptyCriteriaAfterFiltering_restoresAllPersons() {
        model.updateFilteredPersonList(person -> false);
        assertTrue(model.getFilteredPersonList().isEmpty());

        assertFilterSuccess(prepareCommand(Optional.empty(), Set.of()), getTypicalPersons());
    }

    @Test
    public void execute_partialMixedCaseAddress_listsMatchingPersons() {
        assertFilterSuccess(prepareCommand(Optional.of("cLeMeNtI aVe"), Set.of()), List.of(BENSON));
    }

    @Test
    public void execute_exactTag_listsMatchingPersons() {
        assertFilterSuccess(prepareCommand(Optional.empty(), Set.of(new Tag("friends"))),
                List.of(ALICE, BENSON, DANIEL));
    }

    @Test
    public void execute_multipleTags_requiresAllTags() {
        assertFilterSuccess(prepareCommand(Optional.empty(), Set.of(new Tag("friends"), new Tag("owesMoney"))),
                List.of(BENSON));
    }

    @Test
    public void execute_addressAndTags_requiresAllCriteria() {
        assertFilterSuccess(prepareCommand(Optional.of("Clementi"),
                Set.of(new Tag("friends"), new Tag("owesMoney"))), List.of(BENSON));
        assertFilterSuccess(prepareCommand(Optional.of("Jurong"), Set.of(new Tag("owesMoney"))), List.of());
    }

    @Test
    public void execute_nonMatchingCriteria_listsZeroPersons() {
        assertFilterSuccess(prepareCommand(Optional.of("Bugis"), Set.of()), List.of());
        assertFilterSuccess(prepareCommand(Optional.empty(), Set.of(new Tag("Student"))), List.of());
    }

    @Test
    public void execute_differentlyCasedOrPartialTag_listsZeroPersons() {
        assertFilterSuccess(prepareCommand(Optional.empty(), Set.of(new Tag("Friends"))), List.of());
        assertFilterSuccess(prepareCommand(Optional.empty(), Set.of(new Tag("friend"))), List.of());
    }

    @Test
    public void execute_previousFind_replacesFilter() {
        new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice"))).execute(model);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());

        assertFilterSuccess(prepareCommand(Optional.empty(), Set.of(new Tag("friends"))),
                List.of(ALICE, BENSON, DANIEL));
    }

    @Test
    public void execute_emptyAddressBook_listsZeroPersons() {
        Model emptyModel = new ModelManager();
        FilterCommand command = prepareCommand(Optional.empty(), Set.of());

        assertCommandSuccess(command, emptyModel,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), new ModelManager());
    }

    @Test
    public void equals() {
        FilterCommand command = prepareCommand(Optional.of("Clementi"), Set.of(new Tag("Student")));
        FilterCommand copy = prepareCommand(Optional.of("Clementi"), Set.of(new Tag("Student")));

        assertTrue(command.equals(command));
        assertTrue(command.equals(copy));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(prepareCommand(Optional.of("Bugis"), Set.of(new Tag("Student")))));
        assertFalse(command.equals(prepareCommand(Optional.of("Clementi"), Set.of(new Tag("student")))));
    }

    @Test
    public void hashCode_equalCommands_returnsSameHashCode() {
        FilterCommand command = prepareCommand(Optional.of("Clementi"), Set.of(new Tag("Student")));
        FilterCommand copy = prepareCommand(Optional.of("Clementi"), Set.of(new Tag("Student")));

        assertEquals(command.hashCode(), copy.hashCode());
    }

    @Test
    public void toStringMethod() {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.of("Clementi"), Set.of(new Tag("Student")));
        PersonMatchesFilterPredicate predicate = new PersonMatchesFilterPredicate(criteria);
        FilterCommand command = new FilterCommand(predicate);
        String expected = FilterCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";

        assertEquals(expected, command.toString());
    }

    /**
     * Constructs a command from the supplied address and tag criteria.
     */
    private FilterCommand prepareCommand(Optional<String> addressKeyword, Set<Tag> requiredTags) {
        PersonFilterCriteria criteria = new PersonFilterCriteria(addressKeyword, requiredTags);
        return new FilterCommand(new PersonMatchesFilterPredicate(criteria));
    }

    /**
     * Verifies the result count, displayed persons, and preservation of the rest of the model.
     */
    private void assertFilterSuccess(FilterCommand command, List<Person> expectedPersons) {
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(expectedPersons::contains);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.size());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(expectedPersons, model.getFilteredPersonList());
    }
}
