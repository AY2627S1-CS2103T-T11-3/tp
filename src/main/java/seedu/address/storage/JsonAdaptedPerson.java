package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Email;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Student;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final Integer personId;
    private final String groupId;
    private final Role role;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final Integer yearOfStudy;

    public JsonAdaptedPerson(String name, String phone, String email, String address,
            List<JsonAdaptedTag> tags, Integer personId) {
        this(name, phone, email, address, tags, Role.STUDENT, personId, null, null, null);
    }

    /** Backwards-compatible constructor for data without an explicit group ID. */
    public JsonAdaptedPerson(String name, String phone, String email, String address,
            List<JsonAdaptedTag> tags, Role role, Integer personId, String department) {
        this(name, phone, email, address, tags, role, personId, department, null, null);
    }

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags, @JsonProperty("role") Role role,
            @JsonProperty("personId") Integer personId, @JsonProperty("department") String department,
            @JsonProperty("groupId") String groupId, @JsonProperty("yearOfStudy") Integer yearOfStudy) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.personId = personId;
        // department is retained as a backwards-compatible name for older data files.
        this.groupId = groupId != null ? groupId : department;
        this.role = role;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.yearOfStudy = yearOfStudy;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;

        if (source.getPhone() != null) {
            phone = source.getPhone().value;
        } else {
            phone = null;
        }

        if (source.getEmail() != null) {
            email = source.getEmail().value;
        } else {
            email = null;
        }

        if (source.getAddress() != null) {
            address = source.getAddress().value;
        } else {
            address = null;
        }

        if (source instanceof Colleague sourceColleague) {
            groupId = sourceColleague.getGroupId();
        } else {
            groupId = source.getGroupId();
        }
        role = source.getRole();
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        personId = source.getId().getRawId();
        if (source instanceof Student) {
            yearOfStudy = ((Student) source).getYearOfStudy();
        } else {
            yearOfStudy = null;
        }
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        // Phone is optional
        final Phone modelPhone;

        // Prefer nested ifs here to make intent clearer, instead of making it sequential.
        if (phone != null) {
            if (!Phone.isValidPhone(phone)) {
                throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
            }

            modelPhone = new Phone(phone);
        } else {
            modelPhone = null;
        }

        // Email is optional
        final Email modelEmail;

        // Prefer nested ifs here to make intent clearer, instead of making it sequential.
        if (email != null) {
            if (!Email.isValidEmail(email)) {
                throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
            }

            modelEmail = new Email(email);
        } else {
            modelEmail = null;
        }

        // Address is optional
        final Address modelAddress;

        // Prefer nested ifs here to make intent clearer, instead of making it sequential.
        if (email != null) {
            if (!Address.isValidAddress(address)) {
                throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
            }

            modelAddress = new Address(address);
        } else {
            modelAddress = null;
        }

        if (role == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Role.class.getSimpleName()));
        }

        if (personId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    PersonId.class.getSimpleName()));
        }
        final Integer modelPersonId = personId;

        final Set<Tag> modelTags = new HashSet<>(personTags);
        if (role == Role.GUARDIAN) {
            return new Guardian(modelName, modelPhone, modelEmail, modelAddress, modelTags, modelPersonId);
        }

        if (role == Role.COLLEAGUE) {
            return new Colleague(modelName, modelPhone, modelEmail, modelAddress, modelTags, modelPersonId, groupId);
        }

        // Default is student
        return new Student(modelName, modelPhone, modelEmail, modelAddress, modelTags, modelPersonId, groupId,
                yearOfStudy);
    }

}
