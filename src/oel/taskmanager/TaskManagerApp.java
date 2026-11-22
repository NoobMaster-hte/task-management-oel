package oel.taskmanager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Main window of the Task Management System (Swing GUI).
 * The window contains an input form (title, description, status), buttons for
 * Add / Update / Delete / Clear, quick buttons for changing the status of the
 * selected task, and a {@link JTable} listing all tasks. When there are no
 * tasks, the table is replaced by a clear "no tasks" message.
 * All business rules live in {@link TaskManager}; this class only handles
 * user interaction and shows validation and error messages.
 *
 * @author Muhammad Hassaan Rashid
 */
public final class TaskManagerApp extends JFrame {

    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    /** Card name for the task table view. */
    private static final String CARD_TABLE = "table";
    /** Card name for the empty-state message view. */
    private static final String CARD_EMPTY = "empty";

    /** Service object that holds the tasks and business rules. */
    private final transient TaskManager manager = new TaskManager();
    /** Table model that feeds the task table. */
    private final TaskTableModel tableModel = new TaskTableModel();
    /** Table that lists all tasks. */
    private final JTable table = new JTable(tableModel);

    /** Input field for the task title. */
    private final JTextField titleField = new JTextField(30);
    /** Input area for the task description. */
    private final JTextArea descriptionArea = new JTextArea(4, 30);
    /** Drop-down for choosing the task status. */
    private final JComboBox<TaskStatus> statusCombo = new JComboBox<>(TaskStatus.values());

    /** Label showing the latest message and task counts. */
    private final JLabel summaryLabel = new JLabel(" ");
    /** Layout that switches between table and empty message. */
    private final CardLayout cardLayout = new CardLayout();
    /** Panel holding the table card and the empty-state card. */
    private final JPanel listCards = new JPanel(cardLayout);

    /**
     * Builds the main window, wires all event handlers and shows the empty state.
     */
    public TaskManagerApp() {
        super("Task Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildListPanel(), BorderLayout.CENTER);
        add(buildSummaryPanel(), BorderLayout.SOUTH);

        refreshTable(null);
        setMinimumSize(new Dimension(760, 560));
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Builds the form panel containing the input fields and action buttons.
     *
     * @return the fully assembled form panel
     */
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Task Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title *"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(titleField, gbc);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Description"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Status"), gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

        JButton addButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton clearButton = new JButton("Clear Form");
        addButton.addActionListener(e -> onAdd());
        updateButton.addActionListener(e -> onUpdate());
        deleteButton.addActionListener(e -> onDelete());
        clearButton.addActionListener(e -> onClear());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttons, gbc);

        return panel;
    }

    /**
     * Builds the list panel: quick-status buttons above a card area that shows
     * either the task table or the empty-state message.
     *
     * @return the fully assembled list panel
     */
    private JPanel buildListPanel() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(340);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedTaskIntoForm();
            }
        });

        JLabel emptyLabel = new JLabel(
                "No tasks to display. Fill in the form above and click \"Add Task\".",
                SwingConstants.CENTER);
        emptyLabel.setFont(emptyLabel.getFont().deriveFont(Font.ITALIC, 14f));
        emptyLabel.setForeground(Color.GRAY);

        listCards.add(new JScrollPane(table), CARD_TABLE);
        listCards.add(emptyLabel, CARD_EMPTY);

        JPanel statusButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusButtons.add(new JLabel("Set status of selected task:"));
        for (TaskStatus status : TaskStatus.values()) {
            JButton button = new JButton(status.getLabel());
            button.addActionListener(e -> onSetStatus(status));
            statusButtons.add(button);
        }

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("All Tasks"));
        panel.add(statusButtons, BorderLayout.NORTH);
        panel.add(listCards, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(760, 280));
        return panel;
    }

    /**
     * Builds the bottom panel that shows the latest message and task counts.
     *
     * @return the summary panel
     */
    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(summaryLabel, BorderLayout.WEST);
        return panel;
    }

    /**
     * Returns the task currently selected in the table.
     *
     * @return the selected task, or {@code null} if no row is selected
     */
    private Task getSelectedTask() {
        return tableModel.getTaskAt(table.getSelectedRow());
    }

    /**
     * Copies the selected task's values into the form fields.
     * Does nothing when no row is selected.
     */
    private void loadSelectedTaskIntoForm() {
        Task task = getSelectedTask();
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            statusCombo.setSelectedItem(task.getStatus());
        }
    }

    /**
     * Reloads the table from the manager, chooses the correct card
     * (table or empty message), selects a row if requested and updates the summary.
     *
     * @param selectId the ID of the task to select afterwards, or {@code null} for none
     */
    private void refreshTable(Integer selectId) {
        tableModel.setTasks(manager.getAllTasks());
        cardLayout.show(listCards, manager.isEmpty() ? CARD_EMPTY : CARD_TABLE);
        table.clearSelection();
        if (selectId != null) {
            int row = tableModel.indexOfTask(selectId);
            if (row >= 0) {
                table.setRowSelectionInterval(row, row);
            }
        }
        showSummary(null);
    }

    /**
     * Shows an optional message followed by the current task counts.
     *
     * @param message the message to show first, or {@code null} for counts only
     */
    private void showSummary(String message) {
        String counts = "Total: " + manager.size()
                + "  |  Pending: " + manager.countByStatus(TaskStatus.PENDING)
                + "  |  In Progress: " + manager.countByStatus(TaskStatus.IN_PROGRESS)
                + "  |  Completed: " + manager.countByStatus(TaskStatus.COMPLETED);
        summaryLabel.setText(message == null ? counts : message + "   (" + counts + ")");
    }

    /**
     * Clears all form fields and resets the status to Pending.
     */
    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        statusCombo.setSelectedItem(TaskStatus.PENDING);
    }

    /**
     * Handles the "Add Task" button: validates input and adds a new task.
     */
    private void onAdd() {
        try {
            Task task = manager.addTask(titleField.getText(), descriptionArea.getText(),
                    (TaskStatus) statusCombo.getSelectedItem());
            clearForm();
            refreshTable(null);
            showSummary("Task #" + task.getId() + " added.");
        } catch (IllegalArgumentException ex) {
            showValidationError(ex.getMessage());
        }
    }

    /**
     * Handles the "Update Selected" button: applies the form values to the
     * selected task, handling "nothing selected" and "task no longer exists".
     */
    private void onUpdate() {
        Task selected = getSelectedTask();
        if (selected == null) {
            showWarning("Please select a task in the table before updating.");
            return;
        }
        try {
            manager.updateTask(selected.getId(), titleField.getText(),
                    descriptionArea.getText(), (TaskStatus) statusCombo.getSelectedItem());
            refreshTable(selected.getId());
            showSummary("Task #" + selected.getId() + " updated.");
        } catch (TaskNotFoundException ex) {
            handleMissingTask(ex);
        } catch (IllegalArgumentException ex) {
            showValidationError(ex.getMessage());
        }
    }

    /**
     * Handles the "Delete Selected" button after asking for confirmation.
     */
    private void onDelete() {
        Task selected = getSelectedTask();
        if (selected == null) {
            showWarning("Please select a task in the table before deleting.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete task #" + selected.getId() + " \"" + selected.getTitle() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            manager.deleteTask(selected.getId());
            clearForm();
            refreshTable(null);
            showSummary("Task #" + selected.getId() + " deleted.");
        } catch (TaskNotFoundException ex) {
            handleMissingTask(ex);
        }
    }

    /**
     * Handles the quick status buttons: changes only the status of the selected task.
     *
     * @param status the status to apply to the selected task
     */
    private void onSetStatus(TaskStatus status) {
        Task selected = getSelectedTask();
        if (selected == null) {
            showWarning("Please select a task in the table before changing its status.");
            return;
        }
        try {
            manager.setStatus(selected.getId(), status);
            refreshTable(selected.getId());
            showSummary("Task #" + selected.getId() + " set to " + status.getLabel() + ".");
        } catch (TaskNotFoundException ex) {
            handleMissingTask(ex);
        }
    }

    /**
     * Handles the "Clear Form" button: empties the form and the table selection.
     */
    private void onClear() {
        table.clearSelection();
        clearForm();
        titleField.requestFocusInWindow();
    }

    /**
     * Informs the user that a task no longer exists and refreshes the table.
     *
     * @param ex the exception describing the missing task
     */
    private void handleMissingTask(TaskNotFoundException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage()
                + "\nThe task list will be refreshed.", "Task Not Found",
                JOptionPane.ERROR_MESSAGE);
        refreshTable(null);
    }

    /**
     * Shows a validation error dialog and puts the cursor back in the title field.
     *
     * @param message the validation message to display
     */
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error",
                JOptionPane.ERROR_MESSAGE);
        titleField.requestFocusInWindow();
    }

    /**
     * Shows a warning dialog for an invalid action such as "nothing selected".
     *
     * @param message the warning text to display
     */
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "No Task Selected",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Table cell renderer that colours the status text:
     * grey for Pending, orange for In Progress and green for Completed.
     *
     * @author Muhammad Hassaan Rashid
     */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {

        /** Serialization version identifier. */
        private static final long serialVersionUID = 1L;

        /**
         * Creates the renderer.
         */
        StatusCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        /**
         * Prepares the cell component, colouring the text by status.
         *
         * @param table      the table being drawn
         * @param value      the cell value (a {@link TaskStatus})
         * @param isSelected whether the row is selected
         * @param hasFocus   whether the cell has focus
         * @param row        the row index
         * @param column     the column index
         * @return the component used to draw the cell
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            if (!isSelected && value instanceof TaskStatus) {
                switch ((TaskStatus) value) {
                    case PENDING:
                        c.setForeground(new Color(110, 110, 110));
                        break;
                    case IN_PROGRESS:
                        c.setForeground(new Color(200, 110, 0));
                        break;
                    default:
                        c.setForeground(new Color(0, 140, 60));
                        break;
                }
            }
            return c;
        }
    }

    /**
     * Application entry point. Starts the GUI on the Swing event-dispatch thread.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Falls back to the default Swing look and feel.
            }
            new TaskManagerApp().setVisible(true);
        });
    }
}
