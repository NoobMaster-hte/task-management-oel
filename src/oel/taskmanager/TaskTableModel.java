package oel.taskmanager;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Swing table model that presents a list of {@link Task} objects in four
 * read-only columns: ID, Title, Description and Status.
 *
 * @author Muhammad Hassaan Rashid
 */
public class TaskTableModel extends AbstractTableModel {

    /** Serialization version identifier. */
    private static final long serialVersionUID = 1L;
    /** Column header names, in display order. */
    private static final String[] COLUMNS = {"ID", "Title", "Description", "Status"};

    /** The tasks currently displayed, one per row. */
    private transient List<Task> tasks = new ArrayList<>();

    /**
     * Creates an empty table model.
     */
    public TaskTableModel() {
        // Starts empty; call setTasks to fill it.
    }

    /**
     * Replaces the displayed tasks and notifies the table to repaint.
     *
     * @param newTasks the tasks to display; {@code null} is treated as empty
     */
    public void setTasks(List<Task> newTasks) {
        this.tasks = newTasks == null ? new ArrayList<>() : new ArrayList<>(newTasks);
        fireTableDataChanged();
    }

    /**
     * Returns the task displayed in the given row.
     *
     * @param row the zero-based row index
     * @return the task in that row, or {@code null} if the row is out of range
     */
    public Task getTaskAt(int row) {
        if (row < 0 || row >= tasks.size()) {
            return null;
        }
        return tasks.get(row);
    }

    /**
     * Finds the row index of the task with the given ID.
     *
     * @param id the task identifier
     * @return the zero-based row index, or -1 if the task is not displayed
     */
    public int indexOfTask(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the number of rows.
     *
     * @return the number of displayed tasks
     */
    @Override
    public int getRowCount() {
        return tasks.size();
    }

    /**
     * Returns the number of columns.
     *
     * @return always 4
     */
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /**
     * Returns the header text of a column.
     *
     * @param column the zero-based column index
     * @return the column name
     */
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /**
     * Returns the value shown in a cell.
     *
     * @param rowIndex    the zero-based row index
     * @param columnIndex the zero-based column index
     * @return the ID, title, description or status of the task in that row
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return task.getId();
            case 1:
                return task.getTitle();
            case 2:
                return task.getDescription();
            case 3:
                return task.getStatus();
            default:
                return "";
        }
    }

    /**
     * Returns the class of a column so that Swing aligns the cells correctly.
     *
     * @param columnIndex the zero-based column index
     * @return {@code Integer.class} for the ID column, otherwise {@code Object.class}
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Integer.class : Object.class;
    }

    /**
     * Cells are read-only; editing is done through the form.
     *
     * @param rowIndex    the zero-based row index
     * @param columnIndex the zero-based column index
     * @return always {@code false}
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
