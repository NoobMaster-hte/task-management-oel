package oel.taskmanager;

/**
 * Represents a single task in the Task Management System.
 * A task has an immutable unique identifier, a mandatory title,
 * an optional description and a {@link TaskStatus}.
 * All mutating operations validate their input, so a {@code Task}
 * can never hold a blank title or a {@code null} status.
 *
 * @author Muhammad Hassaan Rashid
 */
public class Task {

    /** Maximum number of characters allowed in a task title. */
    public static final int MAX_TITLE_LENGTH = 100;

    /** Unique, immutable identifier of the task. */
    private final int id;
    /** Mandatory task title (never blank). */
    private String title;
    /** Optional task description (never null). */
    private String description;
    /** Current status of the task (never null). */
    private TaskStatus status;

    /**
     * Creates a new task after validating and normalising the input.
     *
     * @param id          the unique identifier of the task
     * @param title       the title; must not be {@code null}, empty or whitespace-only
     * @param description the description; {@code null} is treated as an empty string
     * @param status      the initial status; must not be {@code null}
     * @throws IllegalArgumentException if the title or status is invalid
     */
    public Task(int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = validateTitle(title);
        this.description = normaliseDescription(description);
        this.status = validateStatus(status);
    }

    /**
     * Validates a title and returns its trimmed form.
     *
     * @param title the raw title entered by the user
     * @return the trimmed title
     * @throws IllegalArgumentException if the title is {@code null}, blank,
     *                                  or longer than {@link #MAX_TITLE_LENGTH}
     */
    public static String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty or contain only spaces.");
        }
        String trimmed = title.trim();
        if (trimmed.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    "Title cannot be longer than " + MAX_TITLE_LENGTH + " characters.");
        }
        return trimmed;
    }

    /**
     * Validates a status value.
     *
     * @param status the status to check
     * @return the same status if it is valid
     * @throws IllegalArgumentException if the status is {@code null}
     */
    private static TaskStatus validateStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status must be selected.");
        }
        return status;
    }

    /**
     * Normalises a description by trimming it and replacing {@code null} with "".
     *
     * @param description the raw description
     * @return the trimmed description, never {@code null}
     */
    private static String normaliseDescription(String description) {
        return description == null ? "" : description.trim();
    }

    /**
     * Updates title, description and status in a single atomic step.
     * If any argument is invalid, none of the fields are changed.
     *
     * @param newTitle       the new title (must not be blank)
     * @param newDescription the new description ({@code null} becomes "")
     * @param newStatus      the new status (must not be {@code null})
     * @throws IllegalArgumentException if the title or status is invalid
     */
    public void update(String newTitle, String newDescription, TaskStatus newStatus) {
        String validTitle = validateTitle(newTitle);
        String validDescription = normaliseDescription(newDescription);
        TaskStatus validStatus = validateStatus(newStatus);
        this.title = validTitle;
        this.description = validDescription;
        this.status = validStatus;
    }

    /**
     * Returns the unique identifier of this task.
     *
     * @return the task ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the title of this task.
     *
     * @return the title, never blank
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the description of this task.
     *
     * @return the description, possibly empty but never {@code null}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the current status of this task.
     *
     * @return the status, never {@code null}
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Changes only the status of this task.
     *
     * @param status the new status; must not be {@code null}
     * @throws IllegalArgumentException if the status is {@code null}
     */
    public void setStatus(TaskStatus status) {
        this.status = validateStatus(status);
    }

    /**
     * Returns a one-line textual representation of this task.
     *
     * @return a string such as {@code [1] Buy milk (Pending)}
     */
    @Override
    public String toString() {
        return "[" + id + "] " + title + " (" + status + ")";
    }
}
