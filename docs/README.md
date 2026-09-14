# David Goggins User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Getting help: `help`

Shows every command David Goggins understands, with the format to type it in.
Commands that add a task also come with an example you can copy.

You don't need to remember this command: the greeting ends with
`Type help to see the commands I understand.`, and typing a command David Goggins
doesn't recognise points you back to `help`.

Format: `help`

- Capitalisation and surrounding spaces don't matter, so `HELP` works too.
- Nothing may follow `help`. For example, `help deadline` is refused with
  `OOPS! The help command takes no details. Try: help`.
- Asking for help doesn't change your task list.

Example: `help`

In the text version, the help page appears in the conversation:

```
____________________________________________________________
 Here are the commands I understand:
 todo <description>
 Example: todo read book
 deadline <description> /by <yyyy-mm-dd>
 Example: deadline return book /by 2026-09-10
 event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>
 Example: event project meeting /from 2026-09-10 /to 2026-09-11
 list
 find <keyword>
 mark <task number>
 unmark <task number>
 delete <task number>
 help
 bye
____________________________________________________________
```

In the app window, David Goggins replies `Opened the help window.` and the same page
opens in a separate **David Goggins - Help** window:

- You can keep typing commands while it is open.
- Typing `help` again brings the same window to the front instead of opening another.
- It closes when you close the main window or type `bye`.


## Feature XYZ

// Feature details