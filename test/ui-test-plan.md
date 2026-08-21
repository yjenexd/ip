# Text UI test plan

This file is both the documentation and the input for the `test-ui` skill. The
skill's runner parses the test cases below, runs the program once per test case
with the listed input lines piped to its standard input, and compares the console
output with the expected output recorded here.

Run the whole plan from the repository root with:

```bash
python3 .claude/skills/test-ui/scripts/run-ui-tests.py
```

## How to write a test case

Each test case is a level-3 heading of the form `### <id>: <title>`, followed by
three fields:

* `**Aim:**` — one line saying what the test case is checking and why.
* `**Input:**` — a fenced block holding one input line per line, exactly as the
  user would type it.
* `**Expected output:**` — a fenced block holding the console output the program
  should produce for that input, from the first line to the last.

Comparison ignores differences that are invisible on screen: line-ending style,
trailing spaces at the end of a line, and blank lines at the very end of the
output. Everything else must match character for character.

`{{NAME}}` inside an expected output is replaced by the reusable snippet of the
same name defined below. The greeting banner and the sign-off appear in every
run, so they are written once here rather than repeated in eight test cases.

## Reusable output snippets

#### GREETING

The banner and welcome message printed before any command is read.

```text
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

```

#### FAREWELL

The sign-off printed as the program exits.

```text
____________________________________________________________
Bye. Remember, stay hard!
____________________________________________________________
```

## Test cases

### TC1: Greets the user and says goodbye

**Aim:** Checks that the chatbot shows its banner and greeting on start-up, and its sign-off when the user types `bye`.

**Input:**

```text
bye
```

**Expected output:**

```text
{{GREETING}}
{{FAREWELL}}
```

### TC2: Lists an empty task list

**Aim:** Checks that `list` on a fresh session reports that the list is empty rather than printing an empty block.

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Your list is empty. Get after it!
____________________________________________________________

{{FAREWELL}}
```

### TC3: Adds tasks and lists them

**Aim:** Checks that unrecognised input is stored as a task, that each addition is confirmed, and that `list` numbers the tasks from 1 with an unticked box.

**Input:**

```text
read book
run 10 miles
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 added: read book
____________________________________________________________

____________________________________________________________
 added: run 10 miles
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[ ] read book
 2.[ ] run 10 miles
____________________________________________________________

{{FAREWELL}}
```

### TC4: Marks and unmarks a task

**Aim:** Checks that `mark 2` ticks the second task, that `unmark 2` clears it again, and that `list` reflects each change.

**Input:**

```text
read book
run 10 miles
mark 2
list
unmark 2
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 added: read book
____________________________________________________________

____________________________________________________________
 added: run 10 miles
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [X] run 10 miles
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[ ] read book
 2.[X] run 10 miles
____________________________________________________________

____________________________________________________________
 OK, I've marked this task as not done yet:
   [ ] run 10 miles
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[ ] read book
 2.[ ] run 10 miles
____________________________________________________________

{{FAREWELL}}
```

### TC5: Rejects a task number that does not exist

**Aim:** Checks that marking a task beyond the end of the list is reported to the user instead of crashing with an index error.

**Input:**

```text
read book
mark 5
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 added: read book
____________________________________________________________

____________________________________________________________
 There's no task 5 in your list.
____________________________________________________________

{{FAREWELL}}
```

### TC6: Rejects a missing or non-numeric task number

**Aim:** Checks that `mark two` and a bare `unmark` both produce the guidance message rather than being stored as new tasks.

**Input:**

```text
read book
mark two
unmark
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 added: read book
____________________________________________________________

____________________________________________________________
 Tell me which task number, e.g. mark 2.
____________________________________________________________

____________________________________________________________
 Tell me which task number, e.g. mark 2.
____________________________________________________________

{{FAREWELL}}
```

### TC7: Accepts the exit command in any capitalisation

**Aim:** Checks that `BYE` ends the session, confirming the exit command is matched case-insensitively.

**Input:**

```text
read book
BYE
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 added: read book
____________________________________________________________

{{FAREWELL}}
```

### TC8: Exits cleanly when the input ends without `bye`

**Aim:** Checks that reaching the end of piped input still prints the sign-off, rather than throwing NoSuchElementException.

**Input:**

```text
read book
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 added: read book
____________________________________________________________

{{FAREWELL}}
```
