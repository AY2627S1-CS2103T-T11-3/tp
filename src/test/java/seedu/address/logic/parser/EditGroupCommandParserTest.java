package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_GROUP;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditGroupCommand;
import seedu.address.model.group.GroupName;

public class EditGroupCommandParserTest {
    @Test
    public void parse_validArgs_returnsEditGroupCommand() {
        assertParseSuccess(new EditGroupCommandParser(), "1 New Class",
                new EditGroupCommand(INDEX_FIRST_GROUP, new GroupName("New Class")));
    }
}
