package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR_OF_STUDY;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Colleague;
import seedu.address.model.person.Email;
import seedu.address.model.person.Guardian;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.Student;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_CONTACT_TYPE, PREFIX_TAG, PREFIX_YEAR_OF_STUDY);

        // Only name is required as the user might not have the information about the other fields when they are first
        // adding the contact.
        if (!arePrefixesPresent(argMultimap, PREFIX_NAME) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_CONTACT_TYPE, PREFIX_YEAR_OF_STUDY);

        // Name is always present.
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());

        // Phone, email, and address are made optional as the users may not have the information when they are first
        // adding the contact.
        Phone phone = null;
        Email email = null;
        Address address = null;

        Optional<String> phoneValue = argMultimap.getValue(PREFIX_PHONE);
        Optional<String> emailValue = argMultimap.getValue(PREFIX_EMAIL);
        Optional<String> addressValue = argMultimap.getValue(PREFIX_ADDRESS);

        if (phoneValue.isPresent()) {
            phone = ParserUtil.parsePhone(phoneValue.get());
        }

        if (emailValue.isPresent()) {
            email = ParserUtil.parseEmail(emailValue.get());
        }

        if (addressValue.isPresent()) {
            address = ParserUtil.parseAddress(addressValue.get());
        }

        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Role role = ParserUtil.parseContactType(argMultimap.getValue(PREFIX_CONTACT_TYPE).orElse("STUDENT"));
        Integer yearOfStudy = null;
        if (argMultimap.getValue(PREFIX_YEAR_OF_STUDY).isPresent()) {
            if (role != Role.STUDENT) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
            }

            yearOfStudy = ParserUtil.parseYearOfStudy(argMultimap.getValue(PREFIX_YEAR_OF_STUDY).get());
        }

        Person person = switch (role) {
            case Role.COLLEAGUE -> new Colleague(name, phone, email, address, tagList, null);
            case Role.GUARDIAN -> new Guardian(name, phone, email, address, tagList);
            case Role.STUDENT -> new Student(name, phone, email, address, tagList, yearOfStudy);
            default -> throw new ParseException("Invalid contact type given");
        };

        return new AddCommand(person);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
