import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ToDoListSwing {
    private ArrayList<Task> tasks = new ArrayList<>();
    private JPanel taskPanel = new JPanel();
    private DefaultListModel<String> completedListModel = new DefaultListModel<>();
    private JList<String> completedList = new JList<>(completedListModel);
    private JTextField taskInput = new JTextField(20);
    private JTextField dueDateInput = new JTextField(10);
    private JButton addButton = new JButton("Add Task");
    private JButton notifyButton = new JButton("Notify");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListSwing::new);
    }

    public ToDoListSwing() {
        JFrame frame = new JFrame("To-Do List App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(500, 600);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 1));
        inputPanel.add(new JLabel("Task:"));
        inputPanel.add(taskInput);
        inputPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        inputPanel.add(dueDateInput);
        inputPanel.add(addButton);
        inputPanel.add(notifyButton);

        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        JScrollPane taskScrollPane = new JScrollPane(taskPanel);

        inputPanel.add(taskScrollPane);
        inputPanel.add(new JLabel("Completed Tasks:"));
        inputPanel.add(new JScrollPane(completedList));

        addButton.addActionListener(this::addTask);
        notifyButton.addActionListener(this::notifyUpcomingTasks);

        frame.add(inputPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // Set up a timer for periodic notifications
        Timer timer = new Timer(60000, e -> notifyUpcomingTasks(null)); // Check every minute
        timer.start();
    }

    private void addTask(ActionEvent e) {
        String taskDescription = taskInput.getText();
        String dueDateStr = dueDateInput.getText();
        if (!taskDescription.isEmpty() && isValidDate(dueDateStr)) {
            LocalDate dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            Task task = new Task(taskDescription, dueDate);
            tasks.add(task);
            JCheckBox taskCheckBox = new JCheckBox(task.toString());
            taskCheckBox.addActionListener(this::taskCheckBoxChanged);
            taskPanel.add(taskCheckBox);
            taskPanel.revalidate();
            taskPanel.repaint();
            taskInput.setText("");
            dueDateInput.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid input. Please ensure task and due date are correctly entered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void taskCheckBoxChanged(ActionEvent e) {
        JCheckBox checkBox = (JCheckBox) e.getSource();
        String taskStr = checkBox.getText();
        Task task = findTaskByDescription(taskStr);
        if (task != null) {
            if (checkBox.isSelected()) {
                tasks.remove(task);
                completedListModel.addElement(task.toString());
                taskPanel.remove(checkBox);
                taskPanel.revalidate();
                taskPanel.repaint();
            }
        }
    }

    private void notifyUpcomingTasks(ActionEvent e) {
        LocalDate today = LocalDate.now();
        StringBuilder message = new StringBuilder("Upcoming tasks:\n");
        for (Task task : tasks) {
            if (task.getDueDate().isEqual(today) || task.getDueDate().isBefore(today.plusDays(1))) {
                message.append(task).append("\n");
            }
        }
        if (message.length() > "Upcoming tasks:\n".length()) {
            JOptionPane.showMessageDialog(null, message.toString(), "Task Reminder", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Task findTaskByDescription(String description) {
        for (Task task : tasks) {
            if (task.toString().equals(description)) {
                return task;
            }
        }
        return null;
    }

    private static class Task {
        private String description;
        private LocalDate dueDate;

        public Task(String description, LocalDate dueDate) {
            this.description = description;
            this.dueDate = dueDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        @Override
        public String toString() {
            return description + " (Due: " + dueDate + ")";
        }
    }
}
