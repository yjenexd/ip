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

Two further fields are optional, and are only needed by test cases about saving
and loading:

* `**Saved file:**` — a fenced block holding the contents of `data/tasks.txt` to
  put in place *before* the run. Use it to test loading. Omit it entirely and the
  run starts with no save file at all, which is what a first run on a new machine
  looks like; give it an empty block for an existing but empty file.
* `**Expected saved file:**` — a fenced block holding the contents `data/tasks.txt`
  should have *after* the run. Use it to test saving. Omit it and the file is not
  checked.

Each test case runs in its own empty working directory. Without that, a case
would inherit the tasks saved by the case before it, and the order the cases
happen to run in would change their results.

Comparison ignores differences that are invisible on screen: line-ending style,
trailing spaces at the end of a line, and blank lines at the very end of the
output. Everything else must match character for character.

`{{NAME}}` inside an expected output is replaced by the reusable snippet of the
same name defined below. The greeting banner and the sign-off appear in every
run, so they are written once here rather than repeated in every test case.

Two habits keep these test cases worth running:

* **Write the expected output from the source, not from a run.** If you paste in
  what the program printed, the test case can only ever confirm what the program
  already does, including its bugs. Predict the output, then run the case; a
  mismatch means either the program or the prediction is wrong, and working out
  which is the point of the exercise.
* **End a negative test case with a positive one.** After input that should be
  rejected, add a valid command and a `list`. A rejection that nevertheless
  corrupted the stored tasks — a half-built task, a wrong count, a shifted
  numbering — then shows up as a failure instead of passing unnoticed.

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

The table shows which test cases exercise each command, so an untested area is
easy to spot. A command should have at least one case for its normal use and one
for each way its input can be wrong.

| Area | Works correctly | Rejects bad input |
| --- | --- | --- |
| Start-up and `bye` | TC1, TC7, TC8 | — |
| `list` | TC2, TC3, TC4 | — |
| `todo` | TC3, TC9 | TC11 |
| `deadline` | TC9 | TC12 |
| `event` | TC9 | TC13 |
| `mark` / `unmark` | TC4 | TC5, TC6, TC18 |
| `delete` | TC15 | TC16 |
| Parsing the command word | TC7, TC17 | TC10, TC17 |
| Stored state after errors | TC14 | TC12, TC13, TC14, TC16 |


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

### TC3: Adds todos and lists them

**Aim:** Checks that `todo` stores a task, that each addition is confirmed with the new list size, and that `list` numbers the tasks from 1 with a type box and an unticked status box.

**Input:**

```text
todo read book
todo run 10 miles
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [T][ ] run 10 miles
 Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] run 10 miles
____________________________________________________________

{{FAREWELL}}
```

### TC4: Marks and unmarks a task

**Aim:** Checks that `mark 2` ticks the second task, that `unmark 2` clears it again, and that `list` reflects each change.

**Input:**

```text
todo read book
todo run 10 miles
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
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [T][ ] run 10 miles
 Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] run 10 miles
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][X] run 10 miles
____________________________________________________________

____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] run 10 miles
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] run 10 miles
____________________________________________________________

{{FAREWELL}}
```

### TC5: Rejects a task number that does not exist

**Aim:** Checks that marking a task beyond the end of the list is reported to the user instead of crashing with an index error.

**Input:**

```text
todo read book
mark 5
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 OOPS! There's no task 5 in your list: pick a number from 1 to 1.
____________________________________________________________

{{FAREWELL}}
```

### TC6: Rejects a missing or non-numeric task number

**Aim:** Checks that `mark two` is reported as a non-numeric task number and that a bare `unmark` asks for a number, each naming the command the user actually typed, rather than being stored as new tasks.

**Input:**

```text
todo read book
mark two
unmark
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 OOPS! "two" is not a task number you log! Use a whole number, e.g. mark 2.
____________________________________________________________

____________________________________________________________
 OOPS! Tell me which task number NOW!, e.g. unmark 2.
____________________________________________________________

{{FAREWELL}}
```

### TC7: Accepts the exit command in any capitalisation

**Aim:** Checks that `BYE` ends the session, confirming the exit command is matched case-insensitively.

**Input:**

```text
todo read book
BYE
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

{{FAREWELL}}
```

### TC8: Exits cleanly when the input ends without `bye`

**Aim:** Checks that reaching the end of piped input still prints the sign-off, rather than throwing NoSuchElementException.

**Input:**

```text
todo read book
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

{{FAREWELL}}
```

### TC9: Adds each kind of task

**Aim:** Checks the three task types end to end: `todo`, `deadline ... /by ...` and `event ... /from ... /to ...` are parsed, confirmed with the running list size, and shown by `list` with the right type box and details.

**Input:**

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 /to 2026-08-07
todo join sports club
mark 1
mark 4
todo borrow book
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2026-06-06)
 Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2026-08-06 to: 2026-08-07)
 Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [T][ ] join sports club
 Now you have 4 tasks in the list.
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] join sports club
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 5 tasks in the list.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-06-06)
 3.[E][ ] project meeting (from: 2026-08-06 to: 2026-08-07)
 4.[T][X] join sports club
 5.[T][ ] borrow book
____________________________________________________________

{{FAREWELL}}
```

### TC10: Rejects an unrecognised command

**Aim:** Checks that plain text with no command word is reported as unknown instead of being silently stored, now that every task must be added with `todo`, `deadline` or `event`.

**Input:**

```text
read book
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 OOPS! What are you saying! I don't know the command "read". I understand: todo, deadline, event, list, mark, unmark, delete, bye.
____________________________________________________________

____________________________________________________________
 Your list is empty. Get after it!
____________________________________________________________

{{FAREWELL}}
```

### TC11: Rejects a todo with no description

**Aim:** Checks the second error required by Level 5: `todo` with nothing after it is refused with a message showing the correct form, and the following `list` confirms no blank task was stored.

**Input:**

```text
todo
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 OOPS! The description of a todo cannot be empty you log! Try: todo read book
____________________________________________________________

____________________________________________________________
 Your list is empty. Get after it!
____________________________________________________________

{{FAREWELL}}
```

### TC12: Rejects malformed deadlines

**Aim:** Checks each way a `deadline` can be incomplete — no `/by`, no description before `/by`, nothing after `/by` — and that a correct deadline typed straight afterwards still works, so the failed attempts left no half-built task behind.

**Input:**

```text
deadline return book
deadline /by 2026-09-13
deadline return book /by
deadline return book /by 2026-09-13
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 OOPS! A deadline needs a /by part you log! Try: deadline return book /by 2026-09-10
____________________________________________________________

____________________________________________________________
 OOPS! The description of a deadline cannot be empty you log! Try: deadline return book /by 2026-09-10
____________________________________________________________

____________________________________________________________
 OOPS! Tell me when it is due after /by you log! Try: deadline return book /by 2026-09-10
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2026-09-13)
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: 2026-09-13)
____________________________________________________________

{{FAREWELL}}
```

### TC13: Rejects malformed events

**Aim:** Checks each missing piece of an `event` — no `/from`, no `/to`, and an empty description, start or end — and that a correct event afterwards is still added as task 1, proving the rejected lines did not reach the list.

**Input:**

```text
event project meeting
event project meeting /from 2026-09-14
event /from 2026-09-14 /to 2026-09-15
event project meeting /from /to 4pm
event project meeting /from 2026-09-14 /to
event project meeting /from 2026-09-14 /to 2026-09-15
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 OOPS! An event needs a /from part. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 OOPS! An event needs a /to part after /from. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 OOPS! The description of an event cannot be empty. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 OOPS! Tell me when the event starts after /from. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 OOPS! Tell me when the event ends after /to. Try: event project meeting /from 2026-09-10 /to 2026-09-11
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2026-09-14 to: 2026-09-15)
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] project meeting (from: 2026-09-14 to: 2026-09-15)
____________________________________________________________

{{FAREWELL}}
```

### TC14: Keeps the list intact across interleaved good and bad input

**Aim:** Interleaves valid and invalid commands — a `mark` past the end, a blank line, an uppercase `TODO`, a valid `mark`, an empty `todo` — and checks with `list` after each stage that only the valid commands changed the stored tasks and their done status.

**Input:**

```text
todo read book
mark 2
list

TODO run 10 miles
mark 2
todo
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 OOPS! There's no task 2 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________

____________________________________________________________
 OOPS! You typed nothing. Give me a command, e.g. list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [T][ ] run 10 miles
 Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] run 10 miles
____________________________________________________________

____________________________________________________________
 OOPS! The description of a todo cannot be empty you log! Try: todo read book
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][X] run 10 miles
____________________________________________________________

{{FAREWELL}}
```

### TC15: Deletes a task and renumbers the ones left

**Aim:** Checks that `delete` removes the right task, reports the new list size, and that the remaining tasks are renumbered, so a later `mark 1` acts on what is now task 1 rather than on the deleted position.

**Input:**

```text
todo read book
deadline return book /by 2026-09-13
event project meeting /from 2026-09-14 /to 2026-09-15
mark 3
delete 1
list
mark 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2026-09-13)
 Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2026-09-14 to: 2026-09-15)
 Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] project meeting (from: 2026-09-14 to: 2026-09-15)
____________________________________________________________

____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: 2026-09-13)
 2.[E][X] project meeting (from: 2026-09-14 to: 2026-09-15)
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: 2026-09-13)
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[D][X] return book (by: 2026-09-13)
 2.[E][X] project meeting (from: 2026-09-14 to: 2026-09-15)
____________________________________________________________

{{FAREWELL}}
```

### TC16: Rejects a delete that cannot be carried out

**Aim:** Checks every way `delete` can go wrong — on an empty list, with no number, with a non-number, with 0, and past the end — and that the one real task survives all of them and can still be deleted afterwards.

**Input:**

```text
delete 1
todo read book
delete
delete two
delete 0
delete 2
list
delete 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 OOPS! There's no task 1 in your list: your list is empty, so add a task first.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 OOPS! Tell me which task number to delete, e.g. delete 2.
____________________________________________________________

____________________________________________________________
 OOPS! "two" is not a task number you log! Use a whole number, e.g. delete 2.
____________________________________________________________

____________________________________________________________
 OOPS! There's no task 0 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 OOPS! There's no task 2 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________

____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 0 tasks in the list.
____________________________________________________________

____________________________________________________________
 Your list is empty. Get after it!
____________________________________________________________

{{FAREWELL}}
```

### TC17: Accepts any capitalisation and untidy spacing

**Aim:** Checks the parser's tidying-up: the command word is matched case-insensitively, leading spaces and runs of spaces between the command and its argument are ignored, and a line of only spaces counts as empty input rather than as an unknown command.

**Input:**

```text
   ToDo    read book
LIST
   
DEADLINE return book /by   2026-09-13
MARK 1
list
BYE
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________

____________________________________________________________
 OOPS! You typed nothing. Give me a command, e.g. list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2026-09-13)
 Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-09-13)
____________________________________________________________

{{FAREWELL}}
```

### TC18: Handles task numbers at and beyond the boundaries

**Aim:** Checks the edges of the task-number range — a negative number, 0, and a number too large to fit in an `int` — and that marking an already-done task or unmarking an already-not-done task is harmless, so the final `list` shows the single task untouched.

**Input:**

```text
todo read book
mark -1
mark 0
mark 99999999999999999999
mark 1
mark 1
unmark 2
unmark 1
unmark 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 OOPS! There's no task -1 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 OOPS! There's no task 0 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 OOPS! "99999999999999999999" is not a task number you log! Use a whole number, e.g. mark 2.
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 OOPS! There's no task 2 in your list: pick a number from 1 to 1.
____________________________________________________________

____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________

____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________

{{FAREWELL}}
```

### TC19: Saves every change to the data file

**Aim:** Checks that adding, marking and deleting are each written through to `data/tasks.txt` in the storage format, so nothing is lost if the program stops.

**Input:**

```text
todo read book
deadline return book /by 2026-09-13
event project meeting /from 2026-09-14 /to 2026-09-15
mark 1
delete 2
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: 2026-09-13)
 Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: 2026-09-14 to: 2026-09-15)
 Now you have 3 tasks in the list.
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________

____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: 2026-09-13)
 Now you have 2 tasks in the list.
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 1 | read book
E | 0 | project meeting | 2026-09-14 | 2026-09-15
```

### TC20: Loads saved tasks when the program starts

**Aim:** Checks that a task list saved by an earlier run comes back, with each kind of task rebuilt and its done status preserved, and that new tasks are numbered after the loaded ones.

**Saved file:**

```text
T | 1 | read book
D | 0 | return book | 2026-09-13
E | 1 | project meeting | 2026-09-14 | 2026-09-15
```

**Input:**

```text
list
todo run 10 miles
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-09-13)
 3.[E][X] project meeting (from: 2026-09-14 to: 2026-09-15)
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [T][ ] run 10 miles
 Now you have 4 tasks in the list.
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 1 | read book
D | 0 | return book | 2026-09-13
E | 1 | project meeting | 2026-09-14 | 2026-09-15
T | 0 | run 10 miles
```

### TC21: Starts with an empty list when there is no data file

**Aim:** Checks the first run on a new machine: with no `data/` folder and no save file, the program starts normally and says nothing about the missing file, then creates the file when the list first changes.

**Input:**

```text
list
todo read book
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Your list is empty. Get after it!
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 0 | read book
```

### TC22: Ignores a save file that is empty or blank

**Aim:** Checks that an existing but empty save file, and stray blank lines in one, are treated as "no tasks" rather than as corruption.

**Saved file:**

```text

T | 0 | read book

```

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________

{{FAREWELL}}
```

### TC23: Skips corrupted lines and keeps the readable ones

**Aim:** Checks the stretch goal. Each line of the save file can be malformed in a different way — an unknown type, a done flag that is not 0 or 1, too few fields, too many, an empty field, no separators at all — and none of them should stop the program starting. The readable lines must still load, and the user must be told how many were dropped.

**Saved file:**

```text
T | 1 | read book
X | 0 | unknown type marker
D | 2 | done flag is not 0 or 1 | 2026-09-13
T | 0
E | 0 | too few fields for an event | 2026-09-14
E | 0 | too many fields | Mon | 4pm | extra
D | 0 | empty due date |
T | 0 |
no separators at all
D | 0 | return book | 2026-09-13
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
 Warning: skipped 8 unreadable lines in data/tasks.txt. They will be dropped the next time the list changes.
{{GREETING}}
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-09-13)
____________________________________________________________

{{FAREWELL}}
```

### TC24: Refuses a task containing the field separator

**Aim:** Checks the round-trip hazard: a description containing the `|` used to separate fields in the save file would be written as an extra field and could not be read back, so it is refused up front. Ends with a valid task to show the list is unharmed.

**Input:**

```text
todo read book | now
deadline return book /by 2026-09-13 | Monday
todo read book
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 OOPS! A task cannot contain the "|" character, since that is what I use to separate fields when saving. Drop it and try again.
____________________________________________________________

____________________________________________________________
 OOPS! A task cannot contain the "|" character, since that is what I use to separate fields when saving. Drop it and try again.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________

{{FAREWELL}}
```

**Expected saved file:**

```text
T | 0 | read book
```
