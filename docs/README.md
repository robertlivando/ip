# Yachiyo User Guide

Yachiyo helps you manage to-dos, deadlines, and events from a JavaFX chat window. Enter a command in
the message field, then press **Enter** or select **Send** to receive Yachiyo's response.

## Quick start

1. [Start Yachiyo](../README.md#running-with-gradle).
2. Add a task, for example `todo Read book`.
3. Enter `list` to see the task and its number.
4. Use that number in commands such as `mark 1` or `delete 1`.
5. Enter `bye` when you are finished. The window closes after Yachiyo's farewell.

Yachiyo saves each change automatically to `data/yachiyo.txt`, so tasks remain available between
sessions.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add a to-do | `todo DESCRIPTION` | `todo Read book` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` | `deadline Submit report /by 20/9/2026 1700` |
| Add an event | `event DESCRIPTION /from START /to END` | `event Project meeting /from 20/9/2026 1400 /to 20/9/2026 1600` |
| List all tasks | `list` | `list` |
| Find by description | `find KEYWORD` | `find report` |
| Find on a date | `on DATE` | `on 20/9/2026` |
| Mark as complete | `mark TASK_NUMBER` | `mark 2` |
| Mark as incomplete | `unmark TASK_NUMBER` | `unmark 2` |
| Delete a task | `delete TASK_NUMBER` | `delete 2` |
| Exit Yachiyo | `bye` | `bye` |

## Input conventions

- Command words are not case-sensitive. Descriptions and search terms retain the text you enter.
- Dates use `d/M/yyyy`, for example `2/12/2026`.
- Date-times use `d/M/yyyy HHmm`, for example `2/12/2026 1800`.
- Task numbers start from 1 and appear in the results of `list`, `find`, and `on`.
- `[T]`, `[D]`, and `[E]` identify to-dos, deadlines, and events respectively.
- `[X]` identifies a completed task, while `[ ]` identifies an incomplete task.

## Adding a to-do

Format: `todo DESCRIPTION`

Example:

```text
todo Read book
```

## Adding a deadline

Format: `deadline DESCRIPTION /by DATE_TIME`

Example:

```text
deadline Submit report /by 20/9/2026 1700
```

## Adding an event

Format: `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME`

Example:

```text
event Project meeting /from 20/9/2026 1400 /to 20/9/2026 1600
```

The end date-time must be later than the start date-time.

## Listing tasks

Format: `list`

Yachiyo displays every task with its task number.

## Finding tasks by description

Format: `find KEYWORD`

Example:

```text
find report
```

The search is case-insensitive and matches the keyword anywhere in a task description. Results keep
their task numbers from the complete list.

## Finding tasks on a date

Format: `on DATE`

Example:

```text
on 20/9/2026
```

Yachiyo displays deadlines due on the date and events taking place on the date. A multi-day event is
included on its start date, end date, and every date in between.

## Marking a task as complete

Format: `mark TASK_NUMBER`

Example:

```text
mark 2
```

## Marking a task as incomplete

Format: `unmark TASK_NUMBER`

Example:

```text
unmark 2
```

## Deleting a task

Format: `delete TASK_NUMBER`

Example:

```text
delete 2
```

After deletion, the remaining task numbers may change. Run `list` again before using another task
number.

## Exiting Yachiyo

Format: `bye`

Yachiyo displays a farewell and then closes the JavaFX window.
