package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests address substring matching, exact tag matching, and the combination of criteria.
 */
public class PersonMatchesFilterPredicateTest {

    private final Person person = new PersonBuilder().withAddress("311, Clementi Ave 2")
            .withTags("Student", "Year2").build();

    @Test
    public void constructor_nullCriteria_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonMatchesFilterPredicate(null));
    }

    @Test
    public void test_emptyCriteria_returnsTrue() {
        PersonMatchesFilterPredicate predicate = preparePredicate(Optional.empty(), Set.of());

        assertTrue(predicate.test(person));
        assertTrue(predicate.test(new PersonBuilder().withTags().build()));
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
                preparePredicate(Optional.of("Clementi"), Set.of(new Tag("Student")));
        PersonMatchesFilterPredicate copy =
                preparePredicate(Optional.of("Clementi"), Set.of(new Tag("Student")));

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
        return new PersonMatchesFilterPredicate(new PersonFilterCriteria(addressKeyword, requiredTags));
    }
}
