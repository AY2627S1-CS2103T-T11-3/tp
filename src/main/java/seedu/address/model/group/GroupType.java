package seedu.address.model.group;

import static java.util.Objects.requireNonNull;

/**
 * Represents a Group's type in the address book.
 */
public class GroupType {

    public enum GType {
        CLASS,
        DEPARTMENT
    }

    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers should only contain digits, and should be at least 3 digits long";
    public final GType groupType;

    public GroupType(String groupTypeString) {
        requireNonNull(groupTypeString);
        groupType = mapStringToGType(groupTypeString);
    }

    /**
     * Maps a String to the GType enum
     */
    public static GType mapStringToGType(String groupTypeString) throws IllegalArgumentException {
        return switch (groupTypeString) {
            case "class" -> GType.CLASS;
            case "department" -> GType.DEPARTMENT;
            default -> throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        };
    }

    @Override
    public String toString() {
        return switch(groupType) {
            case CLASS -> "class";
            case DEPARTMENT -> "department";
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GroupType otherGroupType)) {
            return false;
        }

        return groupType.equals(otherGroupType.groupType);
    }

    @Override
    public int hashCode() {
        return groupType.hashCode();
    }

}
