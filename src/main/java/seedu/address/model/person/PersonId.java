package seedu.address.model.person;

import java.util.Objects;

/**
 * Represents a person's ID that is automatically assigned when their contact is added.
 *
 * Guaranteed to be immutable.
 */
public final class PersonId {
    private static int nextId = 1;

    private final int id;

    private PersonId(int id) {
        this.id = id;
    }

    /**
     * Creates an automatically assigned person ID.
     */
    public PersonId() {
        id = nextId;
        nextId++;
    }

    /**
     * Creates a new PersonId with the given numeric ID, without any checks if it has already been assigned.
     */
    public static PersonId createFromRawId(int id) {
        return new PersonId(id);
    }

    /**
     * Returns the next ID to automatically assign.
     */
    public static int getNextId() {
        return nextId;
    }

    /**
     * Initialises the next ID to assign to a person.
     *
     * @param nextId the next free ID.
     */
    public static void setNextId(int nextId) {
        PersonId.nextId = nextId;
    }

    /**
     * Compares the person ID to a numeric ID and returns if their value is equal.
     *
     * @param id the numeric ID to compare.
     * @return Whether the numeric values of the person ID and the given ID number are equal.
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
        PersonId personId = (PersonId) o;
        return id == personId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
