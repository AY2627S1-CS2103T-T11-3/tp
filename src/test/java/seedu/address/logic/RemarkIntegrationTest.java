package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Verifies remark parsing, model updates and persistence together.
 */
public class RemarkIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private JsonAddressBookStorage storage;
    private Person original;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        original = new PersonBuilder().build();
        model.addPerson(original);
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_addReplaceClearRemark_persistsEachChange() throws Exception {
        for (String value : new String[]{"Likes swimming", "New note", ""}) {
            logic.execute("remark 1 r/" + value);
            Person expected = new PersonBuilder(original).withRemark(value).build();
            assertEquals(expected, model.getFilteredPersonList().get(0));
            assertEquals(expected, storage.readAddressBook().orElseThrow().getPersonList().get(0));
        }
    }

    @Test
    public void execute_editOtherFields_preservesRemark() throws Exception {
        logic.execute("remark 1 r/Keep this note");
        logic.execute("edit 1 p/91234567");
        Person expected = new PersonBuilder(original).withRemark("Keep this note").withPhone("91234567").build();
        assertEquals(expected, storage.readAddressBook().orElseThrow().getPersonList().get(0));
    }

    @Test
    public void execute_filteredList_changesDisplayedPerson() throws Exception {
        Person second = new PersonBuilder().withName("Bob Tan").build();
        model.addPerson(second);
        logic.execute("find Bob");
        logic.execute("remark 1 r/Second person");
        assertEquals(original, model.getAddressBook().getPersonList().get(0));
        assertEquals(new PersonBuilder(second).withRemark("Second person").build(),
                model.getAddressBook().getPersonList().get(1));
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_invalidDisplayedIndex_doesNotChangePerson() {
        assertThrows(CommandException.class, () -> logic.execute("remark 2 r/note"));
        assertEquals(original, model.getFilteredPersonList().get(0));
    }
}
