package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.group.DepartmentGroup;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests text substring matching, role and tag matching, and the intersection of all filter criteria.
 */
public class PersonMatchesFilterPredicateTest {

    private static final ObservableList<Group> groupList = FXCollections.observableArrayList();
    private static final DepartmentGroup foodScienceDepartment =
            new DepartmentGroup(new GroupName("Food Science"));

    private final Person person = new PersonBuilder().withName("Alice Tan").withAddress("311, Clementi Ave 2")
            .withTags("Student", "Year2").build();
    private final Student student = new Student(person.getName(), person.getPhone(), person.getEmail(),
            person.getAddress(), person.getTags(), null);
    private final Guardian guardian = new Guardian(person.getName(), person.getPhone(), person.getEmail(),
            person.getAddress(), person.getTags());
    private final Colleague colleague = prepareColleague();

    @BeforeAll
    public static void populateGroupList() {
        groupList.add(foodScienceDepartment);
    }

    @Test
    public void constructor_nullCriteria_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonMatchesFilterPredicate(null));
    }

    @Test
    public void test_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> preparePredicate(Optional.empty(), Set.of())
                        .test(null, groupList));
    }

    @Test
    public void test_emptyCriteria_returnsTrue() {
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.empty(), Set.of());

        assertTrue(predicate.test(person, groupList));
        assertTrue(predicate.test(new PersonBuilder().withTags().build(), groupList));
        assertTrue(predicate.test(student, groupList));
        assertTrue(predicate.test(guardian, groupList));
        assertTrue(predicate.test(colleague, groupList));
        assertTrue(predicate.test(prepareColleague(), groupList));
    }

    @Test
    public void test_fullName_returnsTrue() {
        assertTrue(preparePredicate(Optional.of("Alice Tan"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of()).test(person, groupList));
    }

    @Test
    public void test_partialNameAtBeginningMiddleAndEnd_returnsTrue() {
        for (String keyword : List.of("Ali", "lice", "Tan", "ce Ta")) {
            assertTrue(preparePredicate(Optional.of(keyword), Optional.empty(), Optional.empty(),
                    Optional.empty(), Set.of()).test(person, groupList));
        }
    }

    @Test
    public void test_nameDiffersInCase_returnsTrue() {
        assertTrue(preparePredicate(Optional.of("aLiCe tAn"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of()).test(person, groupList));
    }

    @Test
    public void test_nonContiguousOrNonMatchingName_returnsFalse() {
        for (String keyword : List.of("tan alice", "ali tan", "alice  tan", "Bob")) {
            assertFalse(preparePredicate(Optional.of(keyword), Optional.empty(), Optional.empty(),
                    Optional.empty(), Set.of()).test(person, groupList));
        }
    }

    @Test
    public void test_nameKeywordOnlyInOtherFields_returnsFalse() {
        Person otherPerson = new PersonBuilder().withName("Bob Lim").withEmail("alice@example.com")
                .withAddress("Alice Road").withTags("Alice").build();

        assertFalse(preparePredicate(Optional.of("alice"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of()).test(otherPerson, groupList));
    }

    @Test
    public void test_contactType_matchesOnlyActualRole() {
        for (Role role : Role.values()) {
            PersonMatchesFilterPredicate predicate = preparePredicate(Optional.empty(), Optional.of(role),
                    Optional.empty(), Optional.empty(), Set.of());

            for (Person contact : List.of(student, guardian, colleague)) {
                assertEquals(role == contact.getRole(), predicate.test(contact, groupList));
            }
        }
    }

    @Test
    public void test_contactTypeUsesRole_notTagsOrSubclass() {
        PersonMatchesFilterPredicate studentPredicate = preparePredicate(Optional.empty(), Optional.of(Role.STUDENT),
                Optional.empty(), Optional.empty(), Set.of());
        PersonMatchesFilterPredicate colleaguePredicate = preparePredicate(Optional.empty(),
                Optional.of(Role.COLLEAGUE), Optional.empty(), Optional.empty(), Set.of());

        assertTrue(preparePredicate(Optional.empty(), Set.of(new Tag("Student"))).test(guardian, groupList));
        assertFalse(studentPredicate.test(guardian, groupList));
        assertTrue(colleaguePredicate.test(new PersonBuilder().withRole(Role.COLLEAGUE).build(), groupList));
    }

    @Test
    public void test_fullAndPartialDepartment_returnsTrue() {
        for (String keyword : List.of("Food Science", "Food", "food sci", "ood Sci", "Science", "fOoD sCiEnCe")) {
            assertTrue(preparePredicate(Optional.empty(), Optional.empty(), Optional.of(keyword),
                    Optional.empty(), Set.of()).test(colleague, groupList));
        }
    }

    @Test
    public void test_nonContiguousOrNonMatchingDepartment_returnsFalse() {
        for (String keyword : List.of("Computer Science", "Science Food", "Food  Science")) {
            assertFalse(preparePredicate(Optional.empty(), Optional.empty(), Optional.of(keyword),
                    Optional.empty(), Set.of()).test(colleague, groupList));
        }
    }

    @Test
    public void test_departmentOnContactsWithoutDepartment_returnsFalse() {
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.empty(), Optional.empty(),
                Optional.of("science"), Optional.empty(), Set.of());
        Colleague noDepartmentColleague = new Colleague(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                person.getTags(), null);

        assertFalse(predicate.test(student, groupList));
        assertFalse(predicate.test(guardian, groupList));
        assertFalse(predicate.test(noDepartmentColleague, groupList));
        assertFalse(predicate.test(new PersonBuilder().withRole(Role.COLLEAGUE).build(), groupList));
    }

    @Test
    public void test_departmentKeywordOnlyInOtherFields_returnsFalse() {
        Person details = new PersonBuilder().withName("Science Teacher").withAddress("Science Road")
                .withEmail("science@example.com").withTags("Science").build();
        Colleague otherColleague = new Colleague(details.getName(), details.getPhone(), details.getEmail(),
                details.getAddress(), details.getTags(), null);

        assertFalse(preparePredicate(Optional.empty(), Optional.empty(), Optional.of("science"),
                Optional.empty(), Set.of()).test(otherColleague, groupList));
    }

    @Test
    @ResourceLock(Resources.LOCALE)
    public void test_turkishDefaultLocale_matchesNameAndDepartment() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Person details = new PersonBuilder().withName("IRIS Tan").build();
            Colleague iris = new Colleague(details.getName(), details.getPhone(), details.getEmail(),
                    details.getAddress(), details.getTags(), foodScienceDepartment.getId());

            assertTrue(preparePredicate(Optional.of("iris"), Optional.of(Role.COLLEAGUE), Optional.of("science"),
                    Optional.empty(), Set.of()).test(iris, groupList));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void test_allCriteriaMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE),
                Optional.of("food sci"), Optional.of("clementi ave"), Set.of(new Tag("Student"), new Tag("Year2")));

        assertTrue(predicate.test(colleague, groupList));
    }

    @Test
    public void test_anySingleCriterionFails_returnsFalse() {
        Set<Tag> tags = Set.of(new Tag("Student"), new Tag("Year2"));

        assertFalse(preparePredicate(Optional.of("Bob"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                Optional.of("clementi ave"), tags).test(colleague, groupList));
        assertFalse(preparePredicate(Optional.of("ali"), Optional.of(Role.STUDENT), Optional.of("food sci"),
                Optional.of("clementi ave"), tags).test(colleague, groupList));
        assertFalse(preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("mathematics"),
                Optional.of("clementi ave"), tags).test(colleague, groupList));
        assertFalse(preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                Optional.of("Bedok"), tags).test(colleague, groupList));
        assertFalse(preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                Optional.of("clementi ave"), Set.of(new Tag("Student"), new Tag("Year3"))).test(colleague, groupList));
    }

    @Test
    public void test_fullAddress_returnsTrue() {
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.of("311, Clementi Ave 2"), Set.of());

        assertTrue(predicate.test(person, groupList));
    }

    @Test
    public void test_partialAddress_returnsTrue() {
        assertTrue(preparePredicate(Optional.of("Clementi Ave"), Set.of()).test(person, groupList));
        assertTrue(preparePredicate(Optional.of("Ave 2"), Set.of()).test(person, groupList));
        assertTrue(preparePredicate(Optional.of("menti"), Set.of()).test(person, groupList));
    }

    @Test
    public void test_addressDiffersInCase_returnsTrue() {
        assertTrue(preparePredicate(Optional.of("cLeMeNtI aVE"), Set.of()).test(person, groupList));
    }

    @Test
    @ResourceLock(Resources.LOCALE)
    public void test_turkishDefaultLocale_matchesAddress() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Person personInIstanbul = new PersonBuilder().withAddress("Istanbul Road").build();
            PersonMatchesFilterPredicate predicate = preparePredicate(Optional.of("istanbul"), Set.of());

            assertTrue(predicate.test(personInIstanbul, groupList));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void test_nonContiguousAddress_returnsFalse() {
        assertFalse(preparePredicate(Optional.of("Clementi 2"), Set.of()).test(person, groupList));
        assertFalse(preparePredicate(Optional.of("Ave Clementi"), Set.of()).test(person, groupList));
    }

    @Test
    public void test_nonMatchingAddress_returnsFalse() {
        assertFalse(preparePredicate(Optional.of("Bugis"), Set.of()).test(person, groupList));
    }

    @Test
    public void test_keywordInOtherFields_returnsFalse() {
        Person otherPerson = new PersonBuilder().withName("Clementi").withEmail("clementi@example.com")
                .withAddress("Bugis Street").withTags("Clementi").build();

        assertFalse(preparePredicate(Optional.of("Clementi"), Set.of()).test(otherPerson, groupList));
    }

    @Test
    public void test_exactTagWithExtraPersonTags_returnsTrue() {
        assertTrue(preparePredicate(Optional.empty(),
                Set.of(new Tag("Student"))).test(person, groupList));
    }

    @Test
    public void test_tagDiffersInCase_returnsFalse() {
        assertFalse(preparePredicate(Optional.empty(),
                Set.of(new Tag("student"))).test(person, groupList));
        assertFalse(preparePredicate(Optional.empty(),
                Set.of(new Tag("STUDENT"))).test(person, groupList));
    }

    @Test
    public void test_partialOrExtendedTag_returnsFalse() {
        assertFalse(preparePredicate(Optional.empty(),
                Set.of(new Tag("Stud"))).test(person, groupList));
        assertFalse(preparePredicate(Optional.empty(),
                Set.of(new Tag("Student2026"))).test(person, groupList));
    }

    @Test
    public void test_allRequiredTagsPresent_returnsTrue() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.empty(), Set.of(new Tag("Student"), new Tag("Year2")));

        assertTrue(predicate.test(person, groupList));
    }

    @Test
    public void test_requiredTagMissing_returnsFalse() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.empty(), Set.of(new Tag("Student"), new Tag("Year3")));

        assertFalse(predicate.test(person, groupList));
        assertFalse(predicate.test(new PersonBuilder().withTags().build(), groupList));
    }

    @Test
    public void test_differentlyCasedTags_requiresBothExactTags() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.empty(), Set.of(new Tag("Student"), new Tag("student")));

        assertFalse(predicate.test(person, groupList));
        assertTrue(predicate.test(new PersonBuilder().withTags("Student", "student").build(), groupList));
    }

    @Test
    public void test_addressAndTagsMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.of("clementi ave"), Set.of(new Tag("Student"), new Tag("Year2")));

        assertTrue(predicate.test(person, groupList));
    }

    @Test
    public void test_onlyOneCriterionMatches_returnsFalse() {
        assertFalse(preparePredicate(Optional.of("Clementi"), Set.of(new Tag("student"))).test(person, groupList));
        assertFalse(preparePredicate(Optional.of("Bugis"), Set.of(new Tag("Student"))).test(person, groupList));
    }

    @Test
    public void equals() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.of("Clementi"), Set.of(new Tag("Student")));
        PersonMatchesFilterPredicate copy =
                preparePredicate(Optional.of("Clementi"), Set.of(new Tag("Student")));

        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(copy));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(1));
        assertFalse(predicate.equals(preparePredicate(Optional.of("Bugis"), Set.of(new Tag("Student")))));
        assertFalse(predicate.equals(preparePredicate(Optional.of("Clementi"), Set.of(new Tag("student")))));
    }

    @Test
    public void hashCode_equalPredicates_returnsSameHashCode() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                        Optional.of("Clementi"), Set.of(new Tag("Student")));
        PersonMatchesFilterPredicate copy =
                preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                        Optional.of("Clementi"), Set.of(new Tag("Student")));

        assertEquals(predicate.hashCode(), copy.hashCode());
    }

    @Test
    public void toStringMethod() {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.of("Clementi"), Set.of(new Tag("Student")));
        PersonMatchesFilterPredicate predicate = new PersonMatchesFilterPredicate(criteria);
        String expected = PersonMatchesFilterPredicate.class.getCanonicalName() + "{criteria=" + criteria + "}";

        assertEquals(expected, predicate.toString());
    }

    /**
     * Constructs a predicate from the supplied address and tag criteria.
     */
    private PersonMatchesFilterPredicate preparePredicate(Optional<String> addressKeyword, Set<Tag> requiredTags) {
        return preparePredicate(Optional.empty(), Optional.empty(), Optional.empty(), addressKeyword, requiredTags);
    }

    /**
     * Constructs a predicate from all supported criteria without parsing command input.
     */
    private PersonMatchesFilterPredicate preparePredicate(Optional<String> nameKeyword, Optional<Role> contactType,
            Optional<String> departmentKeyword, Optional<String> addressKeyword, Set<Tag> requiredTags) {
        return new PersonMatchesFilterPredicate(new PersonFilterCriteria(nameKeyword, contactType, departmentKeyword,
                addressKeyword, requiredTags));
    }

    /**
     * Constructs a colleague with the shared contact details and an optional department.
     */
    private Colleague prepareColleague() {
        return new Colleague(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                person.getTags(), foodScienceDepartment.getId());
    }
}
