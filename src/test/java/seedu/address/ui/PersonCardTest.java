package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class PersonCardTest {
    @BeforeAll
    public static void initializeJavaFx() {
        Platform.startup(() -> Platform.setImplicitExit(false));
    }

    @Test
    public void constructor_guardians_showsNamesAndHidesEmptyLabel() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            PersonCard linkedCard = new PersonCard(ALICE, 1, "Mary Tan, John Tan");
            Label guardians = (Label) linkedCard.getRoot().lookup("#guardians");
            assertEquals("Guardians: Mary Tan, John Tan", guardians.getText());
            assertTrue(guardians.isVisible());
            assertTrue(guardians.isManaged());

            PersonCard unlinkedCard = new PersonCard(ALICE, 1);
            Label emptyGuardians = (Label) unlinkedCard.getRoot().lookup("#guardians");
            assertFalse(emptyGuardians.isVisible());
            assertFalse(emptyGuardians.isManaged());
            return null;
        });
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
