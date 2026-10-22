package seedu.address.model.person;

import java.util.Objects;

/**
 * Represents a person's ID that is automatically assigned when their contact is added.
 */
public class PersonId {
    private static int nextId = 0;

    private final int id;

    public PersonId() {
        id = nextId;
        nextId++;
    }

    /**
     * Initialises the next ID to assign to a person
     *
     * @param nextId the next free ID
     */
    public static void setNextId(int nextId) {
        PersonId.nextId = nextId;
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
