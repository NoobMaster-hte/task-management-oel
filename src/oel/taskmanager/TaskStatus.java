package oel.taskmanager;

/**
 * Enumeration of the lifecycle states a {@link Task} can be in.
 * Each constant carries a human-readable label used in the GUI.
 *
 * @author Muhammad Hassaan Rashid
 */
public enum TaskStatus {

    /** The task has been created but work has not started. */
    PENDING("Pending"),

    /** Work on the task has started but is not finished. */
    IN_PROGRESS("In Progress"),

    /** The task is finished. */
    COMPLETED("Completed");

    /** Human-readable label shown in the user interface. */
    private final String label;

    /**
     * Creates a status constant with the given display label.
     *
     * @param label the human-readable text shown in the user interface
     */
    TaskStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label of this status.
     *
     * @return the display label, e.g. {@code "In Progress"}
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the display label so that Swing components such as
     * {@code JComboBox} and {@code JTable} show readable text.
     *
     * @return the display label of this status
     */
    @Override
    public String toString() {
        return label;
    }
}
