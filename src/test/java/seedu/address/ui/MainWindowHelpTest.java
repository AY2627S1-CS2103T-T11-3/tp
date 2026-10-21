package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.FxTestUtil.runAndWait;
import static seedu.address.testutil.TypicalAddressBook.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.FxTestUtil;

public class MainWindowHelpTest {
    @TempDir
    public Path testFolder;

    private MainWindow mainWindow;
    private Model model;
    private TextField commandField;
    private TextArea feedback;
    private Button helpButton;

    @BeforeAll
    public static void initializeToolkit() throws Exception {
        FxTestUtil.initialize();
    }

    @BeforeEach
    public void setUp() throws Exception {
        runAndWait(() -> {
            model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
            Path addressBookFile = testFolder.resolve("addressbook.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(addressBookFile),
                    new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
            mainWindow = new MainWindow(new Stage(), new LogicManager(model, storage), addressBookFile);
            mainWindow.show();
            mainWindow.fillInnerParts();
            mainWindow.getRoot().getScene().getRoot().applyCss();
            mainWindow.getRoot().getScene().getRoot().layout();
            commandField = lookup("#commandTextField", TextField.class);
            feedback = lookup("#resultDisplay", TextArea.class);
            helpButton = lookup(".menu-bar-help", Button.class);
        });
    }

    @AfterEach
    public void tearDown() throws Exception {
        runAndWait(() -> new ArrayList<>(Window.getWindows()).forEach(Window::hide));
    }

    @Test
    public void helpButton_oneClick_opensReferenceAndShowsFeedback() throws Exception {
        runAndWait(() -> {
            helpButton.fire();
            assertHelpVisible();
            assertTrue(mainWindow.getRoot().isShowing());
        });
    }

    @Test
    public void typedHelp_filteredContacts_preservesDataAndFilter() throws Exception {
        runAndWait(() -> {
            showPersonAtIndex(model, INDEX_FIRST_PERSON);
            AddressBook original = new AddressBook(model.getAddressBook());
            List<?> displayed = List.copyOf(model.getFilteredPersonList());
            enterCommand("help");
            assertHelpVisible();
            assertEquals("", commandField.getText());
            assertEquals(original, model.getAddressBook());
            assertEquals(original.getGroupList(), model.getAddressBook().getGroupList());
            assertEquals(displayed, model.getFilteredPersonList());
        });
    }

    @Test
    public void f1_commandField_opensReferenceWithoutClearingDraft() throws Exception {
        runAndWait(() -> {
            commandField.setText("add n/Unfinished draft");
            commandField.requestFocus();
            pressF1(commandField);
            assertHelpVisible();
            assertEquals("add n/Unfinished draft", commandField.getText());
        });
    }

    @Test
    public void f1_resultDisplay_opensReference() throws Exception {
        runAndWait(() -> {
            feedback.requestFocus();
            pressF1(feedback);
            assertHelpVisible();
        });
    }

    @Test
    public void repeatedHelp_allEntryPoints_reuseAndRestoreSameWindow() throws Exception {
        runAndWait(() -> {
            helpButton.fire();
            Stage helpStage = assertHelpVisible();
            feedback.setText("Other feedback");
            enterCommand("help");
            assertSame(helpStage, assertHelpVisible());
            helpStage.setIconified(true);
            feedback.setText("Other feedback");
            pressF1(commandField);
            assertSame(helpStage, assertHelpVisible());
            assertFalse(helpStage.isIconified());
            feedback.setText("Other feedback");
            helpButton.fire();
            assertSame(helpStage, assertHelpVisible());
        });
    }

    @Test
    public void helpClosed_f1_reopensSameWindow() throws Exception {
        runAndWait(() -> {
            helpButton.fire();
            Stage helpStage = assertHelpVisible();
            Button close = (Button) helpStage.getScene().lookup(".help-close-button");
            close.fire();
            assertTrue(helpStages().isEmpty());
            pressF1(commandField);
            assertSame(helpStage, assertHelpVisible());
        });
    }

    @Test
    public void unrelatedKeysAndModifiedF1_doNotOpenHelp() throws Exception {
        runAndWait(() -> {
            feedback.setText("Previous feedback");
            for (KeyEvent event : List.of(
                    new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F2, false, false, false, false),
                    new KeyEvent(KeyEvent.KEY_RELEASED, "", "", KeyCode.F1, false, false, false, false),
                    new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1, true, false, false, false),
                    new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1, false, true, false, false),
                    new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1, false, false, true, false),
                    new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1, false, false, false, true))) {
                Event.fireEvent(commandField, event);
                assertTrue(helpStages().isEmpty());
                assertEquals("Previous feedback", feedback.getText());
            }
        });
    }

    @Test
    public void helpOpen_listCommand_stillWorks() throws Exception {
        runAndWait(() -> {
            showPersonAtIndex(model, INDEX_FIRST_PERSON);
            helpButton.fire();
            Stage helpStage = assertHelpVisible();
            assertEquals(Modality.NONE, helpStage.getModality());
            enterCommand("list");
            assertEquals(ListCommand.MESSAGE_SUCCESS, feedback.getText());
            assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
            assertTrue(helpStage.isShowing());
            assertTrue(mainWindow.getRoot().isShowing());
        });
    }

    @Test
    public void helpOpen_invalidCommands_showErrorsAndKeepReferenceOpen() throws Exception {
        runAndWait(() -> {
            helpButton.fire();
            Stage helpStage = assertHelpVisible();
            AddressBook original = new AddressBook(model.getAddressBook());
            enterCommand("unknownCommand");
            assertEquals(Messages.MESSAGE_UNKNOWN_COMMAND, feedback.getText());
            assertTrue(commandField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            enterCommand("delete 999");
            assertEquals(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, feedback.getText());
            assertTrue(helpStage.isShowing());
            assertEquals(original, model.getAddressBook());
        });
    }

    @Test
    public void exit_helpOpen_closesBothWindows() throws Exception {
        runAndWait(() -> {
            helpButton.fire();
            Stage helpStage = assertHelpVisible();
            enterCommand("exit");
            assertFalse(helpStage.isShowing());
            assertFalse(mainWindow.getRoot().isShowing());
        });
    }

    @Test
    public void fileExit_helpOpen_closesBothWindows() throws Exception {
        runAndWait(() -> {
            helpButton.fire();
            Stage helpStage = assertHelpVisible();
            MenuBar menuBar = lookup("#menuBar", MenuBar.class);
            menuBar.getMenus().getFirst().getItems().getFirst().fire();
            assertFalse(helpStage.isShowing());
            assertFalse(mainWindow.getRoot().isShowing());
        });
    }

    @Test
    public void mainWindowClose_helpOpen_closesBothWindows() throws Exception {
        runAndWait(() -> {
            helpButton.fire();
            Stage helpStage = assertHelpVisible();
            Stage mainStage = mainWindow.getRoot();
            Event.fireEvent(mainStage, new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            assertFalse(helpStage.isShowing());
            assertFalse(mainStage.isShowing());
        });
    }

    @Test
    public void helpButton_focusAndArmedStyles_highlightOnlyOnHover() throws Exception {
        runAndWait(() -> {
            PseudoClass hover = PseudoClass.getPseudoClass("hover");
            helpButton.pseudoClassStateChanged(hover, false);
            helpButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
            helpButton.arm();
            assertEquals(0, helpButtonBackground().getOpacity());
            helpButton.pseudoClassStateChanged(hover, true);
            assertTrue(helpButtonBackground().getOpacity() > 0);
            helpButton.pseudoClassStateChanged(hover, false);
            assertEquals(0, helpButtonBackground().getOpacity());
            helpButton.disarm();
        });
    }

    private <T extends Node> T lookup(String selector, Class<T> type) {
        Node node = mainWindow.getRoot().getScene().lookup(selector);
        assertNotNull(node, selector);
        return type.cast(node);
    }

    private void enterCommand(String text) {
        commandField.setText(text);
        commandField.fireEvent(new ActionEvent());
    }

    private void pressF1(Node target) {
        Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                false, false, false, false));
    }

    private Stage assertHelpVisible() {
        List<Stage> stages = helpStages();
        assertEquals(1, stages.size());
        assertEquals(HelpCommand.SHOWING_HELP_MESSAGE, feedback.getText());
        return stages.getFirst();
    }

    private List<Stage> helpStages() {
        return Window.getWindows().stream()
                .filter(window -> window.getScene().getRoot().getStyleClass().contains("help-root"))
                .map(window -> (Stage) window).toList();
    }

    private Color helpButtonBackground() {
        mainWindow.getRoot().getScene().getRoot().applyCss();
        return (Color) helpButton.getBackground().getFills().getFirst().getFill();
    }
}
