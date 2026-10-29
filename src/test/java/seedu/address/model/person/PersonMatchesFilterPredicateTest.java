package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests text, role, tag, and numeric matching, including OR within IDs and AND across criteria.
 */
public class PersonMatchesFilterPredicateTest {

    private final Person person = new PersonBuilder().withName("Alice Tan").withAddress("311, Clementi Ave 2")
            .withTags("Student", "Year2").build();
    private final Student student = new Student(person.getName(), person.getPhone(), person.getEmail(),
            person.getAddress(), person.getTags(), null);
    private final Guardian guardian = new Guardian(person.getName(), person.getPhone(), person.getEmail(),
            person.getAddress(), person.getTags());
    private final Colleague colleague = prepareColleague("Food Science");

    /** Verifies exact ID matching rather than textual-prefix matching. */
    @Test
    public void test_singlePersonId_matchesCompleteId() {
        PersonMatchesFilterPredicate predicate = prepareNumericPredicate(Set.of(1), Optional.empty());

        assertTrue(predicate.test(person));
        for (int id : List.of(10, 12)) {
            assertFalse(predicate.test(new PersonBuilder(person).withPersonId(id).build()));
        }
    }

    /** Verifies that any supplied ID can match, while a contact outside the set cannot. */
    @Test
    public void test_multiplePersonIds_matchesAnyAlternative() {
        PersonMatchesFilterPredicate predicate = prepareNumericPredicate(Set.of(1, 10), Optional.empty());

        assertTrue(predicate.test(person));
        assertTrue(predicate.test(new PersonBuilder(person).withPersonId(10).build()));
        assertFalse(predicate.test(new PersonBuilder(person).withPersonId(12).build()));
    }

    /** Verifies that permanent IDs identify contacts independently of their runtime category. */
    @Test
    public void test_personIds_matchAllContactTypes() {
        Person numericStudent = new PersonBuilder(person).withPersonId(128).build();
        Guardian numericGuardian = new Guardian(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getTags(), 129);
        Colleague numericColleague = new Colleague(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getTags(), 130, "Food Science");
        PersonMatchesFilterPredicate predicate = prepareNumericPredicate(Set.of(128, 129, 130), Optional.empty());

        for (Person contact : List.of(numericStudent, numericGuardian, numericColleague)) {
            assertTrue(predicate.test(contact));
        }
    }

    /** Verifies exact year matching rather than matching a digit within another year. */
    @Test
    public void test_yearOfStudy_matchesExactNumericValue() {
        PersonMatchesFilterPredicate predicate = prepareNumericPredicate(Set.of(), Optional.of(2));

        assertTrue(predicate.test(new PersonBuilder(person).withYearOfStudy(2).build()));
        assertFalse(predicate.test(new PersonBuilder(person).withYearOfStudy(12).build()));
    }

    /** Verifies safe rejection of absent student years and contacts without a student year field. */
    @Test
    public void test_missingYearOrNonStudent_returnsFalse() {
        PersonMatchesFilterPredicate predicate = prepareNumericPredicate(Set.of(), Optional.of(2));

        for (Person contact : List.of(student, guardian, colleague)) {
            assertFalse(predicate.test(contact));
        }
    }

    /** Uses values outside the usual Integer cache to detect identity-based comparisons. */
    @Test
    public void test_largeNumericCriteria_compareByValue() {
        Person largeYearStudent = new PersonBuilder(person).withPersonId(128).withYearOfStudy(1000).build();
        PersonMatchesFilterPredicate predicate = prepareNumericPredicate(Set.of(128), Optional.of(1000));

        assertTrue(predicate.test(largeYearStudent));
        assertFalse(predicate.test(new PersonBuilder(largeYearStudent).withYearOfStudy(1001).build()));
    }

    /** Verifies that satisfying the ID alternatives cannot bypass the year criterion, or vice versa. */
    @Test
    public void test_personIdsAndYearOfStudy_requireBothCriteria() {
        PersonMatchesFilterPredicate predicate = prepareNumericPredicate(Set.of(1, 10), Optional.of(2));

        assertTrue(predicate.test(new PersonBuilder(person).withYearOfStudy(2).build()));
        assertFalse(predicate.test(new PersonBuilder(person).withPersonId(10).withYearOfStudy(12).build()));
        assertFalse(predicate.test(new PersonBuilder(person).withPersonId(12).withYearOfStudy(2).build()));
    }

    /** Verifies that a matching ID cannot bypass a non-matching name, role, or address. */
    @Test
    public void test_personIdsAndExistingFields_requireEveryCriterion() {
        Set<Integer> ids = Set.of(1, 10);

        assertFalse(preparePredicate(Optional.of("Bob"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of(), ids, Optional.empty()).test(person));
        assertFalse(preparePredicate(Optional.empty(), Optional.of(Role.GUARDIAN), Optional.empty(),
                Optional.empty(), Set.of(), ids, Optional.empty()).test(person));
        assertFalse(preparePredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("Bugis"), Set.of(), ids, Optional.empty()).test(person));
    }

    /** Verifies that numeric matching still requires every exact, case-sensitive tag. */
    @Test
    public void test_numericCriteriaWithTags_requireAllExactTags() {
        Person secondYearStudent = new PersonBuilder(person).withYearOfStudy(2).build();
        Set<Integer> ids = Set.of(1, 10);

        assertTrue(preparePredicate(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Set.of(new Tag("Student"), new Tag("Year2")), ids, Optional.of(2)).test(secondYearStudent));
        assertFalse(preparePredicate(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Set.of(new Tag("Student"), new Tag("Missing")), ids, Optional.of(2)).test(secondYearStudent));
        assertFalse(preparePredicate(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Set.of(new Tag("student"), new Tag("Year2")), ids, Optional.of(2)).test(secondYearStudent));
    }

    /** Verifies that an ID alternative cannot make a student satisfy a colleague department criterion. */
    @Test
    public void test_personIdsAndDepartment_matchOnlyLinkedColleagues() {
        Colleague linkedColleague = new Colleague(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getTags(), 128, "Food Science");
        Person numericStudent = new PersonBuilder(person).withPersonId(129).build();
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.empty(), Optional.empty(),
                Optional.of("food sci"), Optional.empty(), Set.of(), Set.of(128, 129), Optional.empty());

        assertTrue(predicate.test(linkedColleague));
        assertFalse(predicate.test(numericStudent));
    }

    /** Verifies that the valid but incompatible department and year criteria cannot match either subtype. */
    @Test
    public void test_yearOfStudyAndDepartment_returnsFalse() {
        Person secondYearStudent = new PersonBuilder(person).withPersonId(128).withYearOfStudy(2).build();
        Colleague linkedColleague = new Colleague(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getTags(), 129, "Food Science");
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.empty(), Optional.empty(),
                Optional.of("science"), Optional.empty(), Set.of(), Set.of(128, 129), Optional.of(2));

        assertFalse(predicate.test(secondYearStudent));
        assertFalse(predicate.test(linkedColleague));
    }

    @Test
    public void constructor_nullCriteria_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonMatchesFilterPredicate(null));
    }

    @Test
    public void test_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> preparePredicate(Optional.empty(), Set.of()).test(null));
    }

    @Test
    public void test_emptyCriteria_returnsTrue() {
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.empty(), Set.of());

        assertTrue(predicate.test(person));
        assertTrue(predicate.test(new PersonBuilder().withTags().build()));
        assertTrue(predicate.test(student));
        assertTrue(predicate.test(guardian));
        assertTrue(predicate.test(colleague));
        assertTrue(predicate.test(prepareColleague(null)));
    }

    @Test
    public void test_fullName_returnsTrue() {
        assertTrue(preparePredicate(Optional.of("Alice Tan"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of()).test(person));
    }

    @Test
    public void test_partialNameAtBeginningMiddleAndEnd_returnsTrue() {
        for (String keyword : List.of("Ali", "lice", "Tan", "ce Ta")) {
            assertTrue(preparePredicate(Optional.of(keyword), Optional.empty(), Optional.empty(),
                    Optional.empty(), Set.of()).test(person));
        }
    }

    @Test
    public void test_nameDiffersInCase_returnsTrue() {
        assertTrue(preparePredicate(Optional.of("aLiCe tAn"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of()).test(person));
    }

    @Test
    public void test_nonContiguousOrNonMatchingName_returnsFalse() {
        for (String keyword : List.of("tan alice", "ali tan", "alice  tan", "Bob")) {
            assertFalse(preparePredicate(Optional.of(keyword), Optional.empty(), Optional.empty(),
                    Optional.empty(), Set.of()).test(person));
        }
    }

    @Test
    public void test_nameKeywordOnlyInOtherFields_returnsFalse() {
        Person otherPerson = new PersonBuilder().withName("Bob Lim").withEmail("alice@example.com")
                .withAddress("Alice Road").withTags("Alice").build();

        assertFalse(preparePredicate(Optional.of("alice"), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of()).test(otherPerson));
    }

    @Test
    public void test_contactType_matchesOnlyActualRole() {
        for (Role role : Role.values()) {
            PersonMatchesFilterPredicate predicate = preparePredicate(Optional.empty(), Optional.of(role),
                    Optional.empty(), Optional.empty(), Set.of());

            for (Person contact : List.of(student, guardian, colleague)) {
                assertEquals(role == contact.getRole(), predicate.test(contact));
            }
        }
    }

    @Test
    public void test_contactTypeUsesRole_notTagsOrSubclass() {
        PersonMatchesFilterPredicate studentPredicate = preparePredicate(Optional.empty(), Optional.of(Role.STUDENT),
                Optional.empty(), Optional.empty(), Set.of());
        PersonMatchesFilterPredicate colleaguePredicate = preparePredicate(Optional.empty(),
                Optional.of(Role.COLLEAGUE), Optional.empty(), Optional.empty(), Set.of());

        assertTrue(preparePredicate(Optional.empty(), Set.of(new Tag("Student"))).test(guardian));
        assertFalse(studentPredicate.test(guardian));
        assertTrue(colleaguePredicate.test(new PersonBuilder().withRole(Role.COLLEAGUE).build()));
    }

    @Test
    public void test_fullAndPartialDepartment_returnsTrue() {
        for (String keyword : List.of("Food Science", "Food", "food sci", "ood Sci", "Science", "fOoD sCiEnCe")) {
            assertTrue(preparePredicate(Optional.empty(), Optional.empty(), Optional.of(keyword),
                    Optional.empty(), Set.of()).test(colleague));
        }
    }

    @Test
    public void test_nonContiguousOrNonMatchingDepartment_returnsFalse() {
        for (String keyword : List.of("Computer Science", "Science Food", "Food  Science")) {
            assertFalse(preparePredicate(Optional.empty(), Optional.empty(), Optional.of(keyword),
                    Optional.empty(), Set.of()).test(colleague));
        }
    }

    @Test
    public void test_departmentOnContactsWithoutDepartment_returnsFalse() {
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.empty(), Optional.empty(),
                Optional.of("science"), Optional.empty(), Set.of());

        assertFalse(predicate.test(student));
        assertFalse(predicate.test(guardian));
        assertFalse(predicate.test(prepareColleague(null)));
        assertFalse(predicate.test(prepareColleague("")));
        assertFalse(predicate.test(new PersonBuilder().withRole(Role.COLLEAGUE).build()));
    }

    @Test
    public void test_departmentKeywordOnlyInOtherFields_returnsFalse() {
        Person details = new PersonBuilder().withName("Science Teacher").withAddress("Science Road")
                .withEmail("science@example.com").withTags("Science").build();
        Colleague otherColleague = new Colleague(details.getName(), details.getPhone(), details.getEmail(),
                details.getAddress(), details.getTags(), "Mathematics");

        assertFalse(preparePredicate(Optional.empty(), Optional.empty(), Optional.of("science"),
                Optional.empty(), Set.of()).test(otherColleague));
    }

    @Test
    @ResourceLock(Resources.LOCALE)
    public void test_turkishDefaultLocale_matchesNameAndDepartment() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Person details = new PersonBuilder().withName("IRIS Tan").build();
            Colleague iris = new Colleague(details.getName(), details.getPhone(), details.getEmail(),
                    details.getAddress(), details.getTags(), "FOOD SCIENCE");

            assertTrue(preparePredicate(Optional.of("iris"), Optional.of(Role.COLLEAGUE), Optional.of("science"),
                    Optional.empty(), Set.of()).test(iris));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void test_allCriteriaMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE),
                Optional.of("food sci"), Optional.of("clementi ave"), Set.of(new Tag("Student"), new Tag("Year2")));

        assertTrue(predicate.test(colleague));
    }

    @Test
    public void test_anySingleCriterionFails_returnsFalse() {
        Set<Tag> tags = Set.of(new Tag("Student"), new Tag("Year2"));

        assertFalse(preparePredicate(Optional.of("Bob"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                Optional.of("clementi ave"), tags).test(colleague));
        assertFalse(preparePredicate(Optional.of("ali"), Optional.of(Role.STUDENT), Optional.of("food sci"),
                Optional.of("clementi ave"), tags).test(colleague));
        assertFalse(preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("mathematics"),
                Optional.of("clementi ave"), tags).test(colleague));
        assertFalse(preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                Optional.of("Bedok"), tags).test(colleague));
        assertFalse(preparePredicate(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("food sci"),
                Optional.of("clementi ave"), Set.of(new Tag("Student"), new Tag("Year3"))).test(colleague));
    }

    @Test
    public void test_fullAddress_returnsTrue() {
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.of("311, Clementi Ave 2"), Set.of());

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_partialAddress_returnsTrue() {
        assertTrue(preparePredicate(Optional.of("Clementi Ave"), Set.of()).test(person));
        assertTrue(preparePredicate(Optional.of("Ave 2"), Set.of()).test(person));
        assertTrue(preparePredicate(Optional.of("menti"), Set.of()).test(person));
    }

    @Test
    public void test_addressDiffersInCase_returnsTrue() {
        assertTrue(preparePredicate(Optional.of("cLeMeNtI aVE"), Set.of()).test(person));
    }

    @Test
    @ResourceLock(Resources.LOCALE)
    public void test_turkishDefaultLocale_matchesAddress() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Person personInIstanbul = new PersonBuilder().withAddress("Istanbul Road").build();
            PersonMatchesFilterPredicate predicate = preparePredicate(Optional.of("istanbul"), Set.of());

            assertTrue(predicate.test(personInIstanbul));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void test_nonContiguousAddress_returnsFalse() {
        assertFalse(preparePredicate(Optional.of("Clementi 2"), Set.of()).test(person));
        assertFalse(preparePredicate(Optional.of("Ave Clementi"), Set.of()).test(person));
    }

    @Test
    public void test_nonMatchingAddress_returnsFalse() {
        assertFalse(preparePredicate(Optional.of("Bugis"), Set.of()).test(person));
    }

    @Test
    public void test_keywordInOtherFields_returnsFalse() {
        Person otherPerson = new PersonBuilder().withName("Clementi").withEmail("clementi@example.com")
                .withAddress("Bugis Street").withTags("Clementi").build();

        assertFalse(preparePredicate(Optional.of("Clementi"), Set.of()).test(otherPerson));
    }

    @Test
    public void test_exactTagWithExtraPersonTags_returnsTrue() {
        assertTrue(preparePredicate(Optional.empty(), Set.of(new Tag("Student"))).test(person));
    }

    @Test
    public void test_tagDiffersInCase_returnsFalse() {
        assertFalse(preparePredicate(Optional.empty(), Set.of(new Tag("student"))).test(person));
        assertFalse(preparePredicate(Optional.empty(), Set.of(new Tag("STUDENT"))).test(person));
    }

    @Test
    public void test_partialOrExtendedTag_returnsFalse() {
        assertFalse(preparePredicate(Optional.empty(), Set.of(new Tag("Stud"))).test(person));
        assertFalse(preparePredicate(Optional.empty(), Set.of(new Tag("Student2026"))).test(person));
    }

    @Test
    public void test_allRequiredTagsPresent_returnsTrue() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.empty(), Set.of(new Tag("Student"), new Tag("Year2")));

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_requiredTagMissing_returnsFalse() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.empty(), Set.of(new Tag("Student"), new Tag("Year3")));

        assertFalse(predicate.test(person));
        assertFalse(predicate.test(new PersonBuilder().withTags().build()));
    }

    @Test
    public void test_differentlyCasedTags_requiresBothExactTags() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.empty(), Set.of(new Tag("Student"), new Tag("student")));

        assertFalse(predicate.test(person));
        assertTrue(predicate.test(new PersonBuilder().withTags("Student", "student").build()));
    }

    @Test
    public void test_addressAndTagsMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate =
                preparePredicate(Optional.of("clementi ave"), Set.of(new Tag("Student"), new Tag("Year2")));

        assertTrue(predicate.test(person));
    }

    @Test
    public void test_onlyOneCriterionMatches_returnsFalse() {
        assertFalse(preparePredicate(Optional.of("Clementi"), Set.of(new Tag("student"))).test(person));
        assertFalse(preparePredicate(Optional.of("Bugis"), Set.of(new Tag("Student"))).test(person));
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
        return preparePredicate(nameKeyword, contactType, departmentKeyword, addressKeyword, requiredTags,
                Set.of(), Optional.empty());
    }

    /** Constructs a predicate including ID alternatives and an optional exact year. */
    private PersonMatchesFilterPredicate preparePredicate(Optional<String> nameKeyword, Optional<Role> contactType,
            Optional<String> departmentKeyword, Optional<String> addressKeyword, Set<Tag> requiredTags,
            Set<Integer> personIds, Optional<Integer> yearOfStudy) {
        return new PersonMatchesFilterPredicate(new PersonFilterCriteria(nameKeyword, contactType, departmentKeyword,
                addressKeyword, requiredTags, personIds, yearOfStudy));
    }

    /** Constructs numeric predicates without adding unrelated matching restrictions. */
    private PersonMatchesFilterPredicate prepareNumericPredicate(Set<Integer> personIds,
            Optional<Integer> yearOfStudy) {
        return preparePredicate(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Set.of(),
                personIds, yearOfStudy);
    }

    /**
     * Constructs a colleague with the shared contact details and an optional department.
     */
    private Colleague prepareColleague(String department) {
        return new Colleague(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                person.getTags(), department);
    }
}
