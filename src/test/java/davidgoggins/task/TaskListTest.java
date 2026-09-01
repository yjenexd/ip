package davidgoggins.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import davidgoggins.DavidGogginsException;
import davidgoggins.storage.Storage;
import davidgoggins.ui.Ui;

/**
 * Tests {@link TaskList#find(String)} and the numbering in {@link TaskList#format(List)}.
 *
 * <p>Each list is built through the constructor that takes its tasks directly, so no
 * save file is read. Searching only reads the list, so the {@link Storage} handed to
 * that constructor is never asked to write anything.
 */
public class TaskListTest {

    /** Builds a list holding exactly the given tasks, with a Storage that is never used. */
    private static TaskList listOf(Task... tasks) {
        Storage unused = new Storage("build/tmp/task-list-test.txt", new Ui());
        return new TaskList(unused, List.of(tasks));
    }

    /** The three tasks the searches below are run against. */
    private static TaskList sampleList() throws DavidGogginsException {
        return listOf(new Todo("read book"),
                new Deadlines("return book", "2026-06-06"),
                new Todo("run 10 miles"));
    }

    @Test
    public void find_keywordInSomeDescriptions_onlyThoseReturned() throws DavidGogginsException {
        List<Task> matches = sampleList().find("book");
        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[D][ ] return book (by: 2026-06-06)", matches.get(1).toString());
    }

    /** A user typing quickly should not have to match the capitals they first used. */
    @Test
    public void find_keywordInDifferentCase_stillMatches() throws DavidGogginsException {
        assertEquals(2, sampleList().find("BOOK").size());
    }

    /** The stored description is what varies in case here, rather than the keyword. */
    @Test
    public void find_descriptionInDifferentCase_stillMatches() throws DavidGogginsException {
        assertEquals(1, listOf(new Todo("Read Book")).find("read").size());
    }

    @Test
    public void find_noDescriptionContainsKeyword_emptyListReturned() throws DavidGogginsException {
        assertTrue(sampleList().find("swim").isEmpty());
    }

    /** Matching is on any part of the description, not only whole words. */
    @Test
    public void find_keywordIsPartOfAWord_stillMatches() throws DavidGogginsException {
        assertEquals(1, sampleList().find("mile").size());
    }

    /** Searching must not disturb the list it searches. */
    @Test
    public void find_afterSearching_listUnchanged() throws DavidGogginsException {
        TaskList tasks = sampleList();
        tasks.find("book");
        assertEquals(3, tasks.size());
    }

    /** Matches are numbered from 1, not by their position in the whole list. */
    @Test
    public void format_matchesFromMiddleOfList_numberedFromOne() throws DavidGogginsException {
        String lines = TaskList.format(sampleList().find("book"));
        assertEquals(" 1.[T][ ] read book" + System.lineSeparator()
                + " 2.[D][ ] return book (by: 2026-06-06)", lines);
    }

    @Test
    public void format_emptyList_emptyStringReturned() {
        assertEquals("", TaskList.format(List.of()));
    }

    /** The whole list is numbered by the same code the search results use. */
    @Test
    public void toString_wholeList_sameFormatAsMatches() throws DavidGogginsException {
        TaskList tasks = sampleList();
        assertEquals(TaskList.format(tasks.find("")), tasks.toString());
    }
}
