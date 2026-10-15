package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAddressBook.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonFilterCriteria;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.person.Role;
import seedu.address.model.person.Student;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests for the interaction between {@code FilterCommand} and the model.
 */
public class FilterCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Student student = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
            ALICE.getAddress(), ALICE.getTags());
    private final Guardian guardian = new Guardian(BENSON.getName(), BENSON.getPhone(), BENSON.getEmail(),
            BENSON.getAddress(), BENSON.getTags());
    private final Colleague foodScienceColleague = prepareColleague("Alison Goh", "Food Science");
    private final Colleague computerScienceColleague = prepareColleague("Marcus Lee", "Computer Science");
    private final Colleague unlinkedColleague = prepareColleague("Priya Rao", null);

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
    public void execute_partialMixedCaseName_listsMatchingPersons() {
        assertFilterSuccess(prepareCommand(Optional.of("eIeR"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of()), List.of(BENSON, DANIEL));
        assertFilterSuccess(prepareCommand(Optional.of("Nobody"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of()), List.of());
    }

    @Test
    public void execute_nameAcrossCategories_listsMatchingPersons() {
        Model roleModel = prepareRoleModel();

        assertFilterSuccess(prepareCommand(Optional.of("ALI"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of()), roleModel, List.of(student, foodScienceColleague));
    }

    @Test
    public void execute_contactTypes_listsOnlyMatchingRoles() {
        Model roleModel = prepareRoleModel();

        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.of(Role.STUDENT), Optional.empty(),
                Optional.empty(), Set.of()), roleModel, List.of(student));
        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.of(Role.GUARDIAN), Optional.empty(),
                Optional.empty(), Set.of()), roleModel, List.of(guardian));
        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.of(Role.COLLEAGUE), Optional.empty(),
                Optional.empty(), Set.of()), roleModel,
                List.of(foodScienceColleague, computerScienceColleague, unlinkedColleague));
    }

    @Test
    public void execute_partialMixedCaseDepartment_listsMatchingColleagues() {
        Model roleModel = prepareRoleModel();

        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.empty(), Optional.of("SCIENCE"),
                Optional.empty(), Set.of()), roleModel, List.of(foodScienceColleague, computerScienceColleague));
        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.empty(), Optional.of("fOoD sCi"),
                Optional.empty(), Set.of()), roleModel, List.of(foodScienceColleague));
        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.empty(), Optional.of("Mathematics"),
                Optional.empty(), Set.of()), roleModel, List.of());
    }

    @Test
    public void execute_allCriteria_listsIntersection() {
        Model roleModel = prepareRoleModel();
        FilterCommand command = prepareCommand(Optional.of("ali"), Optional.of(Role.COLLEAGUE),
                Optional.of("food sci"), Optional.of("clementi"), Set.of(new Tag("Committee"), new Tag("Staff")));

        assertFilterSuccess(command, roleModel, List.of(foodScienceColleague));
    }

    @Test
    public void execute_parsedChainedCriteria_listsIntersection() throws Exception {
        Model roleModel = prepareRoleModel();
        FilterCommand command = (FilterCommand) new AddressBookParser().parseCommand(
                "filter t/Staff d/FOOD SCI n/ALI a/CLEMENTI ct/cOl t/Committee");

        assertFilterSuccess(command, roleModel, List.of(foodScienceColleague));
    }

    @Test
    public void execute_contradictoryCriteria_listsZeroPersons() {
        Model roleModel = prepareRoleModel();

        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.of(Role.STUDENT), Optional.of("science"),
                Optional.empty(), Set.of()), roleModel, List.of());
        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.of(Role.GUARDIAN), Optional.of("science"),
                Optional.empty(), Set.of()), roleModel, List.of());
        assertFilterSuccess(prepareCommand(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                Optional.of("Bedok"), Set.of(new Tag("Committee"))), roleModel, List.of());
        assertFilterSuccess(prepareCommand(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                Optional.of("clementi"), Set.of(new Tag("Committee"), new Tag("Missing"))), roleModel, List.of());
    }

    @Test
    public void execute_newCriteriaAfterDepartmentFilter_replacesFilter() {
        Model roleModel = prepareRoleModel();

        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.empty(), Optional.of("food sci"),
                Optional.empty(), Set.of()), roleModel, List.of(foodScienceColleague));
        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.of(Role.STUDENT), Optional.empty(),
                Optional.empty(), Set.of()), roleModel, List.of(student));
        assertFilterSuccess(prepareCommand(Optional.empty(), Set.of()), roleModel,
                List.of(student, guardian, foodScienceColleague, computerScienceColleague, unlinkedColleague));
    }

    @Test
    public void execute_allCriteriaOnEmptyAddressBook_listsZeroPersons() {
        FilterCommand command = prepareCommand(Optional.of("ali"), Optional.of(Role.COLLEAGUE),
                Optional.of("food sci"), Optional.of("clementi"), Set.of(new Tag("Committee")));

        assertFilterSuccess(command, new ModelManager(), List.of());
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
    public void execute_previousNameFilter_replacesFilter() {
        PersonFilterCriteria nameCriteria = new PersonFilterCriteria(Optional.of("Alice"), Optional.empty(),
                Optional.empty(), Optional.empty(), Set.of());
        new FilterCommand(new PersonMatchesFilterPredicate(nameCriteria)).execute(model);
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
        FilterCommand command = prepareCommand(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("science"),
                Optional.of("Clementi"), Set.of(new Tag("Student")));
        FilterCommand copy = prepareCommand(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("science"),
                Optional.of("Clementi"), Set.of(new Tag("Student")));

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
        return prepareCommand(Optional.empty(), Optional.empty(), Optional.empty(), addressKeyword, requiredTags);
    }

    /**
     * Constructs a command from all supported criteria without parsing command input.
     */
    private FilterCommand prepareCommand(Optional<String> nameKeyword, Optional<Role> contactType,
            Optional<String> departmentKeyword, Optional<String> addressKeyword, Set<Tag> requiredTags) {
        PersonFilterCriteria criteria = new PersonFilterCriteria(nameKeyword, contactType, departmentKeyword,
                addressKeyword, requiredTags);
        return new FilterCommand(new PersonMatchesFilterPredicate(criteria));
    }

    /**
     * Verifies the result count, displayed persons, and preservation of the rest of the model.
     */
    private void assertFilterSuccess(FilterCommand command, List<Person> expectedPersons) {
        assertFilterSuccess(command, model, expectedPersons);
    }

    /**
     * Verifies the result count and model state for either the typical or mixed-role address book.
     */
    private void assertFilterSuccess(FilterCommand command, Model actualModel, List<Person> expectedPersons) {
        Model expectedModel = new ModelManager(actualModel.getAddressBook(), actualModel.getUserPrefs());
        expectedModel.updateFilteredPersonList(expectedPersons::contains);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.size());

        assertCommandSuccess(command, actualModel, expectedMessage, expectedModel);
        assertEquals(expectedPersons, actualModel.getFilteredPersonList());
    }

    /**
     * Constructs an in-memory model containing every supported role and an unlinked colleague.
     */
    private Model prepareRoleModel() {
        Model roleModel = new ModelManager();
        for (Person contact : List.of(student, guardian, foodScienceColleague, computerScienceColleague,
                unlinkedColleague)) {
            roleModel.addPerson(contact);
        }
        return roleModel;
    }

    /**
     * Constructs a colleague with shared address and tags, and an optional department.
     */
    private Colleague prepareColleague(String name, String department) {
        Person details = new PersonBuilder().withName(name).withAddress("311, Clementi Ave 2")
                .withTags("Committee", "Staff").build();
        return new Colleague(details.getName(), details.getPhone(), details.getEmail(), details.getAddress(),
                details.getTags(), department);
    }
}
