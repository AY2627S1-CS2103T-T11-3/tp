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
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class GuardianLinkTest {
    @Test
    public void link_saveLoadEditDelete_preservesRelationships() throws Exception {
        AddressBook book = new AddressBook();
        book.setPersons(List.of(ALICE, BOB));
        ModelManager model = new ModelManager(book, new UserPrefs());
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("link-guardian s/1 g/2").execute(model);
        assertEquals(Map.of(BOB, Set.of(ALICE)), model.getAddressBook().getGuardianStudents());

        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(model.getAddressBook()));
        AddressBook restored = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class).toModelType();
        assertEquals(model.getAddressBook(), new ModelManager(restored, new UserPrefs()).getAddressBook());
        restored.resetData(restored);
        assertEquals(Map.of(BOB, Set.of(ALICE)), restored.getGuardianStudents());

        Person edited = new PersonBuilder(ALICE).withName("Edited Alice").build();
        restored.setPerson(ALICE, edited);
        assertEquals(Map.of(BOB, Set.of(edited)), restored.getGuardianStudents());
        restored.removePerson(BOB);
        assertEquals(Map.of(), restored.getGuardianStudents());
    }

    @Test
    public void link_invalidInput_reportsErrors() throws Exception {
        AddressBook book = new AddressBook();
        book.setPersons(List.of(ALICE, BOB));
        ModelManager model = new ModelManager(book, new UserPrefs());
        AddressBookParser parser = new AddressBookParser();
        for (String args : List.of("s/1", "s/0 g/2", "s/1 g/2 g/1", "s/x g/2")) {
            assertThrows(ParseException.class, () -> parser.parseCommand("link-guardian " + args));
        }
        for (String args : List.of("s/3 g/2", "s/1 g/3", "s/1 g/1")) {
            assertThrows(CommandException.class, () ->
                    parser.parseCommand("link-guardian " + args).execute(model));
        }
        parser.parseCommand("link-guardian g/2 s/1").execute(model);
        assertThrows(CommandException.class, () ->
                parser.parseCommand("link-guardian s/1 g/2").execute(model));
    }

    @Test
    public void storage_oldAndInvalidLinks_handlesData() throws Exception {
        AddressBook book = new AddressBook();
        book.setPersons(List.of(ALICE, BOB));
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(book));
        String oldJson = json.replaceAll("(?s),\\s*\"guardianLinks\"\\s*:\\s*\\[\\s*\\]", "");
        assertEquals(book, JsonUtil.fromJsonString(oldJson, JsonSerializableAddressBook.class).toModelType());
        List<JsonAdaptedPerson> persons = List.of(new JsonAdaptedPerson(ALICE), new JsonAdaptedPerson(BOB));
        for (List<List<Integer>> links : List.of(List.of(List.of(2, 0)), List.of(List.of(0, 0)),
                List.of(List.of(1, 0), List.of(1, 0)), List.of(List.of(1)))) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonSerializableAddressBook(persons, links).toModelType());
        }
    }
}
