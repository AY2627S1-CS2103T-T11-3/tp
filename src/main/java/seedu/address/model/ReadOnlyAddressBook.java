package seedu.address.model;

import java.util.Map;
import java.util.Set;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.Guardian;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns an immutable snapshot of guardian-to-student relationships.
     */
    Map<Guardian, Set<Person>> getGuardianStudents();

}
