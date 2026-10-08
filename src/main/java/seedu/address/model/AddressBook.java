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
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();

    // Each guardian maps to the students linked to that guardian.
    private final Map<Person, Set<Person>> guardianStudents = new HashMap<>();

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
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        // Capture links before replacing data, including when newData is this object.
        Map<Person, Set<Person>> links = newData.getGuardianStudents();
        setPersons(List.copyOf(newData.getPersonList()));

        links.forEach((guardian, students) ->
                guardianStudents.put(guardian, new HashSet<>(students)));
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
        Set<Person> students = guardianStudents.remove(target);
        if (students != null) {
            guardianStudents.put(editedPerson, students);
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
        guardianStudents.remove(key);
        guardianStudents.values().forEach(students -> students.remove(key));
        guardianStudents.values().removeIf(Set::isEmpty);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public Map<Person, Set<Person>> getGuardianStudents() {
        Map<Person, Set<Person>> snapshot = new HashMap<>();
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
    public void linkGuardianToStudent(Person guardian, Person student) {
        requireNonNull(guardian);
        requireNonNull(student);

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

