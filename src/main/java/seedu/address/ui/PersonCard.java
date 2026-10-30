package seedu.address.ui;

import java.util.Comparator;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.model.person.Student;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label index;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label role;
    @FXML
    private Label yearOfStudy;
    @FXML
    private Button guardians;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        this(person, displayedIndex, "", "");
    }

    /**
     * Creates a contact card with the names of linked guardians.
     */
    public PersonCard(Person person, int displayedIndex, String guardianNames) {
        this(person, displayedIndex, guardianNames, "");
    }

    /** Creates a contact card with both linked guardians and linked students. */
    public PersonCard(Person person, int displayedIndex, String guardianNames, String studentNames) {
        super(FXML);
        this.person = person;
        index.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);

        if (person.getPhone() != null) {
            phone.setText("☎  " + person.getPhone().value);
        } else {
            phone.setText("☎  Not added");
        }

        if (person.getAddress() != null) {
            address.setText("📍  " + person.getAddress().value);
        } else {
            address.setText("📍  Not added");
        }

        if (person.getEmail() != null) {
            email.setText("✉  " + person.getEmail().value);
        } else {
            email.setText("✉  Not added");
        }

        String roleName = person.getRole().name();
        role.setText(roleName.charAt(0) + roleName.substring(1).toLowerCase(Locale.ROOT));
        role.getStyleClass().add("role-" + roleName.toLowerCase(Locale.ROOT));
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        boolean isGuardian = !studentNames.isEmpty();
        String relationshipNames = isGuardian ? studentNames : guardianNames;
        String relationshipLabel = isGuardian ? "View students" : "View guardians";
        guardians.setText(relationshipLabel);
        guardians.setTooltip(new Tooltip("Click to " + relationshipLabel.toLowerCase(Locale.ROOT)));
        guardians.setVisible(!relationshipNames.isEmpty());
        guardians.setManaged(!relationshipNames.isEmpty());
        if (!relationshipNames.isEmpty()) {
            guardians.setOnAction(event -> showRelationshipPopup(
                    isGuardian ? "Students" : "Guardians", relationshipNames));
        }

        String yearOfStudyString = "";
        if (person instanceof Student student && student.getYearOfStudy() != null) {
            yearOfStudyString = "Year of study: " + student.getYearOfStudy().toString();
        }

        yearOfStudy.setText(yearOfStudyString);
        yearOfStudy.setVisible(!yearOfStudyString.isEmpty());
        yearOfStudy.setManaged(!yearOfStudyString.isEmpty());
    }

    /** Shows linked contacts without adding extra lines to the contact card. */
    private void showRelationshipPopup(String titleText, String relationshipNames) {
        Label title = new Label(titleText);
        title.getStyleClass().add("popup-title");
        Label names = new Label(relationshipNames);
        names.setWrapText(true);
        names.getStyleClass().add("popup-content");

        VBox content = new VBox(6, title, names);
        content.getStyleClass().add("guardian-popup");

        CustomMenuItem popupItem = new CustomMenuItem(content);
        ContextMenu popup = new ContextMenu(popupItem);
        popup.show(guardians, Side.BOTTOM, 0, 0);
    }
}
