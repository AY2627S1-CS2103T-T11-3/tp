package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_NAME_DEPARTMENT;
import static seedu.address.testutil.TypicalGroups.MATH_DPT;
import static seedu.address.testutil.TypicalGroups.RESPECT_5;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.person.PersonId;
import seedu.address.testutil.GroupBuilder;

public class GroupTest {
    @BeforeEach
    public void setUp() {
        PersonId.setNextId(0);
    }

    @Test
    public void constructor() {
        Set<PersonId> personIds = new HashSet<>();
        Group group = new ClassGroup(
                new GroupName("Computer Science"),
                personIds
        );
        personIds.add(new PersonId());
        assertFalse(personIds.equals(group.getPersonIds()));
    }

    @Test
    public void isSameGroup() {
        // same object -> returns true
        assertTrue(RESPECT_5.isSameGroup(RESPECT_5));

        // null -> returns false
        assertFalse(RESPECT_5.isSameGroup(null));

        // same name, all other attributes different -> returns true
        PersonId[] editedPersonIds = { new PersonId() };
        Group editedRespect5 = new GroupBuilder(RESPECT_5)
                .withPersonIds(new HashSet<>(Arrays.asList(editedPersonIds))).build();
        assertTrue(RESPECT_5.isSameGroup(editedRespect5));

        // different name, all other attributes same -> returns false
        editedRespect5 = new GroupBuilder(RESPECT_5).withName(VALID_GROUP_NAME_DEPARTMENT).build();
        assertFalse(RESPECT_5.isSameGroup(editedRespect5));

        // name differs in case, all other attributes same -> returns false
        editedRespect5 = new GroupBuilder(RESPECT_5)
                .withName(RESPECT_5.getGroupName().groupName.toLowerCase()).build();
        assertFalse(RESPECT_5.isSameGroup(editedRespect5));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = RESPECT_5.getGroupName().groupName + " ";
        editedRespect5 = new GroupBuilder(RESPECT_5).withName(nameWithTrailingSpaces).build();
        assertFalse(RESPECT_5.isSameGroup(editedRespect5));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Group respect5Copy = new GroupBuilder(RESPECT_5).build();
        assertTrue(RESPECT_5.equals(respect5Copy));

        // same object -> returns true
        assertTrue(RESPECT_5.equals(RESPECT_5));

        // null -> returns false
        assertFalse(RESPECT_5.equals(null));

        // different type -> returns false
        assertFalse(RESPECT_5.equals(5));

        // different person -> returns false
        assertFalse(RESPECT_5.equals(MATH_DPT));

        // different group name -> returns false
        Group editedRespect5 = new GroupBuilder(RESPECT_5).withName(VALID_GROUP_NAME_DEPARTMENT).build();
        assertFalse(RESPECT_5.equals(editedRespect5));

        // different group type -> returns false
        editedRespect5 = new GroupBuilder(RESPECT_5).withType(GroupType.DEPARTMENT).build();
        assertFalse(RESPECT_5.equals(editedRespect5));

        // different group type -> returns true
        PersonId[] editedPersonIds = { new PersonId() };
        editedRespect5 = new GroupBuilder(RESPECT_5)
                .withPersonIds(new HashSet<>(Arrays.asList(editedPersonIds))).build();
        assertFalse(RESPECT_5.equals(editedRespect5));
    }

    @Test
    public void toStringMethod() {
        String expected = ClassGroup.class.getCanonicalName() + "{group name=" + RESPECT_5.getGroupName()
                + ", person ids=" + RESPECT_5.getPersonIds() + "}";
        assertEquals(expected, RESPECT_5.toString());
    }

}
