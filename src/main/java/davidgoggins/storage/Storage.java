package davidgoggins.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import davidgoggins.DavidGogginsException;
import davidgoggins.task.Task;
import davidgoggins.ui.Ui;

/**
 * Deals with loading tasks from the save file and writing them back to it.
 *
 * <p>The only class that knows a file is involved: {@link davidgoggins.task.TaskList} asks it to load or
 * save and hears nothing about paths or IO errors. A failure is reported through the
 * {@link Ui} and then shrugged off, so the session survives an unwritable disk.
 */
public class Storage {

    /**
     * Where the list is saved between runs.
     *
     * <p>Held as a {@link Path} rather than a string so the correct separator is used on
     * every OS, and kept relative to the folder the program is run from so it works on
     * any machine.
     */
    private final Path file;

    /** Used to warn the user when a load or a save does not work out. */
    private final Ui ui;

    /**
     * @param filePath where to keep the saved list, e.g. {@code "data/tasks.txt"}
     * @param ui       used to report a load or save that fails
     */
    public Storage(String filePath, Ui ui) {
        this.file = Path.of(filePath);
        this.ui = ui;
        // The folder is not created here: a run that never changes the list should not
        // leave a stray ./data/ folder behind. save() creates it when it is needed.
    }

    /**
     * Writes the whole list to the save file, replacing whatever was there before.
     *
     * <p>The entire file is rewritten on every change rather than appending one line,
     * because {@code delete}, {@code mark} and {@code unmark} alter lines that are
     * already written, and a file has no "replace line 2" operation.
     *
     * @param tasks the tasks to write, in the order they should be stored
     */
    public void save(List<Task> tasks) {
        StringBuilder lines = new StringBuilder();
        for (Task task : tasks) {
            lines.append(task.toSaveFormat()).append(System.lineSeparator());
        }

        Path temporary = null;
        try {
            // createDirectories (plural) makes any missing parent folders and does
            // nothing if they already exist, unlike createDirectory which throws.
            Path parent = file.getParent();
            if (parent != null) { // null only if the path were a bare file name
                Files.createDirectories(parent);
            }

            // The new list is written to a temporary file first and only then moved
            // into place. Writing straight to the real file would empty it before the
            // new contents were written, so a crash or a full disk part-way through
            // would leave the user with no tasks at all rather than the previous ones.
            // The temporary file goes in the same folder so the move stays on one disk.
            temporary = Files.createTempFile(parent, "tasks", ".tmp");
            Files.writeString(temporary, lines.toString());
            replace(temporary, file);
            temporary = null; // the move consumed it, so there is nothing left to clean up
        } catch (IOException e) {
            // Saving is a background chore, so a failure warns the user but does not
            // stop the command they asked for from succeeding in memory.
            ui.showWarning("could not save your tasks to " + file
                    + " (" + e.getMessage() + "). Your last change is in this session only.");
        } finally {
            deleteIfPresent(temporary);
        }
    }

    /**
     * Moves {@code source} onto {@code target}, replacing it.
     *
     * <p>An atomic move is preferred, because it means a reader can only ever see the
     * old file or the new one, never a half-written mixture. Not every file system
     * supports it, so a plain replacing move is used when it is refused.
     */
    private static void replace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Removes a leftover temporary file, ignoring any further failure. */
    private static void deleteIfPresent(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Nothing useful can be done about a temporary file that will not delete,
            // and the user has already been told the save itself failed.
        }
    }

    /**
     * Reads the previously saved tasks back off disk.
     *
     * <p>Called once at start-up. A missing file, an unreadable file and a corrupted
     * line are each survivable and handled separately below: start-up always continues,
     * with a warning where the user would otherwise wonder where their tasks went.
     *
     * @return the tasks that could be read, which may be an empty list
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (NoSuchFileException e) {
            // Expected on a first run, so this is not worth telling the user about.
            // Caught before IOException because it is a subclass of it.
            return tasks;
        } catch (IOException e) {
            // Covers an unreadable file, a folder where the file should be, and so on.
            ui.showWarning("could not read your saved tasks ("
                    + e.getMessage() + "). Starting with an empty list.");
            return tasks;
        }

        int skipped = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue; // a stray blank line is harmless, not corruption
            }
            try {
                tasks.add(Task.fromSaveFormat(line));
            } catch (DavidGogginsException e) {
                skipped++;
            }
        }

        if (skipped > 0) {
            ui.showWarning("skipped " + skipped + " unreadable line"
                    + (skipped == 1 ? "" : "s") + " in " + file
                    + ". They will be dropped the next time the list changes.");
        }
        return tasks;
    }
}
