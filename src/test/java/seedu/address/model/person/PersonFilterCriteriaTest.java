package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;

/**
 * Tests the value semantics and immutability of {@code PersonFilterCriteria}.
 */
public class PersonFilterCriteriaTest {

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonFilterCriteria(null, Set.of()));
        assertThrows(NullPointerException.class, () -> new PersonFilterCriteria(Optional.empty(), null));

        Set<Tag> tagsWithNull = new HashSet<>();
        tagsWithNull.add(null);
        assertThrows(NullPointerException.class, () -> new PersonFilterCriteria(Optional.empty(), tagsWithNull));
    }

    @Test
    public void constructor_extendedNullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonFilterCriteria(null, Optional.empty(),
                Optional.empty(), Optional.empty(), Set.of()));
        assertThrows(NullPointerException.class, () -> new PersonFilterCriteria(Optional.empty(), null,
                Optional.empty(), Optional.empty(), Set.of()));
        assertThrows(NullPointerException.class, () -> new PersonFilterCriteria(Optional.empty(), Optional.empty(),
                null, Optional.empty(), Set.of()));
        assertThrows(NullPointerException.class, () -> new PersonFilterCriteria(Optional.empty(), Optional.empty(),
                Optional.empty(), null, Set.of()));
        assertThrows(NullPointerException.class, () -> new PersonFilterCriteria(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), null));

        Set<Tag> tagsWithNull = new HashSet<>();
        tagsWithNull.add(null);
        assertThrows(NullPointerException.class, () -> new PersonFilterCriteria(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), tagsWithNull));
    }

    @Test
    public void constructor_emptyCriteria_storesNoRestrictions() {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.empty(), Set.of());

        assertEquals(Optional.empty(), criteria.getNameKeyword());
        assertEquals(Optional.empty(), criteria.getContactType());
        assertEquals(Optional.empty(), criteria.getDepartmentKeyword());
        assertEquals(Optional.empty(), criteria.getAddressKeyword());
        assertTrue(criteria.getRequiredTags().isEmpty());
    }

    @Test
    public void constructor_emptyExtendedCriteria_matchesLegacyConstructor() {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Set.of());

        assertEquals(new PersonFilterCriteria(Optional.empty(), Set.of()), criteria);
    }

    @Test
    public void constructor_allCriteria_preservesValues() {
        Set<Tag> tags = Set.of(new Tag("Committee"), new Tag("Staff"));
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.of("aLiCe Tan"), Optional.of(Role.COLLEAGUE),
                Optional.of("Food Sci"), Optional.of("Clementi Ave"), tags);

        assertEquals(Optional.of("aLiCe Tan"), criteria.getNameKeyword());
        assertEquals(Optional.of(Role.COLLEAGUE), criteria.getContactType());
        assertEquals(Optional.of("Food Sci"), criteria.getDepartmentKeyword());
        assertEquals(Optional.of("Clementi Ave"), criteria.getAddressKeyword());
        assertEquals(tags, criteria.getRequiredTags());
    }

    @Test
    public void constructor_suppliedCriteria_preservesValues() {
        Set<Tag> tags = Set.of(new Tag("Student"), new Tag("student"));
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.of("cLeMeNtI Ave"), tags);

        assertEquals(Optional.of("cLeMeNtI Ave"), criteria.getAddressKeyword());
        assertEquals(tags, criteria.getRequiredTags());
    }

    @Test
    public void constructor_inputSetModified_keepsDefensiveCopy() {
        Set<Tag> originalTags = new HashSet<>(Set.of(new Tag("Student")));
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.empty(), originalTags);
        PersonFilterCriteria extendedCriteria = new PersonFilterCriteria(Optional.of("ali"),
                Optional.of(Role.COLLEAGUE), Optional.of("science"), Optional.of("Clementi"), originalTags);

        originalTags.clear();
        originalTags.add(new Tag("Teacher"));

        assertEquals(Set.of(new Tag("Student")), criteria.getRequiredTags());
        assertEquals(Set.of(new Tag("Student")), extendedCriteria.getRequiredTags());
    }

    @Test
    public void getRequiredTags_modifySet_throwsUnsupportedOperationException() {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.empty(), Set.of(new Tag("Student")));

        assertThrows(UnsupportedOperationException.class, () -> criteria.getRequiredTags().add(new Tag("Teacher")));
        assertThrows(UnsupportedOperationException.class, () -> criteria.getRequiredTags().remove(new Tag("Student")));
        assertThrows(UnsupportedOperationException.class, () -> criteria.getRequiredTags().clear());
    }

    @Test
    public void equals() {
        PersonFilterCriteria criteria =
                new PersonFilterCriteria(Optional.of("Clementi"), Set.of(new Tag("Student"), new Tag("Year2")));
        PersonFilterCriteria copy =
                new PersonFilterCriteria(Optional.of("Clementi"), Set.of(new Tag("Year2"), new Tag("Student")));

        assertTrue(criteria.equals(criteria));
        assertTrue(criteria.equals(copy));
        assertFalse(criteria.equals(null));
        assertFalse(criteria.equals(1));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.of("Bugis"), criteria.getRequiredTags())));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.empty(), criteria.getRequiredTags())));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.of("Clementi"), Set.of(new Tag("Student")))));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.of("Clementi"),
                Set.of(new Tag("student"), new Tag("Year2")))));
    }

    @Test
    public void equals_newFieldsDiffer_returnsFalse() {
        Set<Tag> tags = Set.of(new Tag("Committee"));
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.of("ali"), Optional.of(Role.COLLEAGUE),
                Optional.of("science"), Optional.of("Clementi"), tags);
        PersonFilterCriteria copy = new PersonFilterCriteria(Optional.of("ali"), Optional.of(Role.COLLEAGUE),
                Optional.of("science"), Optional.of("Clementi"), tags);

        assertTrue(criteria.equals(copy));
        assertTrue(copy.equals(criteria));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.of("bob"), Optional.of(Role.COLLEAGUE),
                Optional.of("science"), Optional.of("Clementi"), tags)));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.empty(), Optional.of(Role.COLLEAGUE),
                Optional.of("science"), Optional.of("Clementi"), tags)));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.of("ali"), Optional.of(Role.STUDENT),
                Optional.of("science"), Optional.of("Clementi"), tags)));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.of("ali"), Optional.empty(),
                Optional.of("science"), Optional.of("Clementi"), tags)));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.of("ali"), Optional.of(Role.COLLEAGUE),
                Optional.of("mathematics"), Optional.of("Clementi"), tags)));
        assertFalse(criteria.equals(new PersonFilterCriteria(Optional.of("ali"), Optional.of(Role.COLLEAGUE),
                Optional.empty(), Optional.of("Clementi"), tags)));
    }

    @Test
    public void hashCode_equalCriteria_returnsSameHashCode() {
        PersonFilterCriteria criteria =
                new PersonFilterCriteria(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("science"),
                        Optional.of("Clementi"), Set.of(new Tag("Student"), new Tag("Year2")));
        PersonFilterCriteria copy =
                new PersonFilterCriteria(Optional.of("ali"), Optional.of(Role.COLLEAGUE), Optional.of("science"),
                        Optional.of("Clementi"), Set.of(new Tag("Year2"), new Tag("Student")));

        assertEquals(criteria.hashCode(), copy.hashCode());
    }

    @Test
    public void toStringMethod() {
        PersonFilterCriteria criteria =
                new PersonFilterCriteria(Optional.of("Clementi"), Set.of(new Tag("Student")));
        String expected = PersonFilterCriteria.class.getCanonicalName()
                + "{nameKeyword=Optional.empty, contactType=Optional.empty, departmentKeyword=Optional.empty, "
                + "addressKeyword=Optional[Clementi], requiredTags=[[Student]]}";

        assertEquals(expected, criteria.toString());
    }

    @Test
    public void toStringMethod_allCriteria_includesNewFields() {
        PersonFilterCriteria criteria = new PersonFilterCriteria(Optional.of("ali"), Optional.of(Role.COLLEAGUE),
                Optional.of("science"), Optional.of("Clementi"), Set.of(new Tag("Committee")));
        String expected = PersonFilterCriteria.class.getCanonicalName()
                + "{nameKeyword=Optional[ali], contactType=Optional[COLLEAGUE], departmentKeyword=Optional[science], "
                + "addressKeyword=Optional[Clementi], requiredTags=[[Committee]]}";

        assertEquals(expected, criteria.toString());
    }
}
