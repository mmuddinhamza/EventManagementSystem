import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event extends JFrame {
    private JPanel mainPanel;
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField timeField;
    private JTextField durationField;
    private JDateChooser dateChooser;
    private JButton addRecordButton;
    private JButton updateRecordButton;
    private JTable eventTable;
    private JScrollPane tableScrollPane;

    private final String DB_URL = "jdbc:mysql://localhost:3306/eventdb";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "password";

    public Event() {
        setTitle("Event Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);

        loadTableData();

        addRecordButton.addActionListener(this::addRecord);
        updateRecordButton.addActionListener(this::updateRecord);

        eventTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = eventTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) eventTable.getModel();
                nameField.setText(model.getValueAt(selectedRow, 0).toString());
                descriptionField.setText(model.getValueAt(selectedRow, 1).toString());
                timeField.setText(model.getValueAt(selectedRow, 2).toString());
                durationField.setText(model.getValueAt(selectedRow, 3).toString());
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(model.getValueAt(selectedRow, 4).toString());
                    dateChooser.setDate(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addRecord(ActionEvent event) {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String time = timeField.getText();
        String duration = durationField.getText();
        Date date = dateChooser.getDate();

        if (name.isEmpty() || description.isEmpty() || time.isEmpty() || duration.isEmpty() || date == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields to add record.");
            return;
        }

        String query = "INSERT INTO event (name, description, time, duration, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, time);
            preparedStatement.setString(4, duration);
            preparedStatement.setDate(5, new java.sql.Date(date.getTime()));

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Event added successfully.");
            clearFields();
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRecord(ActionEvent event) {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String time = timeField.getText();
        String duration = durationField.getText();
        Date date = dateChooser.getDate();

        if (name.isEmpty() || description.isEmpty() || time.isEmpty() || duration.isEmpty() || date == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields to update record.");
            return;
        }

        String query = "UPDATE event SET description = ?, time = ?, duration = ?, date = ? WHERE name = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, description);
            preparedStatement.setString(2, time);
            preparedStatement.setString(3, duration);
            preparedStatement.setDate(4, new java.sql.Date(date.getTime()));
            preparedStatement.setString(5, name);

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Event updated successfully.");
            clearFields();
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        String query = "SELECT * FROM event";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"Name", "Description", "Time", "Duration", "Date"}, 0);
            while (resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("time"),
                        resultSet.getString("duration"),
                        resultSet.getDate("date")
                });
            }
            eventTable.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.setText("");
        descriptionField.setText("");
        timeField.setText("");
        durationField.setText("");
        dateChooser.setDate(null);
    }
}
