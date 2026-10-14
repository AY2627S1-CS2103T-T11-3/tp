package seedu.address.testutil;

import static seedu.address.testutil.TypicalGroups.getTypicalGroups;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import seedu.address.model.AddressBook;
import seedu.address.model.group.Group;
import seedu.address.model.person.Person;

public class TypicalAddressBook {

    /**
     * Returns an {@code AddressBook} with all the typical persons.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Person person : getTypicalPersons()) {
            ab.addPerson(person);
        }
        for (Group group: getTypicalGroups()) {
            ab.addGroup(group);
        }
        return ab;
    }

}
