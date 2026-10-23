---
layout: page
title: User Guide
---

CaNvUS is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, CaNvUS can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/Alice Tan p/91234567 e/alice@example.com a/1 Clementi Road ct/student t/Year1` : Adds a contact named `Alice Tan` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE e/EMAIL a/ADDRESS ct/CONTACT_TYPE [t/TAG]`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including zero.
</div>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 ct/student`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Jurong East p/1234567 ct/colleague`

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Filtering persons: `filter`

Lists contacts that satisfy all supplied criteria.

Format: `filter [n/NAME] [ct/CONTACT_TYPE] [d/DEPARTMENT] [a/ADDRESS] [t/TAG]...`

| Prefix | Criterion | Matching rule |
| ------ | --------- | ------------- |
| `n/` | Name | Case-insensitive, contiguous substring |
| `ct/` | Contact type | Case-insensitive full value or unambiguous prefix of `student`, `guardian`, or `colleague` |
| `d/` | Department | Case-insensitive, contiguous substring; only colleagues with a department can match |
| `a/` | Address | Case-insensitive, contiguous substring |
| `t/` | Tag | Case-sensitive, complete tag |

* Running `filter` without criteria shows all contacts.
* All supplied criteria must match (`AND`). For example, `ct/colleague d/science` requires both conditions.
* Each prefix can appear at most once, except `t/`. If multiple tags are supplied, every tag must be present.
* Empty values and repeated single-value prefixes are rejected. For example, `filter n/` and `filter d/Science d/Math` are invalid.
* Leading and trailing whitespace is ignored. The order and whitespace inside a name, address, or department phrase are significant.
* `ct/student` checks a contact's actual type. `t/Student` only checks for the exact tag `Student`.
* Contact-type abbreviations start at the beginning of the type: `ct/stu`, `ct/guar`, and `ct/col` are valid. Unknown or ambiguous abbreviations are rejected.
* Tags contain one or more letters or digits, without spaces or punctuation. For example, `Year2` is valid but `Year 2` and `Year-2` are invalid.
* Each filter replaces the previous search and checks all contacts; successive filters are not cumulative.
* If no contacts match, an empty list is displayed. A department criterion combined with `ct/student` or `ct/guardian` therefore produces no results.

Examples:

* `filter n/ali` matches names such as `Alice` and `Kalid`.
* `filter ct/STU` selects student contacts, regardless of their tags.
* `filter d/science` matches departments such as `Food Science` and `Computer Science`.
* `filter ct/col d/food sci` selects colleagues whose departments contain `food sci`.
* `filter n/ali a/clementi t/Student t/Year2` requires the name and address substrings and both exact tags.

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `filter n/Betsy` followed by `delete 1` deletes the 1st person in the filter results.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS ct/CONTACT_TYPE [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 ct/colleague t/friend`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Filter** | `filter [n/NAME] [ct/CONTACT_TYPE] [d/DEPARTMENT] [a/ADDRESS] [t/TAG]...`<br> e.g., `filter n/ali ct/student a/clementi t/Year2`
**List** | `list`
**Help** | `help`
