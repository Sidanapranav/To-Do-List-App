import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * This is the main (public) class with the GUI. You only need one public class in a .java file.
 */
public class ToDoListOneFile extends JFrame {

    private TaskManager taskManager;           // Manages our tasks (add, remove, mark completed, etc.)
    private DefaultListModel<Task> listModel;  // Feeds data to the JList for display
    private JList<Task> taskList;              // The UI component that shows tasks

    private JComboBox<String> filterComboBox;  // Filter dropdown: All, Completed, Pending

    // Input fields for adding new tasks:
    private JTextField titleField;       
    private JTextField dueDateField;    
    private JTextArea descriptionArea;  

    /**
     * Constructor sets up the window, loads tasks, and refreshes the list initially.
     */
    public ToDoListOneFile() {
        super("To-Do List with Mark & Remove");
        taskManager = new TaskManager(); 
        initUI();
        refreshTaskList("All");  // Start by showing all tasks
    }

    /**
     * Sets up the user interface (buttons, fields, layout).
     */
    private void initUI() {
        // Basic window stuff
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);  // Center window

        // Main panel (BorderLayout)
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // --------------------------------
        // TOP PANEL: Filter & Clear Completed
        // --------------------------------
        JPanel topPanel = new JPanel();
        filterComboBox = new JComboBox<>(new String[] {"All", "Completed", "Pending"});
        filterComboBox.addActionListener(e -> {
            // Whenever user picks a filter (All, Completed, Pending), refresh.
            String filter = (String) filterComboBox.getSelectedItem();
            refreshTaskList(filter);
        });

        JButton clearCompletedBtn = new JButton("Clear Completed");
        clearCompletedBtn.addActionListener(e -> {
            // Remove every task that is marked completed
            taskManager.clearCompletedTasks();
            refreshTaskList((String) filterComboBox.getSelectedItem());
        });

        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterComboBox);
        topPanel.add(clearCompletedBtn);

        // --------------------------------
        // CENTER PANEL: List of Tasks
        // --------------------------------
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(taskList);

        // --------------------------------
        // BOTTOM PANEL: Add/Edit/Remove
        // --------------------------------
        JPanel bottomPanel = new JPanel();

        // Title (TextField)
        titleField = new JTextField(12);
        bottomPanel.add(new JLabel("Title:"));
        bottomPanel.add(titleField);

            
        // Due Date (TextField)
        dueDateField = new JTextField(10);
        bottomPanel.add(new JLabel("Due (YYYY-MM-DD):"));
        bottomPanel.add(dueDateField);

        // Remove Task Button
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> removeTask());
        bottomPanel.add(removeButton);

// Mark Completed Button
        JButton markCompletedButton = new JButton("Mark Completed");
        markCompletedButton.addActionListener(e -> markCompleted());
        bottomPanel.add(markCompletedButton);


        // Description (TextArea in a scroll pane)
        descriptionArea = new JTextArea(3, 15);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        bottomPanel.add(new JLabel("Description:"));
        bottomPanel.add(descriptionScroll);

        // Add Task Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addTask());
        bottomPanel.add(addButton);

        // Remove Task Button
        

        // Mark Completed
        JButton completeButton = new JButton("Mark Completed");
        completeButton.addActionListener(e -> markCompleted());
        bottomPanel.add(completeButton);

        // Mark Pending
        JButton pendingButton = new JButton("Mark Pending");
        pendingButton.addActionListener(e -> markPending());
        bottomPanel.add(pendingButton);

        // Edit Button (allows changing title, due date, description)
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editTask());
        bottomPanel.add(editButton);

        // Add sub-panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Save tasks automatically when window closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                taskManager.saveTasks();
                super.windowClosing(e);
            }
        });
    }

    /**
     * Refresh the list in the UI according to the filter (All, Completed, Pending).
     */
    private void refreshTaskList(String filter) {
        listModel.clear();  // Clear out old list
        for (Task task : taskManager.getFilteredTasks(filter)) {
            listModel.addElement(task);
        }
    }

    /**
     * Add a new task using what's typed into the fields.
     */
    private void addTask() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        // Title must not be empty
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task title cannot be empty!");
            return;
        }

        // Parse the date if provided
        LocalDate dueDate = null;
        if (!dueDateField.getText().trim().isEmpty()) {
            try {
                dueDate = LocalDate.parse(dueDateField.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }
        }

        // Add the new task to TaskManager
        taskManager.addTask(title, dueDate, description);

        // Clear fields
        titleField.setText("");
        dueDateField.setText("");
        descriptionArea.setText("");

        // Refresh the list
        refreshTaskList((String) filterComboBox.getSelectedItem());
    }

    /**
     * Remove the selected task entirely from the list.
     */
    private void removeTask() {
        Task selected = taskList.getSelectedValue();
        if (selected != null) {
            // Remove from the TaskManager
            taskManager.removeTask(selected);
            // Refresh the display
            refreshTaskList((String) filterComboBox.getSelectedItem());
        } else {
            JOptionPane.showMessageDialog(this, "No task selected to remove.");
        }
    }

    /**
     * Mark the selected task as completed.
     */
    private void markCompleted() {
        Task selected = taskList.getSelectedValue();
        if (selected != null) {
            taskManager.markTaskCompleted(selected);
            refreshTaskList((String) filterComboBox.getSelectedItem());
        } else {
            JOptionPane.showMessageDialog(this, "No task selected.");
        }
    }

    /**
     * Mark the selected task as pending (not completed).
     */
    private void markPending() {
        Task selected = taskList.getSelectedValue();
        if (selected != null) {
            taskManager.markTaskPending(selected);
            refreshTaskList((String) filterComboBox.getSelectedItem());
        } else {
            JOptionPane.showMessageDialog(this, "No task selected.");
        }
    }

    /**
     * Edit the selected task's title, due date, or description.
     */
    private void editTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No task selected to edit.");
            return;
        }

        // Edit Title
        String newTitle = JOptionPane.showInputDialog(
            this, "Edit Title:", selected.getTitle()
        );
        if (newTitle == null || newTitle.trim().isEmpty()) {
            return; // if user canceled or typed nothing, do nothing
        }

        // Edit Due Date
        String currentDueDate = (selected.getDueDate() == null) ? "" : selected.getDueDate().toString();
        String newDueDateStr = JOptionPane.showInputDialog(
            this, "Edit Due Date (YYYY-MM-DD):", currentDueDate
        );
        LocalDate newDueDate = null;
        if (newDueDateStr != null && !newDueDateStr.trim().isEmpty()) {
            try {
                newDueDate = LocalDate.parse(newDueDateStr.trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date. Edit canceled.");
                return;
            }
        }

        // Edit Description
        String newDesc = JOptionPane.showInputDialog(
            this, "Edit Description:", selected.getDescription()
        );
        if (newDesc == null) {
            return; // user canceled
        }

        // Update the selected task with new data
        selected.setTitle(newTitle.trim());
        selected.setDueDate(newDueDate);
        selected.setDescription(newDesc.trim());

        // Refresh
        refreshTaskList((String) filterComboBox.getSelectedItem());
    }

    /**
     * Main method to start the program.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ToDoListOneFile().setVisible(true);
        });
    }
}

/**
 * Represents a single Task with title, due date, description, and completion status.
 * Not public because we already have one public class: ToDoListOneFile.
 */
class Task implements Serializable {
    private String title;
    private LocalDate dueDate;
    private String description;
    private boolean completed;

    public Task(String title, LocalDate dueDate, String description) {
        this.title = title;
        this.dueDate = dueDate;
        this.description = description;
        this.completed = false; 
    }

    // Getters and setters:
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        String status = completed ? "[Completed]" : "[Pending]";
        String dateText = (dueDate != null) ? dueDate.toString() : "No due date";
        return String.format("%s %s (Due: %s)", status, title, dateText);
    }
}

/**
 * Manages the list of tasks: add, remove, mark completed, etc.
 * Also handles saving and loading tasks from a file.
 */
class TaskManager {
    private ArrayList<Task> tasks;
    private final String DATA_FILE = "tasks_mark_remove.ser";

    public TaskManager() {
        tasks = new ArrayList<>();
        loadTasks();  // Load from file if available
    }

    public void addTask(String title, LocalDate dueDate, String description) {
        tasks.add(new Task(title, dueDate, description));
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public void clearCompletedTasks() {
        // Remove every Task where isCompleted() == true
        tasks.removeIf(Task::isCompleted);
    }

    public void markTaskCompleted(Task task) {
        task.setCompleted(true);
    }

    public void markTaskPending(Task task) {
        task.setCompleted(false);
    }

    public ArrayList<Task> getFilteredTasks(String filter) {
        ArrayList<Task> filtered = new ArrayList<>();
        for (Task t : tasks) {
            switch (filter) {
                case "All":
                    filtered.add(t);
                    break;
                case "Completed":
                    if (t.isCompleted()) filtered.add(t);
                    break;
                case "Pending":
                    if (!t.isCompleted()) filtered.add(t);
                    break;
            }
        }
        return filtered;
    }

    /**
     * Save tasks to file using serialization.
     */
    public void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load tasks from file if it exists.
     */
    @SuppressWarnings("unchecked")
    public void loadTasks() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return; // No data file yet
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            tasks = (ArrayList<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
