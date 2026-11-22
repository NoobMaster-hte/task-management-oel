package oel.taskmanager;

/**
 * Checked exception thrown when an operation refers to a task identifier
 * that does not exist in the {@link TaskManager}.
 *
 * @author Muhammad Hassaan Rashid
 */
public class TaskNotFoundException extends Exception {

    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;

    /** The identifier that could not be found. */
    private final int taskId;

    /**
     * Creates an exception for the task identifier that could not be found.
     *
     * @param taskId the identifier that was looked up but does not exist
     */
    public TaskNotFoundException(int taskId) {
        super("Task with ID " + taskId + " does not exist.");
        this.taskId = taskId;
    }

    /**
     * Returns the identifier that could not be found.
     *
     * @return the missing task identifier
     */
    public int getTaskId() {
        return taskId;
    }
}
