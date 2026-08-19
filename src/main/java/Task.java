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

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), description);
    }
}
