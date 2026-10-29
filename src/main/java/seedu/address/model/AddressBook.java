package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupType;
import seedu.address.model.group.UniqueGroupList;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Person;
import seedu.address.model.person.Role;
import seedu.address.model.person.Student;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final UniqueGroupList groups = new UniqueGroupList();

    // Each guardian maps to the students linked to that guardian.
    private final Map<Guardian, Set<Person>> guardianStudents = new HashMap<>();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
        // Replacing all contacts invalidates the existing relationships.
        guardianStudents.clear();
    }

    /**
     * Replaces the contents of the group list with {@code groups}.
     * {@code groups} may contain groups with the same name and type.
     */
    public void setGroups(List<Group> groups) {
        this.groups.setGroups(groups);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        // Capture links before replacing data, including when newData is this object.
        Map<Guardian, Set<Person>> links = newData.getGuardianStudents();
        setPersons(List.copyOf(newData.getPersonList()));

        links.forEach((guardian, students) ->
                guardianStudents.put(guardian, new HashSet<>(students)));

        setGroups(List.copyOf(newData.getGroupList()));
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        // Replace references because editing creates a new Person object.
        Set<Person> students = null;
        if (target instanceof Guardian guardian) {
            students = guardianStudents.remove(guardian);
        }
        if (students != null && editedPerson instanceof Guardian editedGuardian) {
            guardianStudents.put(editedGuardian, students);
        }

        for (Set<Person> linkedStudents : guardianStudents.values()) {
            if (linkedStudents.remove(target)) {
                linkedStudents.add(editedPerson);
            }
        }
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        // Remove links involving the deleted contact.
        if (key instanceof Guardian guardian) {
            guardianStudents.remove(guardian);
        }
        guardianStudents.values().forEach(students -> students.remove(key));
        guardianStudents.values().removeIf(Set::isEmpty);
    }

    //// group-level operations

    /**
     * Returns true if a group with the same identity as {@code group} exists in the address book.
     */
    public boolean hasGroup(Group group) {
        requireNonNull(group);
        return groups.contains(group);
    }

    /**
     * Adds a group to the address book.
     * The group must not already exist in the address book.
     */
    public void addGroup(Group g) {
        groups.add(g);
    }

    /**
     * Replaces the given group {@code target} in the list with {@code editedGroup}.
     * {@code target} must exist in the address book.
     * The group identity of {@code editedGroup} must not be the same as another existing group in the address book.
     */
    public void setGroup(Group target, Group editedGroup) {
        requireNonNull(editedGroup);
        groups.setGroup(target, editedGroup);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book. Contacts assigned to the group are unassigned.
     */
    public void removeGroup(Group key) {
        groups.remove(key);
        String deletedGroupName = key.getGroupName().groupName;
        List<Person> currentPersons = List.copyOf(persons.asUnmodifiableObservableList());
        currentPersons.stream()
                .filter(person -> belongsToGroup(person, key.getGroupType(), deletedGroupName))
                .forEach(person -> setPerson(person, withoutGroup(person)));
    }

    /** Returns true if {@code person} belongs to the specified group by name and type. */
    private boolean belongsToGroup(Person person, GroupType groupType, String groupName) {
        if (!groupName.equals(person.getGroupId())) {
            return false;
        }
        return (groupType == GroupType.CLASS && person instanceof Student)
                || (groupType == GroupType.DEPARTMENT && person instanceof Colleague);
    }

    /** Returns a copy of {@code person} with no group assignment. */
    private Person withoutGroup(Person person) {
        if (person instanceof Student student) {
            return new Student(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                    person.getTags(), person.getId().getRawId(), null, student.getYearOfStudy());
        }
        Colleague colleague = (Colleague) person;
        return new Colleague(colleague.getName(), colleague.getPhone(), colleague.getEmail(), colleague.getAddress(),
                colleague.getTags(), colleague.getId().getRawId(), null);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("groups", groups)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Group> getGroupList() {
        return groups.asUnmodifiableObservableList();
    }

    @Override
    public Map<Guardian, Set<Person>> getGuardianStudents() {
        Map<Guardian, Set<Person>> snapshot = new HashMap<>();
        guardianStudents.forEach((guardian, students) ->
                snapshot.put(guardian, Set.copyOf(students)));
        return Map.copyOf(snapshot);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && guardianStudents.equals(otherAddressBook.guardianStudents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, guardianStudents);
    }

    /** Links existing contacts without modifying their contact details. */
    public void linkGuardianToStudent(Guardian guardian, Student student) {
        requireNonNull(guardian);
        requireNonNull(student);

        if (guardian.getRole() != Role.GUARDIAN
                || student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Contacts have incompatible roles.");
        }

        if (!hasPerson(guardian) || !hasPerson(student)) {
            throw new IllegalArgumentException("Both contacts must exist.");
        }

        if (guardian.equals(student)) {
            throw new IllegalArgumentException("A contact cannot be linked to itself.");
        }

        Set<Person> students =
                guardianStudents.computeIfAbsent(guardian, unused -> new HashSet<>());

        if (!students.add(student)) {
            throw new IllegalArgumentException("These contacts are already linked.");
        }
    }
}

