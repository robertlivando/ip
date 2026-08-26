public class Task {
    private final String description;
    private boolean isCompleted;

    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public void markAsDone() {
        this.isCompleted = true;
    }

    public void markAsNotDone() {
        this.isCompleted = false;
    }

    public String getStatusIcon() {
        return isCompleted ? "X" : " ";
    }

    /**
     * Returns the task fields shared by all task types in the storage file format.
     *
     * @return completion status and description separated by delimiters
     */
    public String toFileFormat() {
        return String.format("%d | %s", isCompleted ? 1 : 0, description);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), description);
    }
}
