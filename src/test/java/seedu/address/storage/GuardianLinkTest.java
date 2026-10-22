package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Person;
import seedu.address.model.person.Student;
import seedu.address.testutil.PersonBuilder;

public class GuardianLinkTest {
    private static final Guardian BOB_GUARDIAN = new Guardian(
            BOB.getName(),
            BOB.getPhone(),
            BOB.getEmail(),
            BOB.getAddress(),
            BOB.getTags());

    @Test
    public void link_saveLoadEditDelete_preservesRelationships() throws Exception {
        Student aliceStudent = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                ALICE.getTags());

        // The displayed list is [aliceStudent (student), Bob (guardian)], so s/1 g/2 links Bob to aliceStudent.
        AddressBook book = new AddressBook();
        book.setPersons(List.of(aliceStudent, BOB_GUARDIAN));
        ModelManager model = new ModelManager(book, new UserPrefs());
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("link-guardian s/1 g/2").execute(model);
        assertEquals(Map.of(BOB_GUARDIAN, Set.of(aliceStudent)), model.getAddressBook().getGuardianStudents());

        // Serialising and deserialising should preserve both contacts and their relationship.
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(model.getAddressBook()));
        AddressBook restored = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class).toModelType();
        assertEquals(model.getAddressBook(), new ModelManager(restored, new UserPrefs()).getAddressBook());
        restored.resetData(restored);
        assertEquals(Map.of(BOB_GUARDIAN, Set.of(aliceStudent)), restored.getGuardianStudents());

        // Editing a linked contact should update the relationship reference.
        Person edited = new PersonBuilder(aliceStudent).withName("Edited aliceStudent").build();
        restored.setPerson(aliceStudent, edited);
        assertEquals(Map.of(BOB_GUARDIAN, Set.of(edited)), restored.getGuardianStudents());
        // Deleting the guardian should remove the relationship completely.
        restored.removePerson(BOB_GUARDIAN);
        assertEquals(Map.of(), restored.getGuardianStudents());
    }

    @Test
    public void link_invalidInput_reportsErrors() throws Exception {
        Student aliceStudent = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                ALICE.getTags());

        AddressBook book = new AddressBook();
        book.setPersons(List.of(aliceStudent, BOB_GUARDIAN));
        ModelManager model = new ModelManager(book, new UserPrefs());
        AddressBookParser parser = new AddressBookParser();

        // These malformed arguments should fail during parsing.
        for (String args : List.of("s/1", "s/0 g/2", "s/1 g/2 g/1", "s/x g/2")) {
            assertThrows(ParseException.class, () -> parser.parseCommand("link-guardian " + args));
        }
        // These validly shaped commands should fail during execution.
        for (String args : List.of("s/3 g/2", "s/1 g/3", "s/1 g/1")) {
            assertThrows(CommandException.class, () ->
                    parser.parseCommand("link-guardian " + args).execute(model));
        }
        // Prefix order is flexible, but repeating the same relationship is rejected.
        parser.parseCommand("link-guardian g/2 s/1").execute(model);
        assertThrows(CommandException.class, () ->
                parser.parseCommand("link-guardian s/1 g/2").execute(model));
    }

    @Test
    public void linkGuardianToStudent_validContacts_createsRelationship()
            throws Exception {
        Student aliceStudent = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                ALICE.getTags());

        // Create a real Guardian and place it after the student in the displayed list.
        Guardian guardian = new Guardian(
                BOB.getName(),
                BOB.getPhone(),
                BOB.getEmail(),
                BOB.getAddress(),
                BOB.getTags());

        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(aliceStudent, guardian));

        ModelManager model = new ModelManager(
                addressBook,
                new UserPrefs());

        AddressBookParser parser = new AddressBookParser();

        // The command uses displayed-list indices, not contact names.
        parser.parseCommand("link-guardian s/1 g/2")
                .execute(model);

        assertEquals(
                Map.of(guardian, Set.of(aliceStudent)),
                model.getAddressBook().getGuardianStudents());
    }

    @Test
    public void linkGuardianToStudent_nonGuardianSelected_throwsCommandException() {
        // ALICE and BOB are ordinary Person objects, so BOB cannot be selected as a Guardian.
        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(ALICE, BOB));
        ModelManager model = new ModelManager(addressBook, new UserPrefs());
        AddressBookParser parser = new AddressBookParser();

        assertThrows(CommandException.class, () ->
                parser.parseCommand("link-guardian s/1 g/2").execute(model));
    }

    @Test
    public void editLinkedStudent_preservesGuardianRelationship() throws Exception {
        Student aliceStudent = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                ALICE.getTags());

        // Set up a student and Guardian in the displayed order used by s/1 g/2.
        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(aliceStudent, BOB_GUARDIAN));
        ModelManager model = new ModelManager(addressBook, new UserPrefs());
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("link-guardian s/1 g/2").execute(model);

        // Editing replaces the student object, so the relationship must point to the replacement.
        Person editedStudent = new PersonBuilder(aliceStudent)
                .withName("Edited aliceStudent")
                .build();
        model.setPerson(aliceStudent, editedStudent);

        assertEquals(
                Map.of(BOB_GUARDIAN, Set.of(editedStudent)),
                model.getAddressBook().getGuardianStudents());
    }

    @Test
    public void storage_oldAndInvalidLinks_handlesData() throws Exception {
        Student aliceStudent = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                ALICE.getTags());

        // Older files without guardianLinks should still load successfully.
        AddressBook book = new AddressBook();
        book.setPersons(List.of(aliceStudent, BOB_GUARDIAN));
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(book));
        String oldJson = json.replaceAll("(?s),\\s*\"guardianLinks\"\\s*:\\s*\\[\\s*\\]", "");
        assertEquals(book, JsonUtil.fromJsonString(oldJson, JsonSerializableAddressBook.class).toModelType());
        List<JsonAdaptedPerson> persons = List.of(new JsonAdaptedPerson(aliceStudent),
                new JsonAdaptedPerson(BOB_GUARDIAN));
        // Stored links must have two valid positions and must not be self-links or duplicates.
        for (List<List<Integer>> links : List.of(List.of(List.of(2, 0)), List.of(List.of(0, 0)),
                List.of(List.of(1, 0), List.of(1, 0)), List.of(List.of(1)),
                List.of(List.of(0, 1)))) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonSerializableAddressBook(persons, links, 1).toModelType());
        }
    }
}
