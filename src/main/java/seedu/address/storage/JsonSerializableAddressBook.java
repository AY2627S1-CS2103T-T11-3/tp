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
import seedu.address.model.group.Group;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Student;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_GROUP = "Group list contains duplicate groups(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedGroup> groups = new ArrayList<>();

    // Each pair contains [guardian position, student position], both zero-based.
    // Positions refer to the persons array in this same JSON file.
    private final List<List<Integer>> guardianLinks = new ArrayList<>();

    private final Integer nextId;

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and groups
     */
    public JsonSerializableAddressBook(List<JsonAdaptedPerson> persons, List<JsonAdaptedGroup> groups) {
        this(persons, null, groups, 1);
    }

    /**
     * Loads contacts and optional links. Older files have no guardianLinks field.
     */
    @JsonCreator
    public JsonSerializableAddressBook(
            @JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("guardianLinks") List<List<Integer>> guardianLinks,
            @JsonProperty("groups") List<JsonAdaptedGroup> groups,
            @JsonProperty("nextId") Integer nextId
    ) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (guardianLinks != null) {
            this.guardianLinks.addAll(guardianLinks);
        }
        if (groups != null) {
            this.groups.addAll(groups);
        }
        this.nextId = nextId;
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

        List<Group> sourceGroups = source.getGroupList();
        groups.addAll(sourceGroups.stream()
                .map(JsonAdaptedGroup::new)
                .collect(Collectors.toList()));

        // PersonId class handles the next ID by itself
        nextId = PersonId.getNextId();
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        // PersonId class handles the next ID by itself
        if (nextId != null) {
            PersonId.setNextId(nextId);
        }

        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = nextId == null
                    ? jsonAdaptedPerson.toLegacyModelType()
                    : jsonAdaptedPerson.toModelType();
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
                Person guardian = contacts.get(guardianPosition);
                Person student = contacts.get(studentPosition);
                if (!(guardian instanceof Guardian)) {
                    throw new IllegalArgumentException("Guardian link refers to a non-guardian.");
                }
                if (!(student instanceof Student)) {
                    throw new IllegalArgumentException("Guardian link refers to a non-student.");
                }
                addressBook.linkGuardianToStudent(
                        (Guardian) guardian,
                        (Student) student);
            } catch (IllegalArgumentException e) {
                // Self-links and duplicate links are invalid stored data.
                throw new IllegalValueException(e.getMessage());
            }
        }

        for (JsonAdaptedGroup jsonAdaptedGroup : groups) {
            Group group = jsonAdaptedGroup.toModelType();
            if (addressBook.hasGroup(group)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_GROUP);
            }
            addressBook.addGroup(group);
        }
        return addressBook;
    }

}
