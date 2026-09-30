package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOB;
import static seedu.address.logic.commands.RemarkCommand.MESSAGE_ARGUMENTS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.model.ModelManager;

public class RemarkCommandTest {
    private final ModelManager model = new ModelManager();

    @Test
    public void execute() {
        final String remark = "Some remark";
        assertCommandFailure(new RemarkCommand(INDEX_FIRST_PERSON, remark), model,
                String.format(MESSAGE_ARGUMENTS, INDEX_FIRST_PERSON.getOneBased(), remark));
    }

    @Test
    public void equals() {
        final RemarkCommand standardCommand = new RemarkCommand(INDEX_FIRST_PERSON, VALID_REMARK_AMY);
        assertTrue(standardCommand.equals(new RemarkCommand(INDEX_FIRST_PERSON, VALID_REMARK_AMY)));
        assertTrue(standardCommand.equals(standardCommand));
        assertFalse(standardCommand.equals(null));
        assertFalse(standardCommand.equals(new ClearCommand()));
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_SECOND_PERSON, VALID_REMARK_AMY)));
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_FIRST_PERSON, VALID_REMARK_BOB)));
    }
}
