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
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.ClassGroup;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupId;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonFilterCriteria;
import seedu.address.model.person.PersonId;
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
            ALICE.getAddress(), ALICE.getTags(), null);
    private final Guardian guardian = new Guardian(BENSON.getName(), BENSON.getPhone(), BENSON.getEmail(),
            BENSON.getAddress(), BENSON.getTags());
    private final Colleague foodScienceColleague = prepareColleague("Alison Goh", "Food Science");
    private final Colleague computerScienceColleague = prepareColleague("Marcus Lee", "Computer Science");
    private final Colleague unlinkedColleague = prepareColleague("Priya Rao", null);
    private final Student secondYearStudent = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
            ALICE.getAddress(), ALICE.getTags(), 128, null, 2);
    private final Student twelfthYearStudent = new Student(BENSON.getName(), BENSON.getPhone(), BENSON.getEmail(),
            BENSON.getAddress(), BENSON.getTags(), 256, null, 12);
    private final Guardian numericGuardian = new Guardian(BENSON.getName(), BENSON.getPhone(), BENSON.getEmail(),
            BENSON.getAddress(), BENSON.getTags(), 512);

    /** Verifies exact membership and type separation using IDs unrelated to displayed group positions. */
    @Test
    public void execute_groupId_listsOnlyMatchingMembers() {
        Model groupModel = prepareGroupModel();
        List<Person> contacts = groupModel.getAddressBook().getPersonList();
        Group schoolClass = groupModel.getGroupList().get(0);
        Group department = groupModel.getGroupList().get(1);

        assertFilterSuccess(prepareGroupCommand(schoolClass.getId().getRawId()), groupModel,
                List.of(contacts.get(0), contacts.get(1)));
        assertFilterSuccess(prepareGroupCommand(department.getId().getRawId()), groupModel,
                List.of(contacts.get(2)));
    }

    /**
     * Verifies that parsed group restrictions intersect with existing criteria and Person ID alternatives.
     *
     * @throws Exception If a valid combined command unexpectedly cannot be parsed.
     */
    @Test
    public void execute_groupIdWithCriteria_listsIntersection() throws Exception {
        Model groupModel = prepareGroupModel();
        List<Person> contacts = groupModel.getAddressBook().getPersonList();
        String classFilter = "filter gid/" + groupModel.getGroupList().get(0).getId().getRawId();
        String departmentFilter = "filter gid/" + groupModel.getGroupList().get(1).getId().getRawId();

        assertParsedFilterSuccess(classFilter + " id/128 id/256", groupModel,
                List.of(contacts.get(0), contacts.get(1)));
        assertParsedFilterSuccess(classFilter + " id/128 id/768", groupModel, List.of(contacts.get(0)));
        assertParsedFilterSuccess(classFilter + " yos/2", groupModel, List.of(contacts.get(0)));

        String studentFilter = classFilter
                + " id/128 id/256 yos/2 n/ali a/CLEMENTI AVE ct/stu t/Student t/Year2";
        assertParsedFilterSuccess(studentFilter, groupModel, List.of(contacts.get(0)));
        for (String input : List.of(studentFilter.replace("id/128 id/256", "id/768"),
                studentFilter.replace("yos/2", "yos/3"), studentFilter.replace("n/ali", "n/bob"),
                studentFilter.replace("a/CLEMENTI AVE", "a/Bugis"), studentFilter.replace("ct/stu", "ct/guardian"),
                studentFilter.replace("t/Student", "t/student"))) {
            assertParsedFilterSuccess(input, groupModel, List.of());
        }

        assertParsedFilterSuccess(departmentFilter
                + " id/4096 n/col ct/COL d/shared sch a/clementi t/Staff t/Committee", groupModel,
                List.of(contacts.get(2)));
        assertParsedFilterSuccess(departmentFilter + " yos/2", groupModel, List.of());
    }

    /**
     * Verifies replacement contacts are evaluated live and deleted groups are re-resolved on execution.
     *
     * @throws Exception If a valid filter or link command unexpectedly fails.
     */
    @Test
    public void execute_groupFilterAfterChanges_usesCurrentMembership() throws Exception {
        Model groupModel = prepareGroupModel();
        List<Person> contacts = List.copyOf(groupModel.getAddressBook().getPersonList());
        Group schoolClass = groupModel.getGroupList().get(0);
        Group otherClass = groupModel.getGroupList().get(2);
        FilterCommand command = (FilterCommand) new AddressBookParser().parseCommand(
                "filter gid/" + schoolClass.getId().getRawId());
        assertFilterSuccess(command, groupModel, List.of(contacts.get(0), contacts.get(1)));

        String contactName = contacts.get(4).getName().fullName;
        new LinkGroupCommand(contactName, schoolClass.getGroupName().groupName).execute(groupModel);
        Person linkedContact = groupModel.getAddressBook().getPersonList().get(4);
        assertEquals(List.of(contacts.get(0), contacts.get(1), linkedContact), groupModel.getFilteredPersonList());

        new LinkGroupCommand(contactName, otherClass.getGroupName().groupName).execute(groupModel);
        assertEquals(List.of(contacts.get(0), contacts.get(1)), groupModel.getFilteredPersonList());

        groupModel.deleteGroup(schoolClass);
        assertFilterSuccess(command, groupModel, List.of());
    }

    /**
     * Verifies the deduplicated union using permanent IDs that differ from the displayed positions.
     *
     * @throws Exception If the valid command unexpectedly cannot be parsed.
     */
    @Test
    public void execute_multiplePersonIds_listsUnionWithoutDuplicates() throws Exception {
        FilterCommand command = (FilterCommand) new AddressBookParser().parseCommand("filter id/256 id/128 id/128");

        assertFilterSuccess(command, prepareNumericModel(), List.of(secondYearStudent, twelfthYearStudent));
    }

    /** Verifies that numeric and existing criteria all intersect to select the matching student. */
    @Test
    public void execute_personIdsAndYearOfStudy_listsIntersection() {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.of("ali"), Optional.of(Role.STUDENT),
                Optional.empty(), Optional.of("jurong"), Set.of(new Tag("friends")), Set.of(128, 256), Optional.of(2));
        FilterCommand command = new FilterCommand(new PersonMatchesFilterPredicate(criteria));

        assertFilterSuccess(command, prepareNumericModel(), List.of(secondYearStudent));
    }

    /** Verifies zero results for absent IDs, contradictory criteria, and groups without matching members. */
    @Test
    public void execute_numericCriteriaWithNoMatch_listsZeroPersons() {
        Model numericModel = prepareNumericModel();

        assertFilterSuccess(prepareNumericCommand(Set.of(999), Optional.empty()), numericModel, List.of());
        assertFilterSuccess(prepareNumericCommand(Set.of(512), Optional.of(2)), numericModel, List.of());

        Model groupModel = prepareGroupModel();
        Group emptyGroup = groupModel.getGroupList().get(3);
        assertFilterSuccess(prepareGroupCommand(Integer.MAX_VALUE), groupModel, List.of());
        assertFilterSuccess(prepareGroupCommand(emptyGroup.getId().getRawId()), groupModel, List.of());
    }

    /** Verifies that numeric restrictions preserve all stored data, relationships, and both ID counters. */
    @Test
    public void execute_numericFiltering_preservesStateAndCounters() {
        Model numericModel = prepareNumericModel();
        numericModel.linkGuardianToStudent(numericGuardian, secondYearStudent);
        int nextId = PersonId.getNextId();

        assertFilterSuccess(prepareNumericCommand(Set.of(512), Optional.empty()), numericModel,
                List.of(numericGuardian));
        assertEquals(Map.of(numericGuardian, Set.of(secondYearStudent)),
                numericModel.getAddressBook().getGuardianStudents());
        assertEquals(nextId, PersonId.getNextId());

        Model groupModel = prepareGroupModel();
        List<Person> originalContacts = List.copyOf(groupModel.getAddressBook().getPersonList());
        List<Group> originalGroups = List.copyOf(groupModel.getGroupList());
        groupModel.linkGuardianToStudent((Guardian) originalContacts.get(5), (Student) originalContacts.get(0));
        Map<Guardian, Set<Person>> originalLinks = groupModel.getAddressBook().getGuardianStudents();
        int nextPersonId = PersonId.getNextId();
        int nextGroupId = GroupId.getNextId();

        assertFilterSuccess(prepareGroupCommand(originalGroups.get(0).getId().getRawId()), groupModel,
                List.of(originalContacts.get(0), originalContacts.get(1)));
        assertEquals(originalContacts, groupModel.getAddressBook().getPersonList());
        assertEquals(originalGroups, groupModel.getGroupList());
        assertEquals(originalLinks, groupModel.getAddressBook().getGuardianStudents());
        assertEquals(nextPersonId, PersonId.getNextId());
        assertEquals(nextGroupId, GroupId.getNextId());
    }

    /** Verifies that both constructors reject null predicates and the group Optional cannot be null. */
    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        PersonMatchesFilterPredicate predicate = new PersonMatchesFilterPredicate(
                new PersonFilterCriteria(Optional.empty(), Set.of()));

        assertThrows(NullPointerException.class, () -> new FilterCommand(null));
        assertThrows(NullPointerException.class, () -> new FilterCommand(null, Optional.of(128)));
        assertThrows(NullPointerException.class, () -> new FilterCommand(predicate, null));
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

    /** Verifies that role, numeric, and group filters replace previous constraints and can still be cleared. */
    @Test
    public void execute_newCriteriaAfterDepartmentFilter_replacesFilter() {
        Model roleModel = prepareRoleModel();

        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.empty(), Optional.of("food sci"),
                Optional.empty(), Set.of()), roleModel, List.of(foodScienceColleague));
        assertFilterSuccess(prepareCommand(Optional.empty(), Optional.of(Role.STUDENT), Optional.empty(),
                Optional.empty(), Set.of()), roleModel, List.of(student));
        int colleagueId = computerScienceColleague.getId().getRawId();
        assertFilterSuccess(prepareNumericCommand(Set.of(colleagueId), Optional.empty()),
                roleModel, List.of(computerScienceColleague));
        Group department = new DepartmentGroup(new GroupName("Food Science"), Set.of());
        roleModel.addGroup(department);
        assertFilterSuccess(prepareGroupCommand(department.getId().getRawId()), roleModel,
                List.of(foodScienceColleague));
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

    /** Verifies unrestricted, numeric, and group queries against the same empty address book. */
    @Test
    public void execute_emptyAddressBook_listsZeroPersons() {
        Model emptyModel = new ModelManager();

        for (FilterCommand command : List.of(prepareCommand(Optional.empty(), Set.of()),
                prepareNumericCommand(Set.of(128, 256), Optional.of(2)), prepareGroupCommand(128))) {
            assertCommandSuccess(command, emptyModel,
                    String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), new ModelManager());
        }
    }

    /** Verifies equality includes group restrictions and preserves compatibility between constructors. */
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

        PersonMatchesFilterPredicate predicate = new PersonMatchesFilterPredicate(
                new PersonFilterCriteria(Optional.of("Clementi"), Set.of(new Tag("Student"))));
        FilterCommand groupedCommand = new FilterCommand(predicate, Optional.of(128));
        assertTrue(command.equals(new FilterCommand(predicate, Optional.empty())));
        assertTrue(groupedCommand.equals(new FilterCommand(predicate, Optional.of(128))));
        assertFalse(groupedCommand.equals(new FilterCommand(predicate, Optional.of(256))));
        assertFalse(groupedCommand.equals(command));
    }

    /** Verifies equal commands have equal hashes with or without a supplied group ID. */
    @Test
    public void hashCode_equalCommands_returnsSameHashCode() {
        FilterCommand command = prepareCommand(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("science"),
                Optional.of("Clementi"), Set.of(new Tag("Student")));
        FilterCommand copy = prepareCommand(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("science"),
                Optional.of("Clementi"), Set.of(new Tag("Student")));

        assertEquals(command.hashCode(), copy.hashCode());
        FilterCommand groupedCommand = prepareGroupCommand(128);
        FilterCommand groupedCopy = prepareGroupCommand(128);
        assertEquals(groupedCommand.hashCode(), groupedCopy.hashCode());
    }

    /** Verifies the original representation is preserved and a supplied group ID is included. */
    @Test
    public void toStringMethod() {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.of("Clementi"), Set.of(new Tag("Student")));
        PersonMatchesFilterPredicate predicate = new PersonMatchesFilterPredicate(criteria);
        FilterCommand command = new FilterCommand(predicate);
        String expected = FilterCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";

        assertEquals(expected, command.toString());
        FilterCommand groupedCommand = new FilterCommand(predicate, Optional.of(128));
        String expectedGrouped = FilterCommand.class.getCanonicalName() + "{predicate=" + predicate
                + ", groupId=Optional[128]}";
        assertEquals(expectedGrouped, groupedCommand.toString());
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

    /** Constructs a command for the exact numeric criteria without involving parser validation. */
    private FilterCommand prepareNumericCommand(Set<Integer> personIds, Optional<Integer> yearOfStudy) {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of(), personIds, yearOfStudy);
        return new FilterCommand(new PersonMatchesFilterPredicate(criteria));
    }

    /** Constructs an unrestricted person predicate with an exact numeric group restriction. */
    private FilterCommand prepareGroupCommand(int groupId) {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.empty(), Set.of());
        return new FilterCommand(new PersonMatchesFilterPredicate(criteria), Optional.of(groupId));
    }

    /** Parses a complete command through the main parser and reuses the existing result assertions. */
    private void assertParsedFilterSuccess(String input, Model actualModel, List<Person> expectedPersons)
            throws Exception {
        FilterCommand command = (FilterCommand) new AddressBookParser().parseCommand(input);
        assertFilterSuccess(command, actualModel, expectedPersons);
    }

    /** Creates isolated groups above the Integer cache range, restoring the allocation counter afterwards. */
    private Model prepareGroupModel() {
        Model groupModel = new ModelManager();
        int nextGroupId = GroupId.getNextId();
        try {
            GroupId.setNextId(128);
            for (Group group : List.of(new ClassGroup(new GroupName("Shared School"), Set.of()),
                    new DepartmentGroup(new GroupName("Shared School"), Set.of()),
                    new ClassGroup(new GroupName("Other Class"), Set.of()),
                    new ClassGroup(new GroupName("Empty Class"), Set.of()),
                    new ClassGroup(new GroupName("shared school"), Set.of()),
                    new ClassGroup(new GroupName("Shared School Extra"), Set.of()))) {
                groupModel.addGroup(group);
            }
        } finally {
            GroupId.setNextId(nextGroupId);
        }

        Person colleagueDetails = new PersonBuilder().withName("Colin Lee").withAddress("311, Clementi Ave 2")
                .withTags("Staff", "Committee").build();
        Colleague colleague = new Colleague(colleagueDetails.getName(), colleagueDetails.getPhone(),
                colleagueDetails.getEmail(), colleagueDetails.getAddress(), colleagueDetails.getTags(), 4096,
                "Shared School");
        for (Person contact : List.of(prepareGroupedStudent("Alice Goh", 128, "Shared School", 2),
                prepareGroupedStudent("Bob Tan", 256, "Shared School", 3), colleague,
                prepareGroupedStudent("Cara Lee", 768, "Other Class", 2),
                prepareGroupedStudent("Dana Lee", 1024, null, 2), numericGuardian,
                prepareGroupedStudent("Finn Tan", 1536, "shared school", 2),
                prepareGroupedStudent("Grace Tan", 1792, "Shared School Extra", 2))) {
            groupModel.addPerson(contact);
        }
        return groupModel;
    }

    /** Constructs a student with an explicit ID, group assignment, and year-specific tags. */
    private Student prepareGroupedStudent(String name, int personId, String groupName, int yearOfStudy) {
        Person details = new PersonBuilder().withName(name).withAddress("311, Clementi Ave 2")
                .withTags("Student", "Year" + yearOfStudy).build();
        return new Student(details.getName(), details.getPhone(), details.getEmail(), details.getAddress(),
                details.getTags(), personId, groupName, yearOfStudy);
    }

    /** Creates in-memory contacts with explicit IDs unrelated to their displayed positions. */
    private Model prepareNumericModel() {
        Model numericModel = new ModelManager();
        for (Person contact : List.of(secondYearStudent, twelfthYearStudent, numericGuardian)) {
            numericModel.addPerson(contact);
        }
        return numericModel;
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
