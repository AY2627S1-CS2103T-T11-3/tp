package seedu.address.model.group;

import java.util.Objects;

/**
 * Represents a group's ID that is automatically assigned when the group is added.
 *
 * Guaranteed to be immutable.
 */
public final class GroupId {
    private static int nextId = 1;

    private final int id;

    private GroupId(int id) {
        this.id = id;
    }

    /**
     * Creates an automatically assigned group ID.
     * Used for creation of new Group objects.
     */
    public GroupId() {
        id = nextId;
        nextId++;
    }

    /**
     * Creates a new GroupId with the given numeric ID, without any checks if it has already been assigned.
     * Used when a GroupId to an existing Group object needs to be created.
     */
    public static GroupId createFromRawId(int id) {
        return new GroupId(id);
    }

    /**
     * Returns the next ID to automatically assign.
     */
    public static int getNextId() {
        return nextId;
    }

    /**
     * Initialises the next ID to assign to a group.
     *
     * @param nextId the next free ID.
     */
    public static void setNextId(int nextId) {
        GroupId.nextId = nextId;
    }

    /**
     * Compares the group ID to a numeric ID and returns if their value is equal.
     */
    public boolean matches(int id) {
        return this.id == id;
    }

    /**
     * Returns the raw numeric ID.
     */
    public int getRawId() {
        return id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupId groupId = (GroupId) o;
        return id == groupId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
