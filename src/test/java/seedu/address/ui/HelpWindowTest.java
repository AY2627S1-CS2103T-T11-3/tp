package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.FxTestUtil.runAndWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.testutil.FxTestUtil;

public class HelpWindowTest {
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
            List<String> names = root.lookupAll(".help-command-name").stream()
                    .map(node -> ((Label) node).getText()).toList();
            assertTrue(names.containsAll(List.of("add", "edit", "delete", "list", "clear", "help", "exit")));
            for (Node node : root.lookupAll(".help-syntax")) {
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
    public void reference_documentedFormats_areAcceptedByParser() throws Exception {
        runAndWait(() -> {
            AddressBookParser parser = new AddressBookParser();
            Map<String, String> values = Map.of("NAME", "Help Example", "PHONE", "12345678",
                    "EMAIL", "help@example.com", "ADDRESS", "12 Help Street", "INDEX", "1", "TAG", "Friends");
            var formats = helpWindow.getRoot().getScene().getRoot().lookupAll(".help-syntax");
            assertFalse(formats.isEmpty());
            for (Node node : formats) {
                String format = ((TextArea) node).getText();
                List<String> roles = format.startsWith("add ")
                        ? List.of("student", "guardian", "colleague") : List.of("student");
                for (String role : roles) {
                    String example = format.replaceAll("\\[([^\\]]+)\\]", "$1").replace("...", "")
                            .replace("CONTACT_TYPE", role);
                    for (Map.Entry<String, String> value : values.entrySet()) {
                        example = example.replace(value.getKey(), value.getValue());
                    }
                    String command = example;
                    assertDoesNotThrow(() -> parser.parseCommand(command), command);
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
        runAndWait(() -> {
            helpWindow.show();
            Stage stage = helpWindow.getRoot();
            stage.setIconified(true);
            helpWindow.focus();
            assertTrue(helpWindow.isShowing());
            assertFalse(stage.isIconified());
            assertEquals(1, Window.getWindows().size());
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
}
