package oel.taskmanager;

import java.util.List;

/**
 * Plain-Java test runner (no external libraries) that exercises the
 * {@link TaskManager} service against the OEL test cases and prints
 * PASS or FAIL with the actual result of each case.
 *
 * @author Muhammad Hassaan Rashid
 */
public class TaskManagerTest {

    private static int passed = 0;
    private static int failed = 0;

    /**
     * Creates the test runner (not instantiated; see {@link #main(String[])}).
     */
    private TaskManagerTest() {
        // Utility class.
    }

    /**
     * Records and prints the outcome of one test case.
     *
     * @param id       the test case identifier, e.g. "TC-01"
     * @param name     a short description of the test case
     * @param ok       whether the actual result matched the expected result
     * @param expected the expected result text
     * @param actual   the actual result text
     */
    private static void check(String id, String name, boolean ok, String expected, String actual) {
        if (ok) {
            passed++;
        } else {
            failed++;
        }
        System.out.println((ok ? "PASS " : "FAIL ") + id + " | " + name);
        System.out.println("       expected: " + expected);
        System.out.println("       actual  : " + actual);
    }

    /**
     * Runs every test case and prints a summary.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        TaskManager m = new TaskManager();

        // TC-01 Add a valid task
        Task t1 = m.addTask("Buy groceries", "Milk, eggs, bread", TaskStatus.PENDING);
        check("TC-01", "Add a valid task",
                m.size() == 1 && t1.getId() == 1 && t1.getStatus() == TaskStatus.PENDING,
                "Task added with ID 1, status Pending, list size 1",
                t1 + ", size=" + m.size());

        // TC-02 Empty / whitespace-only title
        String msg2 = null;
        try {
            m.addTask("   ", "no title", TaskStatus.PENDING);
        } catch (IllegalArgumentException ex) {
            msg2 = ex.getMessage();
        }
        check("TC-02", "Add a task with an empty (whitespace) title",
                msg2 != null && m.size() == 1,
                "Validation message shown, nothing added (size stays 1)",
                "message=\"" + msg2 + "\", size=" + m.size());

        String msg2b = null;
        try {
            m.addTask(null, "null title", TaskStatus.PENDING);
        } catch (IllegalArgumentException ex) {
            msg2b = ex.getMessage();
        }
        check("TC-02b", "Add a task with a null title",
                msg2b != null && m.size() == 1,
                "Validation message shown, nothing added",
                "message=\"" + msg2b + "\", size=" + m.size());

        // TC-03 View all tasks
        m.addTask("Finish OEL report", "Add screenshots", TaskStatus.IN_PROGRESS);
        m.addTask("Push to GitHub", "", TaskStatus.COMPLETED);
        List<Task> all = m.getAllTasks();
        check("TC-03", "View all tasks",
                all.size() == 3 && all.get(0).getId() == 1 && all.get(2).getId() == 3,
                "3 tasks returned in insertion order",
                all.toString());

        // TC-04 View an empty list
        TaskManager empty = new TaskManager();
        check("TC-04", "View an empty list",
                empty.isEmpty() && empty.getAllTasks().isEmpty(),
                "Empty list; GUI shows 'No tasks to display' message",
                "isEmpty=" + empty.isEmpty() + ", tasks=" + empty.getAllTasks());

        // TC-05 Update an existing task
        String actual5;
        boolean ok5;
        try {
            Task u = m.updateTask(1, "Buy groceries and fruit", "Milk, eggs, apples",
                    TaskStatus.IN_PROGRESS);
            ok5 = u.getTitle().equals("Buy groceries and fruit")
                    && u.getDescription().equals("Milk, eggs, apples")
                    && u.getStatus() == TaskStatus.IN_PROGRESS;
            actual5 = u + " / " + u.getDescription();
        } catch (TaskNotFoundException ex) {
            ok5 = false;
            actual5 = "unexpected: " + ex.getMessage();
        }
        check("TC-05", "Update an existing task", ok5,
                "Title, description and status updated", actual5);

        // TC-05b Update with invalid title leaves task unchanged
        String before = m.getAllTasks().get(0).toString();
        String msg5b = null;
        try {
            m.updateTask(1, "", "changed", TaskStatus.COMPLETED);
        } catch (TaskNotFoundException ex) {
            msg5b = "unexpected: " + ex.getMessage();
        } catch (IllegalArgumentException ex) {
            msg5b = ex.getMessage();
        }
        String after = m.getAllTasks().get(0).toString();
        check("TC-05b", "Update existing task with blank title",
                msg5b != null && before.equals(after)
                        && m.getAllTasks().get(0).getDescription().equals("Milk, eggs, apples"),
                "Validation message; task left unchanged",
                "message=\"" + msg5b + "\", task=" + after);

        // TC-06 Update a non-existing task
        String msg6 = null;
        try {
            m.updateTask(999, "Ghost", "does not exist", TaskStatus.PENDING);
        } catch (TaskNotFoundException ex) {
            msg6 = ex.getMessage();
        }
        check("TC-06", "Update a non-existing task",
                msg6 != null,
                "TaskNotFoundException; handled gracefully (error dialog, no crash)",
                "message=\"" + msg6 + "\"");

        // TC-07 Delete an existing task
        int sizeBefore = m.size();
        String actual7;
        boolean ok7;
        try {
            Task removed = m.deleteTask(2);
            ok7 = m.size() == sizeBefore - 1 && removed.getId() == 2;
            actual7 = "removed " + removed + ", size " + sizeBefore + " -> " + m.size();
        } catch (TaskNotFoundException ex) {
            ok7 = false;
            actual7 = "unexpected: " + ex.getMessage();
        }
        check("TC-07", "Delete an existing task", ok7,
                "Task removed; list size decreases by 1", actual7);

        // TC-08 Delete a non-existing task
        String msg8 = null;
        try {
            m.deleteTask(2);
        } catch (TaskNotFoundException ex) {
            msg8 = ex.getMessage();
        }
        check("TC-08", "Delete an already deleted task",
                msg8 != null,
                "TaskNotFoundException; handled gracefully",
                "message=\"" + msg8 + "\"");

        // TC-09 Set status
        String actual9;
        boolean ok9;
        try {
            Task s = m.setStatus(3, TaskStatus.PENDING);
            ok9 = s.getStatus() == TaskStatus.PENDING;
            actual9 = s.toString();
        } catch (TaskNotFoundException ex) {
            ok9 = false;
            actual9 = "unexpected: " + ex.getMessage();
        }
        check("TC-09", "Change status of a task", ok9,
                "Task 3 status becomes Pending", actual9);

        // TC-10 IDs are never reused
        Task t4 = m.addTask("After delete", "ID must be new", TaskStatus.PENDING);
        check("TC-10", "IDs are not reused after deletion",
                t4.getId() == 4,
                "New task gets ID 4 (not the deleted ID 2)",
                t4.toString());

        // TC-11 Title longer than the limit
        String longTitle = "x".repeat(Task.MAX_TITLE_LENGTH + 1);
        String msg11 = null;
        try {
            m.addTask(longTitle, "", TaskStatus.PENDING);
        } catch (IllegalArgumentException ex) {
            msg11 = ex.getMessage();
        }
        check("TC-11", "Add a task with a title over " + Task.MAX_TITLE_LENGTH + " characters",
                msg11 != null,
                "Validation message shown",
                "message=\"" + msg11 + "\"");

        System.out.println();
        System.out.println("Summary: " + passed + " passed, " + failed + " failed.");
        if (failed > 0) {
            System.exit(1);
        }
    }
}
