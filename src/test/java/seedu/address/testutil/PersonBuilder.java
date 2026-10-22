package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Student;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final Integer DEFAULT_PERSONID = 1;

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<Tag> tags;
    private Role role;
    private PersonId personId;
    private Integer yearOfStudy;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
        role = Role.STUDENT;
        personId = PersonId.createFromRawId(DEFAULT_PERSONID);
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        tags = new HashSet<>(personToCopy.getTags());
        role = personToCopy.getRole();
        personId = personToCopy.getId();
        if (personToCopy instanceof Student student) {
            yearOfStudy = student.getYearOfStudy();
        }
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /** Sets the role of the person being built. */
    public PersonBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    /**
     * Sets the person ID of the person being built.
     */
    public PersonBuilder withPersonId(Integer rawId) {
        this.personId = PersonId.createFromRawId(rawId);
        return this;
    }

    /**
     * Sets the year of study of the student being built.
     */
    public PersonBuilder withYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
        return this;
    }

    /**
     * Builds the person.
     */
    public Person build() {
        if (role == Role.STUDENT) {
            return new Student(name, phone, email, address, tags, personId.getRawId(), null, yearOfStudy);
        }

        return new Person(name, phone, email, address, tags, role, personId.getRawId(), null);
    }

}
