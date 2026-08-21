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
 Now you have 1 tasks in the list.
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
 Now you have 1 tasks in the list.
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
 Now you have 1 tasks in the list.
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
 Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________
 OOPS! "two" is not a task number. Use a whole number, e.g. mark 2.
____________________________________________________________

____________________________________________________________
 OOPS! Tell me which task number, e.g. unmark 2.
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
 Now you have 1 tasks in the list.
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
 Now you have 1 tasks in the list.
____________________________________________________________

{{FAREWELL}}
```

### TC9: Adds each kind of task

**Aim:** Checks the three task types end to end: `todo`, `deadline ... /by ...` and `event ... /from ... /to ...` are parsed, confirmed with the running list size, and shown by `list` with the right type box and details.

**Input:**

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
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
 Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: June 6th)
 Now you have 2 tasks in the list.
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
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
 2.[D][ ] return book (by: June 6th)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
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
 OOPS! I don't know the command "read". I understand: todo, deadline, event, list, mark, unmark, bye.
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
 OOPS! The description of a todo cannot be empty. Try: todo read book
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
deadline /by Sunday
deadline return book /by
deadline return book /by Sunday
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 OOPS! A deadline needs a /by part. Try: deadline return book /by Sunday
____________________________________________________________

____________________________________________________________
 OOPS! The description of a deadline cannot be empty. Try: deadline return book /by Sunday
____________________________________________________________

____________________________________________________________
 OOPS! Tell me when it is due after /by. Try: deadline return book /by Sunday
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Sunday)
____________________________________________________________

{{FAREWELL}}
```

### TC13: Rejects malformed events

**Aim:** Checks each missing piece of an `event` — no `/from`, no `/to`, and an empty description, start or end — and that a correct event afterwards is still added as task 1, proving the rejected lines did not reach the list.

**Input:**

```text
event project meeting
event project meeting /from Mon 2pm
event /from Mon 2pm /to 4pm
event project meeting /from /to 4pm
event project meeting /from Mon 2pm /to
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**

```text
{{GREETING}}
____________________________________________________________
 OOPS! An event needs a /from part. Try: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________

____________________________________________________________
 OOPS! An event needs a /to part after /from. Try: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________

____________________________________________________________
 OOPS! The description of an event cannot be empty. Try: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________

____________________________________________________________
 OOPS! Tell me when the event starts after /from. Try: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________

____________________________________________________________
 OOPS! Tell me when the event ends after /to. Try: event project meeting /from Mon 2pm /to 4pm
____________________________________________________________

____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
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
 Now you have 1 tasks in the list.
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
 OOPS! The description of a todo cannot be empty. Try: todo read book
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][X] run 10 miles
____________________________________________________________

{{FAREWELL}}
```
