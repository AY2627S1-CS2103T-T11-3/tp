package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    // Each pair contains [guardian position, student position], both zero-based.
    // Positions refer to the persons array in this same JSON file.
    private final List<List<Integer>> guardianLinks = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons.
     */
    public JsonSerializableAddressBook(List<JsonAdaptedPerson> persons) {
        this(persons, null);
    }

    /**
     * Loads contacts and optional links. Older files have no guardianLinks field.
     */
    @JsonCreator
    public JsonSerializableAddressBook(
            @JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("guardianLinks") List<List<Integer>> guardianLinks) {
        this.persons.addAll(persons);
        if (guardianLinks != null) {
            this.guardianLinks.addAll(guardianLinks);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        List<Person> contacts = source.getPersonList();
        persons.addAll(contacts.stream()
                .map(JsonAdaptedPerson::new)
                .collect(Collectors.toList()));

        source.getGuardianStudents().forEach((guardian, students) ->
                students.forEach(student -> guardianLinks.add(List.of(
                        contacts.indexOf(guardian),
                        contacts.indexOf(student)))));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        // Restore relationships only after all contacts have been loaded.
        List<Person> contacts = addressBook.getPersonList();
        for (List<Integer> link : guardianLinks) {
            if (link == null || link.size() != 2
                    || link.get(0) == null || link.get(1) == null) {
                throw new IllegalValueException("Invalid guardian link: expected two positions.");
            }

            int guardianPosition = link.get(0);
            int studentPosition = link.get(1);

            if (guardianPosition < 0 || guardianPosition >= contacts.size()
                    || studentPosition < 0 || studentPosition >= contacts.size()) {
                throw new IllegalValueException("Guardian link refers to a missing contact.");
            }

            try {
                addressBook.linkGuardianToStudent(
                        contacts.get(guardianPosition),
                        contacts.get(studentPosition));
            } catch (IllegalArgumentException e) {
                // Self-links and duplicate links are invalid stored data.
                throw new IllegalValueException(e.getMessage());
            }
        }
        return addressBook;
    }

}
