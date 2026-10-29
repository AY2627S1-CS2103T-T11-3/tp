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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.LinkGroupCommand;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Role;
import seedu.address.testutil.FxTestUtil;

/**
 * Integration tests requiring the offline Help window implementation in the tested revision.
 */
public class HelpWindowTest {
    private static final Map<String, String> EDIT_FIELDS = Map.of(
            "[n/NAME]", "n/NAME", "[p/PHONE]", "p/PHONE", "[e/EMAIL]", "e/EMAIL",
            "[a/ADDRESS]", "a/ADDRESS", "[t/TAG]...", "t/TAG");
    private static final Map<String, String> PLACEHOLDER_VALUES = Map.ofEntries(
            Map.entry("NAME", "Help Example"), Map.entry("PHONE", "12345678"),
            Map.entry("EMAIL", "help@example.com"), Map.entry("ADDRESS", "12 Help Street"),
            Map.entry("INDEX", "1"), Map.entry("TAG", "Friends"),
            Map.entry("CONTACT_TYPE", "student"), Map.entry("DEPARTMENT", "Science"),
            Map.entry("GROUP_NAME", "Group"), Map.entry("YEAR_OF_STUDY", "2"),
            Map.entry("GROUP_TYPE", "class"), Map.entry("STUDENT_INDEX", "1"),
            Map.entry("GUARDIAN_INDEX", "2"), Map.entry("PERSON_INDEX", "1"),
            Map.entry("GROUP_INDEX", "1"), Map.entry("PERSON_ID", "1"), Map.entry("GROUP_ID", "3"));
    private static final Pattern OPTIONAL_FIELD = Pattern.compile("\\[[^\\]]+](?:\\.\\.\\.)?");
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
    public void reference_formatsAndExamples_areSelectableReadOnly() throws Exception {
        runAndWait(() -> {
            Parent root = helpWindow.getRoot().getScene().getRoot();
            documentedFormats(); // Also checks command coverage and duplicate entries.
            for (Node node : root.lookupAll(".text-area")) {
                TextArea syntax = (TextArea) node;
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
    public void reference_addFormat_acceptsNameOnlyAndEachOptionalField() throws Exception {
        runAndWait(() -> {
            String format = documentedFormats().get(AddCommand.COMMAND_WORD).getText();
            assertAddFormat(format, Map.of(), Role.STUDENT);
            Map<String, String> optionalFields = Map.of(
                    "[p/PHONE]", "p/PHONE", "[e/EMAIL]", "e/EMAIL", "[a/ADDRESS]", "a/ADDRESS",
                    "[yos/YEAR_OF_STUDY]", "yos/YEAR_OF_STUDY", "[t/TAG]...", "t/TAG");
            for (Map.Entry<String, String> field : optionalFields.entrySet()) {
                assertAddFormat(format, Map.of(field.getKey(), field.getValue()), Role.STUDENT);
            }
        });
    }

    @Test
    public void reference_addFormat_preservesExplicitRolesWithOptionalDetails() throws Exception {
        runAndWait(() -> {
            String format = documentedFormats().get(AddCommand.COMMAND_WORD).getText();
            for (Role role : Role.values()) {
                Map<String, String> fields = new HashMap<>();
                fields.put("[ct/CONTACT_TYPE]", "ct/" + role.name().toLowerCase(Locale.ROOT));
                assertAddFormat(format, fields, role);
                fields.put("[p/PHONE]", "p/PHONE");
                fields.put("[e/EMAIL]", "e/EMAIL");
                fields.put("[a/ADDRESS]", "a/ADDRESS");
                fields.put("[t/TAG]...", "t/TAG t/School");
                if (role == Role.STUDENT) {
                    fields.put("[yos/YEAR_OF_STUDY]", "yos/YEAR_OF_STUDY");
                }
                assertAddFormat(format, fields, role);
            }
        });
    }

    /**
     * Verifies the displayed filter format supports each numeric criterion and combined repeatable fields.
     *
     * @throws Exception If the JavaFX test cannot run.
     */
    @Test
    public void reference_filterFormat_acceptsNumericFieldsAndCombinations() throws Exception {
        runAndWait(() -> {
            String format = documentedFormats().get(FilterCommand.COMMAND_WORD).getText();
            Map<String, String> numericFields = Map.of(
                    "[id/PERSON_ID]...", "id/PERSON_ID", "[gid/GROUP_ID]", "gid/GROUP_ID",
                    "[yos/YEAR_OF_STUDY]", "yos/YEAR_OF_STUDY");
            for (Map.Entry<String, String> field : numericFields.entrySet()) {
                assertTrue(format.contains(field.getKey()), "Missing filter field: " + field.getKey());
                assertFormatAcceptedByParser(format, Map.of(field.getKey(), field.getValue()));
            }
            Map<String, String> combinedFields = new HashMap<>(numericFields);
            combinedFields.put("[id/PERSON_ID]...", "id/PERSON_ID id/2 id/0001");
            combinedFields.put("[a/ADDRESS]", "a/ADDRESS");
            combinedFields.put("[t/TAG]...", "t/TAG t/School");
            assertFormatAcceptedByParser(format, combinedFields);
        });
    }

    /**
     * Verifies visible descriptions explain numeric matching, combinations, and ID versus index semantics.
     *
     * @throws Exception If the JavaFX test cannot run.
     */
    @Test
    public void reference_filterNotes_describeMatchingAndIndexRules() throws Exception {
        runAndWait(() -> {
            String reference = documentedEntryText(FilterCommand.COMMAND_WORD);
            for (String explanation : List.of("id/PERSON_ID", "gid/GROUP_ID", "yos/YEAR_OF_STUDY",
                    "Exact numeric Person ID, not a displayed contact-list index",
                    "Exact numeric Group ID, not its position in the Groups panel",
                    "only students with a recorded year match", "numeric duplicates are ignored",
                    "Different criteria combine using AND", "Repeated id/ values are alternatives (OR)",
                    "repeated t/ values must all match (AND)", "Only id/ and t/ may repeat",
                    "including gid/ and yos/, may appear at most once", "positive integers from 1 to 2147483647",
                    "leading zeros do not change their value", "Omit all fields to show all contacts",
                    "replacing the previous filter", "filter gid/ selects an actual numeric Group ID",
                    "link gid/ selects a displayed group-list index", "Group IDs may change after restarting")) {
                assertTrue(reference.contains(explanation), "Missing filter explanation: " + explanation);
            }
            assertFalse(reference.contains("Use each prefix at most once except t/."));
            assertTrue(documentedEntryText(LinkGroupCommand.COMMAND_WORD).contains("not a permanent group ID"));
        });
    }

    @Test
    public void reference_displayedExamples_areAcceptedByParser() throws Exception {
        runAndWait(() -> {
            Parent root = helpWindow.getRoot().getScene().getRoot();
            assertFalse(root.lookupAll(".help-example").isEmpty());
            for (Node entry : root.lookupAll(".command-entry")) {
                Parent card = (Parent) entry;
                String command = ((Label) card.lookup(".help-command-name")).getText();
                for (Node node : card.lookupAll(".help-example")) {
                    String example = ((TextArea) node).getText();
                    assertEquals(command, example.strip().split("\\s+", 2)[0]);
                    assertDoesNotThrow(() -> new AddressBookParser().parseCommand(example), example);
                }
            }
        });
    }

    @Test
    public void reference_editFormat_acceptsOptionalFieldsAndRepeatedTags() throws Exception {
        runAndWait(() -> {
            String format = documentedFormats().get(EditCommand.COMMAND_WORD).getText();
            for (Map.Entry<String, String> field : EDIT_FIELDS.entrySet()) {
                assertFormatAcceptedByParser(format, Map.of(field.getKey(), field.getValue()));
            }
            Map<String, String> allFields = new HashMap<>(EDIT_FIELDS);
            allFields.put("[t/TAG]...", "t/TAG t/School");
            assertFormatAcceptedByParser(format, allFields);
            assertFormatAcceptedByParser(format, Map.of("[t/TAG]...", "t/"));
        });
    }

    @Test
    public void reference_remainingFormats_areAcceptedByParser() throws Exception {
        runAndWait(() -> {
            Map<String, TextArea> formats = documentedFormats();
            for (String command : ALL_COMMAND_NAMES) {
                if (!command.equals(AddCommand.COMMAND_WORD) && !command.equals(EditCommand.COMMAND_WORD)) {
                    assertFormatAcceptedByParser(formats.get(command).getText(), Map.of());
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
        waitUntil(stage::isIconified, "the Help window to be minimized");
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
    public void layout_resizingAndLargerText_keepsReferenceAndWrappedTextVisible() throws Exception {
        Stage stage = helpWindow.getRoot();
        runAndWait(helpWindow::show);
        for (double width : List.of(stage.getMinWidth(), 1200.0, stage.getMinWidth())) {
            runAndWait(() -> {
                stage.setWidth(width);
                stage.setHeight(stage.getMinHeight());
            });
            waitUntil(() -> Math.abs(stage.getScene().getWidth() - width) < 40,
                    "the Help scene to resize");
            waitUntil(this::wrappedTextFitsViewport, "the wrapped Help fields to finish resizing");
            runAndWait(this::assertReferenceFitsViewport);
        }
        runAndWait(() -> {
            Parent root = stage.getScene().getRoot();
            root.lookupAll(".text-area").forEach(node -> node.setStyle("-fx-font-size: 20px;"));
        });
        waitUntil(this::wrappedTextFitsViewport, "the larger Help text to finish resizing");
        runAndWait(this::assertReferenceFitsViewport);
    }

    /** Checks rendered text bounds while JavaFX settles width-dependent wrapping and field heights. */
    private boolean wrappedTextFitsViewport() {
        Parent root = helpWindow.getRoot().getScene().getRoot();
        root.applyCss();
        root.layout();
        return root.lookupAll(".text-area").stream().map(TextArea.class::cast)
                .allMatch(area -> {
                    ScrollPane inner = (ScrollPane) area.lookup(".scroll-pane");
                    if (inner == null) {
                        return false;
                    }
                    Bounds content = inner.getContent().getLayoutBounds();
                    Bounds viewport = inner.getViewportBounds();
                    return viewport.getWidth() > 0 && viewport.getHeight() > 0
                            && content.getWidth() <= viewport.getWidth() + 1
                            && content.getHeight() <= viewport.getHeight() + 1;
                });
    }

    private void assertReferenceFitsViewport() {
        Parent root = helpWindow.getRoot().getScene().getRoot();
        root.applyCss();
        root.layout();
        ScrollPane scroll = (ScrollPane) root.lookup("#commandScrollPane");
        assertTrue(scroll.isFitToWidth());
        assertEquals(ScrollPane.ScrollBarPolicy.NEVER, scroll.getHbarPolicy());
        assertEquals(ScrollPane.ScrollBarPolicy.AS_NEEDED, scroll.getVbarPolicy());
        assertTrue(scroll.getContent().getLayoutBounds().getHeight() > scroll.getViewportBounds().getHeight());
        assertTrue(scroll.getContent().getLayoutBounds().getWidth() <= scroll.getViewportBounds().getWidth() + 1);
        for (Node entry : root.lookupAll(".command-entry")) {
            Bounds entryBounds = entry.localToScene(entry.getLayoutBounds());
            Set<Node> controls = new HashSet<>(((Parent) entry).lookupAll(".label"));
            controls.addAll(((Parent) entry).lookupAll(".text-area"));
            for (Node child : controls) {
                Bounds bounds = child.localToScene(child.getLayoutBounds());
                assertTrue(bounds.getMinX() >= entryBounds.getMinX() - 1);
                assertTrue(bounds.getMaxX() <= entryBounds.getMaxX() + 1);
                assertTrue(bounds.getMinY() >= entryBounds.getMinY() - 1);
                assertTrue(bounds.getMaxY() <= entryBounds.getMaxY() + 1);
            }
        }
        for (Node node : root.lookupAll(".text-area")) {
            TextArea area = (TextArea) node;
            ScrollPane inner = (ScrollPane) area.lookup(".scroll-pane");
            assertNotNull(inner);
            Bounds content = inner.getContent().getLayoutBounds();
            Bounds viewport = inner.getViewportBounds();
            assertTrue(viewport.getWidth() > 0 && viewport.getHeight() > 0, area.getText());
            assertTrue(content.getWidth() <= viewport.getWidth() + 1, "Hidden horizontal text: " + area.getText());
            assertTrue(content.getHeight() <= viewport.getHeight() + 1, "Hidden vertical text: " + area.getText());
        }
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
        assertEquals(Set.copyOf(ALL_COMMAND_NAMES), formats.keySet());
        return formats;
    }

    /** Returns the visible labels for one command's Help entry, excluding its selectable syntax and examples. */
    private String documentedEntryText(String command) {
        Parent root = helpWindow.getRoot().getScene().getRoot();
        Parent entry = root.lookupAll(".command-entry").stream().map(Parent.class::cast)
                .filter(card -> ((Label) card.lookup(".help-command-name")).getText().equals(command))
                .findFirst().orElseThrow();
        return entry.lookupAll(".label").stream().map(node -> ((Label) node).getText())
                .collect(Collectors.joining(" "));
    }

    private void assertAddFormat(String format, Map<String, String> fields, Role expectedRole) {
        Command command = assertFormatAcceptedByParser(format, fields);
        Model model = new ModelManager();
        assertDoesNotThrow(() -> command.execute(model));
        assertEquals(expectedRole, model.getAddressBook().getPersonList().getFirst().getRole());
    }

    private Command assertFormatAcceptedByParser(String format, Map<String, String> optionalFields) {
        for (String field : optionalFields.keySet()) {
            assertTrue(format.contains(field), "Missing optional field in Help format: " + field);
        }
        String example = OPTIONAL_FIELD.matcher(format).replaceAll(match ->
                Matcher.quoteReplacement(optionalFields.getOrDefault(match.group(), "")));
        example = UNRESOLVED_PLACEHOLDER.matcher(example).replaceAll(match -> {
            String value = PLACEHOLDER_VALUES.get(match.group());
            assertNotNull(value, "Unknown Help placeholder: " + match.group());
            return Matcher.quoteReplacement(value);
        });
        assertFalse(example.contains("[") || example.contains("]") || example.contains("..."), example);
        assertFalse(UNRESOLVED_PLACEHOLDER.matcher(example).find(), example);
        String command = example;
        return assertDoesNotThrow(() -> new AddressBookParser().parseCommand(command), command);
    }
}
