package davidgoggins.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import davidgoggins.DavidGogginsException;
import davidgoggins.task.Deadlines;

/**
 * Tests {@link Parser#parseDeadline(String)}.
 *
 * <p>That method is a good fit for unit testing: it is static, it reads nothing from
 * disk and prints nothing, so a test only has to hand it a string and look at what
 * comes back. The result is checked through {@code toString()} and
 * {@code toSaveFormat()}, since {@link Deadlines} exposes no getters.
 */
public class ParserTest {

    @Test
    public void parseDeadline_descriptionAndDate_deadlineCreated() throws DavidGogginsException {
        Deadlines deadline = Parser.parseDeadline("return book /by 2026-09-10");
        assertEquals("[D][ ] return book (by: 2026-09-10)", deadline.toString());
    }

    /** A new deadline should never start out ticked off, whatever the input. */
    @Test
    public void parseDeadline_descriptionAndDate_notDone() throws DavidGogginsException {
        Deadlines deadline = Parser.parseDeadline("return book /by 2026-09-10");
        assertEquals("D | 0 | return book | 2026-09-10", deadline.toSaveFormat());
    }

    /** The parser trims, so stray spaces around either part must not survive. */
    @Test
    public void parseDeadline_extraSpacesAroundParts_partsTrimmed() throws DavidGogginsException {
        Deadlines deadline = Parser.parseDeadline("   return book    /by    2026-09-10   ");
        assertEquals("[D][ ] return book (by: 2026-09-10)", deadline.toString());
    }

    /** Only the command word is lower-cased, so a description keeps the user's capitals. */
    @Test
    public void parseDeadline_mixedCaseDescription_capitalisationKept() throws DavidGogginsException {
        Deadlines deadline = Parser.parseDeadline("Return CS2103T Book /by 2026-09-10");
        assertEquals("[D][X] Return CS2103T Book (by: 2026-09-10)", markedDone(deadline).toString());
    }

    /**
     * The split is on the bare keyword rather than {@code " /by "}, so the spaces
     * around it are optional. This test pins that behaviour down so a future change to
     * the split cannot break it unnoticed.
     */
    @Test
    public void parseDeadline_noSpacesAroundKeyword_stillSplit() throws DavidGogginsException {
        Deadlines deadline = Parser.parseDeadline("return book/by2026-09-10");
        assertEquals("[D][ ] return book (by: 2026-09-10)", deadline.toString());
    }

    /** Splitting stops after the first {@code /by}, so a later one is part of the date. */
    @Test
    public void parseDeadline_secondByKeyword_onlyFirstSplits() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by 2026-09-10 /by 2026-09-11"));
        assertTrue(e.getMessage().contains("yyyy-mm-dd"), e.getMessage());
    }

    @Test
    public void parseDeadline_missingByKeyword_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book 2026-09-10"));
        assertTrue(e.getMessage().contains("needs a /by part"), e.getMessage());
    }

    /** The user typed a bare {@code deadline}, so the argument reaching the parser is empty. */
    @Test
    public void parseDeadline_emptyArgument_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline(""));
        assertTrue(e.getMessage().contains("needs a /by part"), e.getMessage());
    }

    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("/by 2026-09-10"));
        assertTrue(e.getMessage().contains("description of a deadline cannot be empty"), e.getMessage());
    }

    /** Whitespace alone is not a description either, because the parts are trimmed first. */
    @Test
    public void parseDeadline_blankDescription_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("    /by 2026-09-10"));
        assertTrue(e.getMessage().contains("description of a deadline cannot be empty"), e.getMessage());
    }

    @Test
    public void parseDeadline_missingDate_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by"));
        assertTrue(e.getMessage().contains("when it is due after /by"), e.getMessage());
    }

    @Test
    public void parseDeadline_blankDate_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by    "));
        assertTrue(e.getMessage().contains("when it is due after /by"), e.getMessage());
    }

    /** A date the user wrote in words cannot be turned into a {@code LocalDate}. */
    @Test
    public void parseDeadline_dateNotADate_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by next Friday"));
        assertTrue(e.getMessage().contains("yyyy-mm-dd"), e.getMessage());
    }

    /** The right day, but written the wrong way round. */
    @Test
    public void parseDeadline_dateInWrongOrder_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by 10-09-2026"));
        assertTrue(e.getMessage().contains("yyyy-mm-dd"), e.getMessage());
    }

    /** A date shaped correctly but naming a day that does not exist. */
    @Test
    public void parseDeadline_impossibleDate_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by 2026-13-40"));
        assertTrue(e.getMessage().contains("yyyy-mm-dd"), e.getMessage());
    }

    /** Marks a deadline done so the ticked-off rendering can be checked in one line. */
    private static Deadlines markedDone(Deadlines deadline) {
        deadline.markAsDone();
        return deadline;
    }
}
