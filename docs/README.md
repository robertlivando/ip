# Yachiyo User Guide

Yachiyo helps you manage to-dos, deadlines, and events from the command line. Commands are not
case-sensitive, but their arguments retain the text you enter.

Dates use `d/M/yyyy`, while date-times use `d/M/yyyy HHmm`. Task numbers start from 1 and are shown
by the `list`, `find`, and `on` commands.

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

Displays every task and its task number.

## Finding tasks by description

Format: `find KEYWORD`

Example:

```text
find report
```

The search is case-insensitive and matches the keyword anywhere in a task description.

## Finding tasks on a date

Format: `on DATE`

Example:

```text
on 20/9/2026
```

Displays deadlines due on the date and events taking place on that date.

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

## Exiting Yachiyo

Format: `bye`

Yachiyo automatically saves changes to `data/yachiyo.txt`.
