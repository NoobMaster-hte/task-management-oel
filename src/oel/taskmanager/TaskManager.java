package oel.taskmanager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class that owns all tasks and implements the business logic of the
 * Task Management System (add, view, update, delete, change status).
 * Tasks are stored in a {@link LinkedHashMap} keyed by ID, which gives
 * O(1) lookup, update and deletion while preserving insertion order for display.
 * Identifiers are generated sequentially and are never reused.
 * This class has no dependency on Swing, so it can be tested without a GUI.
 *
 * @author Muhammad Hassaan Rashid
 */
public class TaskManager {

    /** All tasks keyed by ID, in insertion order. */
    private final Map<Integer, Task> tasks = new LinkedHashMap<>();
    /** Next identifier to assign; only ever increases. */
    private int nextId = 1;

    /**
     * Creates an empty task manager.
     */
    public TaskManager() {
        // Intentionally empty: the manager starts with no tasks.
    }

    /**
     * Adds a new task with the given status.
     *
     * @param title       the task title; must not be blank
     * @param description the task description; may be {@code null} or empty
     * @param status      the initial status; must not be {@code null}
     * @return the newly created task with its generated ID
     * @throws IllegalArgumentException if the title or status is invalid
     */
    public Task addTask(String title, String description, TaskStatus status) {
        Task task = new Task(nextId, title, description, status);
        tasks.put(nextId, task);
        nextId++;
        return task;
    }

    /**
     * Adds a new task whose status defaults to {@link TaskStatus#PENDING}.
     *
     * @param title       the task title; must not be blank
     * @param description the task description; may be {@code null} or empty
     * @return the newly created task with its generated ID
     * @throws IllegalArgumentException if the title is invalid
     */
    public Task addTask(String title, String description) {
        return addTask(title, description, TaskStatus.PENDING);
    }

    /**
     * Returns all tasks in the order they were added.
     *
     * @return a new list containing every task; empty if there are none
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Finds a task by its identifier.
     *
     * @param id the identifier to look up
     * @return the matching task
     * @throws TaskNotFoundException if no task has this identifier
     */
    public Task getTask(int id) throws TaskNotFoundException {
        Task task = tasks.get(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        return task;
    }

    /**
     * Updates the title, description and status of an existing task.
     * If validation fails the task is left unchanged.
     *
     * @param id          the identifier of the task to update
     * @param title       the new title; must not be blank
     * @param description the new description; may be {@code null} or empty
     * @param status      the new status; must not be {@code null}
     * @return the updated task
     * @throws TaskNotFoundException    if no task has this identifier
     * @throws IllegalArgumentException if the title or status is invalid
     */
    public Task updateTask(int id, String title, String description, TaskStatus status)
            throws TaskNotFoundException {
        Task task = getTask(id);
        task.update(title, description, status);
        return task;
    }

    /**
     * Changes only the status of an existing task.
     *
     * @param id     the identifier of the task
     * @param status the new status; must not be {@code null}
     * @return the updated task
     * @throws TaskNotFoundException    if no task has this identifier
     * @throws IllegalArgumentException if the status is {@code null}
     */
    public Task setStatus(int id, TaskStatus status) throws TaskNotFoundException {
        Task task = getTask(id);
        task.setStatus(status);
        return task;
    }

    /**
     * Deletes a task permanently.
     *
     * @param id the identifier of the task to delete
     * @return the task that was removed
     * @throws TaskNotFoundException if no task has this identifier
     */
    public Task deleteTask(int id) throws TaskNotFoundException {
        Task removed = tasks.remove(id);
        if (removed == null) {
            throw new TaskNotFoundException(id);
        }
        return removed;
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Tells whether there are no tasks.
     *
     * @return {@code true} if the list is empty, otherwise {@code false}
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Counts the tasks that currently have the given status.
     *
     * @param status the status to count
     * @return the number of tasks with that status
     */
    public int countByStatus(TaskStatus status) {
        int count = 0;
        for (Task task : tasks.values()) {
            if (task.getStatus() == status) {
                count++;
            }
        }
        return count;
    }
}
