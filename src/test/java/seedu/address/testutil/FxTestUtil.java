package seedu.address.testutil;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

import javafx.animation.AnimationTimer;
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
        callAndWait(() -> {
            action.run();
            return null;
        });
    }

    /**
     * Returns the result of an action on the JavaFX thread.
     */
    public static <T> T callAndWait(Callable<T> action) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return action.call();
        }
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return awaitResult(task);
    }

    /**
     * Waits outside the JavaFX thread while checking a window condition once per pulse.
     */
    public static void waitUntil(BooleanSupplier condition, String description) throws Exception {
        if (Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Window-state waits must run outside the JavaFX thread");
        }
        CompletableFuture<Void> completed = new CompletableFuture<>();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    if (condition.getAsBoolean()) {
                        stop();
                        completed.complete(null);
                    }
                } catch (Throwable error) {
                    stop();
                    completed.completeExceptionally(error);
                }
            }
        };
        runAndWait(timer::start);
        try {
            awaitResult(completed);
        } catch (TimeoutException e) {
            throw new AssertionError("Timed out waiting for " + description, e);
        } finally {
            runAndWait(timer::stop);
        }
    }

    private static <T> T awaitResult(Future<T> task) throws Exception {
        try {
            return task.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
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
