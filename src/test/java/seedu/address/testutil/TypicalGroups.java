package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.group.Group;

/**
 * A utility class containing a list of {@code Group} objects to be used in tests.
 */
public class TypicalGroups {
    public static final Group RESPECT_5 = new GroupBuilder()
            .withName("Respect 5")
            .withType("class")
            .build();
    public static final Group MATH_DPT = new GroupBuilder()
            .withName("Math")
            .withType("department")
            .build();

    private TypicalGroups() {} // prevents instantiation

    public static List<Group> getTypicalGroups() {
        return new ArrayList<>(Arrays.asList(RESPECT_5, MATH_DPT));
    }
}
