package seedu.address.testutil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * Runs UI assertions on the JavaFX thread with a bounded wait and no fixed sleeps.
 */
public final class FxTestUtil {
    private static final int TIMEOUT_SECONDS = 10;

    private FxTestUtil() {}

    /**
     * Starts the toolkit once per test JVM and keeps it alive between window tests.
     */
    public static void initialize() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> Platform.setImplicitExit(false), null);
        try {
            Platform.startup(startup);
        } catch (IllegalStateException e) {
            // Another UI test has already started the toolkit.
            Platform.runLater(startup);
        }
        startup.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Runs an action on the JavaFX thread and propagates assertion failures to JUnit.
     */
    public static void runAndWait(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        try {
            task.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof Error error) {
                throw error;
            }
            if (e.getCause() instanceof Exception exception) {
                throw exception;
            }
            throw new AssertionError(e.getCause());
        }
    }
}
