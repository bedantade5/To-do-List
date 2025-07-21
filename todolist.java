import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// Class representing a task in the to-do list
class Task {
    String title, description, priority;
    boolean isCompleted;

    public Task(String title, String description, String priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.isCompleted = false; // Initially set task as not completed
    }

    // Returns a string representation of the task
    public String toString() {
        return title + " - " + description + " - " + priority + " - " + (isCompleted ? "Completed" : "Pending");
    }
}

public class todolist extends JFrame {
    private ArrayList<Task> tasks = new ArrayList<>(); // List to hold tasks
    private DefaultListModel<String> listModel = new DefaultListModel<>(); // Model for the task list
    private JList<String> taskList = new JList<>(listModel); // JList to display tasks

    private JTextField titleField = new JTextField(15);
    private JTextField descField = new JTextField(15);
    private JComboBox<String> priorityBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});
    private JButton addTaskButton = new JButton("Add Task");
    private JButton editTaskButton = new JButton("Edit Task");
    private JButton deleteTaskButton = new JButton("Delete Task");
    private JButton markCompleteButton = new JButton("Mark Complete");
    private JTextField searchField = new JTextField(10);
    private JComboBox<String> filterPriorityBox = new JComboBox<>(new String[]{"All", "Low", "Medium", "High"});
    private CardLayout cardLayout = new CardLayout(); // For switching between panels
    private JPanel mainPanel = new JPanel(cardLayout); // Main panel holding all components

    public todolist() {
        setTitle("To-Do List Manager");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for user login
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Adding username and password fields
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginPanel.add(loginButton, gbc);

        // Authenticate user on login
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (authenticate(username, password)) {
                setSize(600, 400);
                cardLayout.show(mainPanel, "TaskManager"); // Show the task manager on successful login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(loginPanel, "Login");

        JPanel taskManagerPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> filterTasks()); // Filter tasks based on search input
        topPanel.add(searchButton);

        topPanel.add(new JLabel("Filter by Priority:"));
        filterPriorityBox.addActionListener(e -> filterTasksByPriority()); // Update task list based on priority filter
        topPanel.add(filterPriorityBox);

        taskManagerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Tasks"), BorderLayout.NORTH);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single selection of tasks
        JScrollPane scrollPane = new JScrollPane(taskList);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        taskManagerPanel.add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(new JLabel("Title:"));
        rightPanel.add(titleField);
        rightPanel.add(new JLabel("Description:"));
        rightPanel.add(descField);
        rightPanel.add(new JLabel("Priority:"));
        rightPanel.add(priorityBox);

        // Add action listeners for task management buttons
        addTaskButton.addActionListener(e -> addTask());
        editTaskButton.addActionListener(e -> editTask());
        deleteTaskButton.addActionListener(e -> deleteTask());
        markCompleteButton.addActionListener(e -> markTaskComplete());

        rightPanel.add(addTaskButton);
        rightPanel.add(editTaskButton);
        rightPanel.add(deleteTaskButton);
        rightPanel.add(markCompleteButton);
        taskManagerPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(taskManagerPanel, "TaskManager");
        add(mainPanel);
        cardLayout.show(mainPanel, "Login");

        setVisible(true); // Make the frame visible
    }

    private boolean authenticate(String username, String password) {
        return username.equals("user") && password.equals("password"); // Simple authentication
    }

    private void addTask() {
        String title = titleField.getText().trim();
        String description = descField.getText().trim();
        String priority = (String) priorityBox.getSelectedItem();

        if (!title.isEmpty()) {
            Task task = new Task(title, description, priority);
            tasks.add(task); // Add new task to the list
            updateTaskList(); // Refresh the displayed task list
            clearFields(); // Clear input fields
        }
    }

    private void editTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = tasks.get(selectedIndex);
            task.title = titleField.getText();
            task.description = descField.getText();
            task.priority = (String) priorityBox.getSelectedItem();
            updateTaskList(); // Refresh task list after editing
            clearFields(); // Clear input fields
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            tasks.remove(selectedIndex); // Remove selected task
            updateTaskList(); // Refresh the displayed task list
            clearFields(); // Clear input fields
        }
    }

    private void markTaskComplete() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = tasks.get(selectedIndex);
            task.isCompleted = true; // Mark the task as completed
            updateTaskList(); // Refresh the task list to show updated status
        }
    }

    private void filterTasks() {
        String searchText = searchField.getText().toLowerCase();
        listModel.clear(); // Clear current display
        for (Task task : tasks) { 
            // Check for title/description match and priority filter
            if ((task.title.toLowerCase().contains(searchText) || task.description.toLowerCase().contains(searchText)) 
                && (filterPriorityBox.getSelectedItem().equals("All") || task.priority.equals(filterPriorityBox.getSelectedItem()))) {
                listModel.addElement(task.toString()); // Add matching task to the display
            }
        }
    }

    private void filterTasksByPriority() {
        filterTasks(); // Reapply filtering logic
    }

    private void updateTaskList() {
        listModel.clear(); // Clear the current display
        for (Task task : tasks) {
            listModel.addElement(task.toString()); // Add each task to the display
        }
    }

    private void clearFields() {
        titleField.setText(""); // Clear the title field
        descField.setText(""); // Clear the description field
        priorityBox.setSelectedIndex(0); // Reset priority selection
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(todolist::new); // Create an instance of the todolist class
    }
}
