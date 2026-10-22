package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.FxTestUtil.runAndWait;
import static seedu.address.testutil.FxTestUtil.waitUntil;
import static seedu.address.testutil.TypicalCommands.ALL_COMMAND_NAMES;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.testutil.FxTestUtil;

/**
 * Integration tests requiring the offline Help window implementation in the tested revision.
 */
public class HelpWindowTest {
    private static final Map<String, String> OPTIONAL_FIELDS = Map.of(
            "[n/NAME]", "n/NAME", "[p/PHONE]", "p/PHONE", "[e/EMAIL]", "e/EMAIL",
            "[a/ADDRESS]", "a/ADDRESS", "[t/TAG]...", "t/TAG");
    private static final Map<String, String> PLACEHOLDER_VALUES = Map.of(
            "NAME", "Help Example", "PHONE", "12345678", "EMAIL", "help@example.com",
            "ADDRESS", "12 Help Street", "INDEX", "1", "TAG", "Friends");
    private static final Pattern UNRESOLVED_PLACEHOLDER = Pattern.compile("\\b[A-Z][A-Z_]*\\b");

    private HelpWindow helpWindow;

    @BeforeAll
    public static void initializeToolkit() throws Exception {
        FxTestUtil.initialize();
    }

    @BeforeEach
    public void setUp() throws Exception {
        runAndWait(() -> {
            helpWindow = new HelpWindow();
            Parent root = helpWindow.getRoot().getScene().getRoot();
            root.applyCss();
            root.layout();
        });
    }

    @AfterEach
    public void tearDown() throws Exception {
        runAndWait(() -> new ArrayList<>(Window.getWindows()).forEach(Window::hide));
    }

    @Test
    public void constructor_suppliedStage_loadsResizableNonModalReference() throws Exception {
        runAndWait(() -> {
            Stage stage = new Stage();
            helpWindow = new HelpWindow(stage);
            assertSame(stage, helpWindow.getRoot());
            assertEquals(Modality.NONE, stage.getModality());
            assertTrue(stage.isResizable());
            assertTrue(stage.getMinWidth() > 0);
            assertTrue(stage.getMinHeight() > 0);
            assertFalse(helpWindow.isShowing());
            assertFalse(stage.getScene().getStylesheets().isEmpty());
            assertTrue(stage.getScene().getStylesheets().stream()
                    .noneMatch(url -> url.startsWith("http:") || url.startsWith("https:")));
        });
    }

    @Test
    public void reference_documentedCommands_haveSelectableReadOnlyFormats() throws Exception {
        runAndWait(() -> {
            Parent root = helpWindow.getRoot().getScene().getRoot();
            for (TextArea syntax : documentedFormats().values()) {
                assertFalse(syntax.getText().isBlank());
                assertFalse(syntax.isEditable());
                assertTrue(syntax.isWrapText());
                syntax.selectAll();
                assertEquals(syntax.getText(), syntax.getSelectedText());
            }
            assertTrue(root.lookupAll(".help-note").stream()
                    .anyMatch(node -> ((Label) node).getText().equals("Shortcut: F1")));
            assertTrue(root.lookupAll(".hyperlink").isEmpty());
        });
    }

    @Test
    public void reference_addFormat_acceptsAllRolesAndOptionalTags() throws Exception {
        runAndWait(() -> {
            String format = documentedFormats().get(AddCommand.COMMAND_WORD).getText();
            for (String role : List.of("student", "guardian", "colleague")) {
                assertFormatAcceptedByParser(format, role, Map.of());
                assertFormatAcceptedByParser(format, role, Map.of("[t/TAG]...", "t/TAG"));
                assertFormatAcceptedByParser(format, role, Map.of("[t/TAG]...", "t/TAG t/School"));
            }
        });
    }

    @Test
    public void reference_editFormat_acceptsOptionalFieldsAndRepeatedTags() throws Exception {
        runAndWait(() -> {
            String format = documentedFormats().get(EditCommand.COMMAND_WORD).getText();
            for (Map.Entry<String, String> field : OPTIONAL_FIELDS.entrySet()) {
                assertFormatAcceptedByParser(format, "student", Map.of(field.getKey(), field.getValue()));
            }
            Map<String, String> allFields = new HashMap<>(OPTIONAL_FIELDS);
            allFields.put("[t/TAG]...", "t/TAG t/School");
            assertFormatAcceptedByParser(format, "student", allFields);
            assertFormatAcceptedByParser(format, "student", Map.of("[t/TAG]...", "t/"));
        });
    }

    @Test
    public void reference_remainingFormats_areAcceptedByParser() throws Exception {
        runAndWait(() -> {
            Map<String, TextArea> formats = documentedFormats();
            for (String command : ALL_COMMAND_NAMES) {
                if (!command.equals(AddCommand.COMMAND_WORD) && !command.equals(EditCommand.COMMAND_WORD)) {
                    assertFormatAcceptedByParser(formats.get(command).getText(), "student", Map.of());
                }
            }
        });
    }

    @Test
    public void show_hideAndReopen_reusesStageAndContent() throws Exception {
        runAndWait(() -> {
            Stage stage = helpWindow.getRoot();
            Scene scene = stage.getScene();
            helpWindow.show();
            assertTrue(helpWindow.isShowing());
            helpWindow.hide();
            assertFalse(helpWindow.isShowing());
            helpWindow.hide(); // Hiding an already hidden reference is harmless.
            helpWindow.show();
            assertTrue(helpWindow.isShowing());
            assertSame(stage, helpWindow.getRoot());
            assertSame(scene, stage.getScene());
        });
    }

    @Test
    public void focus_minimizedWindow_restoresExistingStage() throws Exception {
        Stage stage = helpWindow.getRoot();
        runAndWait(helpWindow::show);
        waitUntil(stage::isShowing, "the Help window to be shown");
        runAndWait(() -> stage.setIconified(true));
        waitUntil(stage::isIconified, "the Help window to be minimised");
        runAndWait(helpWindow::focus);
        waitUntil(() -> !stage.isIconified(), "the Help window to be restored");
        runAndWait(() -> {
            assertTrue(helpWindow.isShowing());
            assertFalse(stage.isIconified());
            long helpWindows = Window.getWindows().stream()
                    .filter(window -> window.getScene() != null)
                    .filter(window -> window.getScene().getRoot().getStyleClass().contains("help-root"))
                    .count();
            assertEquals(1, helpWindows);
        });
    }

    @Test
    public void closeButton_hidesReference() throws Exception {
        runAndWait(() -> {
            helpWindow.show();
            Button close = (Button) helpWindow.getRoot().getScene().lookup(".help-close-button");
            assertNotNull(close);
            close.fire();
            assertFalse(helpWindow.isShowing());
        });
    }

    @Test
    public void escape_fromSyntaxField_hidesReference() throws Exception {
        runAndWait(() -> {
            helpWindow.show();
            TextArea syntax = (TextArea) helpWindow.getRoot().getScene().lookup(".help-syntax");
            syntax.requestFocus();
            Event.fireEvent(syntax, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE,
                    false, false, false, false));
            assertFalse(helpWindow.isShowing());
        });
    }

    @Test
    public void layout_minimumSize_keepsReferenceScrollableWithoutHorizontalOverflow() throws Exception {
        runAndWait(() -> {
            Stage stage = helpWindow.getRoot();
            stage.setWidth(stage.getMinWidth());
            stage.setHeight(stage.getMinHeight());
            helpWindow.show();
            Parent root = stage.getScene().getRoot();
            root.applyCss();
            root.layout();
            ScrollPane scroll = (ScrollPane) root.lookup("#commandScrollPane");
            assertTrue(scroll.isFitToWidth());
            assertEquals(ScrollPane.ScrollBarPolicy.NEVER, scroll.getHbarPolicy());
            assertEquals(ScrollPane.ScrollBarPolicy.AS_NEEDED, scroll.getVbarPolicy());
            assertTrue(scroll.getContent().getLayoutBounds().getHeight() > scroll.getViewportBounds().getHeight());
            assertTrue(scroll.getContent().getLayoutBounds().getWidth() <= scroll.getViewportBounds().getWidth() + 1);
            assertEquals(ALL_COMMAND_NAMES.size(), root.lookupAll(".command-entry").size());
            for (Node entry : root.lookupAll(".command-entry")) {
                assertTrue(entry.getBoundsInParent().getMaxX() <= scroll.getContent().getLayoutBounds().getWidth());
                Bounds entryBounds = entry.localToScene(entry.getLayoutBounds());
                for (Node label : ((Parent) entry).lookupAll(".label")) {
                    Bounds labelBounds = label.localToScene(label.getLayoutBounds());
                    assertTrue(labelBounds.getMinX() >= entryBounds.getMinX() - 1);
                    assertTrue(labelBounds.getMaxX() <= entryBounds.getMaxX() + 1);
                }
            }
        });
    }

    private Map<String, TextArea> documentedFormats() {
        Parent root = helpWindow.getRoot().getScene().getRoot();
        Map<String, TextArea> formats = new HashMap<>();
        for (Node node : root.lookupAll(".command-entry")) {
            Parent entry = (Parent) node;
            assertEquals(1, entry.lookupAll(".help-command-name").size(), "Each entry needs one command name");
            assertEquals(1, entry.lookupAll(".help-syntax").size(), "Each entry needs one format");
            String command = ((Label) entry.lookup(".help-command-name")).getText();
            TextArea syntax = (TextArea) entry.lookup(".help-syntax");
            assertFalse(syntax.getText().isBlank(), command);
            assertEquals(command, syntax.getText().strip().split("\\s+", 2)[0]);
            assertNull(formats.put(command, syntax), "Duplicate Help entry: " + command);
        }
        assertEquals(ALL_COMMAND_NAMES.size(), formats.size());
        assertTrue(formats.keySet().containsAll(ALL_COMMAND_NAMES));
        return formats;
    }

    private void assertFormatAcceptedByParser(String format, String role, Map<String, String> optionalFields) {
        for (String field : optionalFields.keySet()) {
            assertTrue(format.contains(field), "Missing optional field in Help format: " + field);
        }
        String example = format;
        for (String field : OPTIONAL_FIELDS.keySet()) {
            example = example.replace(field, optionalFields.getOrDefault(field, ""));
        }
        example = example.replace("CONTACT_TYPE", role);
        for (Map.Entry<String, String> placeholder : PLACEHOLDER_VALUES.entrySet()) {
            example = example.replace(placeholder.getKey(), placeholder.getValue());
        }
        assertFalse(example.contains("[") || example.contains("]") || example.contains("..."), example);
        assertFalse(UNRESOLVED_PLACEHOLDER.matcher(example).find(), example);
        String command = example;
        assertDoesNotThrow(() -> new AddressBookParser().parseCommand(command), command);
    }
}
