# DavidGoggins project template

This is a project template for a greenfield Java project. It's named after the chatbot's persona, _David Goggins_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/DavidGoggins.java` file, right-click it, and choose `Run DavidGoggins.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
   +----------------------------------------------------------+
   |      ____      _    __     __ ___  ____                  |
   |     |  _ \    / \   \ \   / /|_ _||  _ \                 |
   |     | | | |  / _ \   \ \ / /  | | | | | |                |
   |     | |_| | / ___ \   \ V /   | | | |_| |                |
   |     |____/ /_/   \_\   \_/   |___||____/                 |
   |       ____   ___    ____   ____  ___  _   _  ____        |
   |      / ___| / _ \  / ___| / ___||_ _|| \ | |/ ___|       |
   |     | |  _ | | | || |  _ | |  _  | | |  \| |\___ \       |
   |     | |_| || |_| || |_| || |_| | | | | |\  | ___) |      |
   |      \____| \___/  \____| \____||___||_| \_||____/       |
   |                                                          |
   |       __                                       __        |
   |      /  \                                     /  \       |
   |     | ## |===================================| ## |      |
   |     | ## |===================================| ## |      |
   |      \__/                                     \__/       |
   |                                                          |
   |       "WHO'S GONNA CARRY THE BOATS AND THE LOGS?!"       |
   |                 THEY DON'T KNOW ME, SON!                 |
   |                                                          |
   |                     >> STAY HARD. <<                     |
   +----------------------------------------------------------+
   Hello! I'm David Goggins.
   What can I do for you?
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

## Commands

Type a command at the prompt and press Enter. Command words are case-insensitive
(`LIST` works the same as `list`); task descriptions keep the case you typed.

| Command | Format | Example |
| --- | --- | --- |
| Add a todo | `todo <description>` | `todo read book` |
| Add a deadline | `deadline <description> /by <yyyy-mm-dd>` | `deadline return book /by 2026-09-10` |
| Add an event | `event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>` | `event project meeting /from 2026-09-10 /to 2026-09-11` |
| List all tasks | `list` | `list` |
| Mark as done | `mark <task number>` | `mark 2` |
| Mark as not done | `unmark <task number>` | `unmark 2` |
| Delete a task | `delete <task number>` | `delete 2` |
| Exit | `bye` | `bye` |

Task numbers are the numbers shown by `list`, starting at 1.

### Example session

```
todo read book
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

deadline return book /by 2026-09-10
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2026-09-10)
 Now you have 2 tasks in the list.
____________________________________________________________

mark 1
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________

list
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-09-10)
____________________________________________________________
```

### Saving

Tasks are saved automatically to `data/tasks.txt` after every change, and reloaded
when the program starts, so your list survives between runs. The path is relative to
the folder you run the program from, so launch from the project root. The `data`
folder is created for you on the first save; you do not need to make it yourself.

The file holds one task per line, with fields separated by `|`. The second field is
`1` for a done task and `0` for one still outstanding:

```text
T | 1 | read book
D | 0 | return book | 2026-09-10
E | 0 | project meeting | 2026-09-10 | 2026-09-11
```

Because `|` separates the fields, it cannot appear in a task's own text; the program
refuses such a task rather than saving something it could not read back. If a line of
the file is unreadable for any other reason, it is skipped with a warning and the
remaining tasks still load.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
