package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.MATH_DPT;
import static seedu.address.testutil.TypicalGroups.RESPECT_5;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.testutil.GroupBuilder;

public class GroupListTest {

    private final GroupList groupList = new GroupList();

    @Test
    public void contains_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> groupList.contains(null));
    }

    @Test
    public void contains_groupNotInList_returnsFalse() {
        assertFalse(groupList.contains(RESPECT_5));
    }

    @Test
    public void contains_groupInList_returnsTrue() {
        groupList.add(RESPECT_5);
        assertTrue(groupList.contains(RESPECT_5));
    }

    @Test
    public void add_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> groupList.add(null));
    }

    @Test
    public void add_duplicateGroup_acceptsDuplicateGroup() {
        groupList.add(RESPECT_5);
        groupList.add(RESPECT_5);
        assertEquals(List.of(RESPECT_5, RESPECT_5), groupList.asUnmodifiableObservableList());
    }

    @Test
    public void setGroup_nullTargetGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> groupList.setGroup(null, RESPECT_5));
    }

    @Test
    public void setGroup_nullEditedGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> groupList.setGroup(RESPECT_5, null));
    }

    @Test
    public void setGroup_targetGroupNotInList_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> groupList.setGroup(RESPECT_5, RESPECT_5));
    }

    @Test
    public void setGroup_editedGroupIsSameGroup_success() {
        groupList.add(RESPECT_5);
        groupList.setGroup(RESPECT_5, RESPECT_5);
        GroupList expectedGroupList = new GroupList();
        expectedGroupList.add(RESPECT_5);
        assertEquals(expectedGroupList, groupList);
    }

    @Test
    public void setGroup_editedGroupHasSameIdentity_success() {
        groupList.add(RESPECT_5);
        Group editedRespect5 = new GroupBuilder(RESPECT_5).build();
        groupList.setGroup(RESPECT_5, editedRespect5);
        GroupList expectedGroupList = new GroupList();
        expectedGroupList.add(editedRespect5);
        assertEquals(expectedGroupList, groupList);
    }

    @Test
    public void setGroup_editedGroupHasDifferentIdentity_success() {
        groupList.add(RESPECT_5);
        groupList.setGroup(RESPECT_5, MATH_DPT);
        GroupList expectedGroupList = new GroupList();
        expectedGroupList.add(MATH_DPT);
        assertEquals(expectedGroupList, groupList);
    }

    @Test
    public void setGroup_editedGroupHasNonUniqueIdentity_acceptsDuplicateGroup() {
        groupList.add(RESPECT_5);
        groupList.add(MATH_DPT);
        groupList.setGroup(RESPECT_5, MATH_DPT);
        assertEquals(List.of(MATH_DPT, MATH_DPT), groupList.asUnmodifiableObservableList());
    }

    @Test
    public void remove_nullGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> groupList.remove(null));
    }

    @Test
    public void remove_groupDoesNotExist_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> groupList.remove(RESPECT_5));
    }

    @Test
    public void remove_existingGroup_removesGroup() {
        groupList.add(RESPECT_5);
        groupList.remove(RESPECT_5);
        GroupList expectedGroupList = new GroupList();
        assertEquals(expectedGroupList, groupList);
    }

    @Test
    public void setGroups_nullGroupList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> groupList.setGroups((GroupList) null));
    }

    @Test
    public void setGroups_groupList_replacesOwnListWithProvidedGroupList() {
        groupList.add(RESPECT_5);
        GroupList expectedGroupList = new GroupList();
        expectedGroupList.add(MATH_DPT);
        groupList.setGroups(expectedGroupList);
        assertEquals(expectedGroupList, groupList);
    }

    @Test
    public void setGroups_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> groupList.setGroups((List<Group>) null));
    }

    @Test
    public void setGroups_list_replacesOwnListWithProvidedList() {
        groupList.add(RESPECT_5);
        List<Group> replacement = List.of(MATH_DPT);
        groupList.setGroups(replacement);
        GroupList expectedGroupList = new GroupList();
        expectedGroupList.add(MATH_DPT);
        assertEquals(expectedGroupList, groupList);
    }

    @Test
    public void setGroups_listWithDuplicateGroups_acceptsDuplicateGroups() {
        List<Group> listWithDuplicateGroups = List.of(RESPECT_5, RESPECT_5);
        groupList.setGroups(listWithDuplicateGroups);
        assertEquals(listWithDuplicateGroups, groupList.asUnmodifiableObservableList());
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
                -> groupList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        assertEquals(groupList.asUnmodifiableObservableList().toString(), groupList.toString());
    }
}
