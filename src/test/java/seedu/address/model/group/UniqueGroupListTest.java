package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.MATH_DPT;
import static seedu.address.testutil.TypicalGroups.RESPECT_5;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.group.exceptions.DuplicateGroupException;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.GroupBuilder;

public class UniqueGroupListTest {

    private final UniqueGroupList uniqueGroupList = new UniqueGroupList();

    @Test
    public void contains_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.contains(null));
    }

    @Test
    public void contains_groupNotInList_returnsFalse() {
        assertFalse(uniqueGroupList.contains(RESPECT_5));
    }

    @Test
    public void contains_groupInList_returnsTrue() {
        uniqueGroupList.add(RESPECT_5);
        assertTrue(uniqueGroupList.contains(RESPECT_5));
    }

    @Test
    public void add_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.add(null));
    }

    @Test
    public void add_duplicateGroup_throwsDuplicateGroupException() {
        uniqueGroupList.add(RESPECT_5);
        assertThrows(DuplicateGroupException.class, () -> uniqueGroupList.add(RESPECT_5));
    }

    @Test
    public void setGroup_nullTargetGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.setGroup(null, RESPECT_5));
    }

    @Test
    public void setGroup_nullEditedGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.setGroup(RESPECT_5, null));
    }

    @Test
    public void setGroup_targetGroupNotInList_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> uniqueGroupList.setGroup(RESPECT_5, RESPECT_5));
    }

    @Test
    public void setGroup_editedGroupIsSameGroup_success() {
        uniqueGroupList.add(RESPECT_5);
        uniqueGroupList.setGroup(RESPECT_5, RESPECT_5);
        UniqueGroupList expectedUniqueGroupList = new UniqueGroupList();
        expectedUniqueGroupList.add(RESPECT_5);
        assertEquals(expectedUniqueGroupList, uniqueGroupList);
    }

    @Test
    public void setGroup_editedGroupHasSameIdentity_success() {
        uniqueGroupList.add(RESPECT_5);
        List<PersonId> personIds = List.of(new PersonId());
        Group editedRespect5 = new GroupBuilder(RESPECT_5).withPersonIds(new HashSet<>(personIds)).build();
        uniqueGroupList.setGroup(RESPECT_5, editedRespect5);
        UniqueGroupList expectedUniqueGroupList = new UniqueGroupList();
        expectedUniqueGroupList.add(editedRespect5);
        assertEquals(expectedUniqueGroupList, uniqueGroupList);
    }

    @Test
    public void setGroup_editedGroupHasDifferentIdentity_success() {
        uniqueGroupList.add(RESPECT_5);
        uniqueGroupList.setGroup(RESPECT_5, MATH_DPT);
        UniqueGroupList expectedUniqueGroupList = new UniqueGroupList();
        expectedUniqueGroupList.add(MATH_DPT);
        assertEquals(expectedUniqueGroupList, uniqueGroupList);
    }

    @Test
    public void setGroup_editedGroupHasNonUniqueIdentity_throwsDuplicateGroupException() {
        uniqueGroupList.add(RESPECT_5);
        uniqueGroupList.add(MATH_DPT);
        assertThrows(DuplicateGroupException.class, () -> uniqueGroupList.setGroup(RESPECT_5, MATH_DPT));
    }

    @Test
    public void remove_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.remove(null));
    }

    @Test
    public void remove_groupDoesNotExist_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> uniqueGroupList.remove(RESPECT_5));
    }

    @Test
    public void remove_existingGroup_removesGroup() {
        uniqueGroupList.add(RESPECT_5);
        uniqueGroupList.remove(RESPECT_5);
        UniqueGroupList expectedUniqueGroupList = new UniqueGroupList();
        assertEquals(expectedUniqueGroupList, uniqueGroupList);
    }

    @Test
    public void setGroups_nullUniqueGroupList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.setGroups((UniqueGroupList) null));
    }

    @Test
    public void setGroups_uniqueGroupList_replacesOwnListWithProvidedUniqueGroupList() {
        uniqueGroupList.add(RESPECT_5);
        UniqueGroupList expectedUniqueGroupList = new UniqueGroupList();
        expectedUniqueGroupList.add(MATH_DPT);
        uniqueGroupList.setGroups(expectedUniqueGroupList);
        assertEquals(expectedUniqueGroupList, uniqueGroupList);
    }

    @Test
    public void setGroups_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.setGroups((List<Group>) null));
    }

    @Test
    public void setGroups_list_replacesOwnListWithProvidedList() {
        uniqueGroupList.add(RESPECT_5);
        List<Group> groupList = List.of(MATH_DPT);
        uniqueGroupList.setGroups(groupList);
        UniqueGroupList expectedUniqueGroupList = new UniqueGroupList();
        expectedUniqueGroupList.add(MATH_DPT);
        assertEquals(expectedUniqueGroupList, uniqueGroupList);
    }

    @Test
    public void setGroups_listWithDuplicateGroups_throwsDuplicateGroupException() {
        List<Group> listWithDuplicateGroups = List.of(RESPECT_5, RESPECT_5);
        assertThrows(DuplicateGroupException.class, () -> uniqueGroupList.setGroups(listWithDuplicateGroups));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
                -> uniqueGroupList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        assertEquals(uniqueGroupList.asUnmodifiableObservableList().toString(), uniqueGroupList.toString());
    }
}
