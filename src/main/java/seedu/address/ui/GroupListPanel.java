package seedu.address.ui;

import java.util.Locale;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupType;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Person;
import seedu.address.model.person.Student;

/** Displays indexed groups and the contacts currently assigned to them. */
public class GroupListPanel extends UiPart<Region> {
    private static final String FXML = "GroupListPanel.fxml";
    private final ObservableList<Person> persons;

    @FXML
    private ListView<Group> groupListView;

    /** Creates a group panel backed by the given groups and contacts. */
    public GroupListPanel(ObservableList<Group> groups, ObservableList<Person> persons) {
        super(FXML);
        this.persons = persons;
        groupListView.setItems(groups);
        groupListView.setCellFactory(listView -> new GroupListViewCell());
    }

    /** Refreshes member names after a link command. */
    public void refresh() {
        groupListView.refresh();
    }

    class GroupListViewCell extends ListCell<Group> {
        @Override
        protected void updateItem(Group group, boolean empty) {
            super.updateItem(group, empty);
            if (empty || group == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            String groupName = group.getGroupName().groupName;
            String type = group.getGroupType().name().toLowerCase(Locale.ROOT);
            String members = persons.stream()
                    .filter(person -> groupName.equals(person.getGroupId())
                            && belongsToGroupType(person, group.getGroupType()))
                    .map(person -> person.getName().fullName)
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .reduce((left, right) -> left + ", " + right)
                    .orElse("No members");

            Label index = new Label((getIndex() + 1) + ".");
            index.getStyleClass().add("group_list_index");
            Label name = new Label(groupName);
            name.getStyleClass().add("group_list_name");
            Label details = new Label(type + "  •  " + members);
            details.getStyleClass().add("group_list_details");
            HBox heading = new HBox(8, index, name);
            heading.setAlignment(Pos.CENTER_LEFT);
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(2, heading, details);
            content.getStyleClass().add("group_list_card");
            setGraphic(content);
            setText(null);
        }

        private boolean belongsToGroupType(Person person, GroupType groupType) {
            return (groupType == GroupType.CLASS && person instanceof Student)
                    || (groupType == GroupType.DEPARTMENT && person instanceof Colleague);
        }
    }
}
