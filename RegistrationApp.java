import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationApp {

    // Database Connection
    private static final String DB_URL = "jdbc:mysql://localhost:3306/registrationdb"; // Replace with your database URL
    private static final String DB_USER = "root"; // Replace with your database username
    private static final String DB_PASSWORD = ""; // Replace with your database password

    private JFrame registrationFrame;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationApp().createRegistrationForm());
    }

    // Registration Form
    private void createRegistrationForm() {
        registrationFrame = new JFrame("Registration Form");
        registrationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registrationFrame.setSize(400, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 5, 5));

        // Fields
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel mobileLabel = new JLabel("Mobile:");
        JTextField mobileField = new JTextField();
        JLabel genderLabel = new JLabel("Gender:");
        JRadioButton maleButton = new JRadioButton("Male");
        JRadioButton femaleButton = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        JLabel dobLabel = new JLabel("DOB:");
        JTextField dobField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField();
        JCheckBox termsCheck = new JCheckBox("Accept Terms and Conditions");

        // Buttons
        JButton submitButton = new JButton("Submit");
        JButton resetButton = new JButton("Reset");
        JButton viewUsersButton = new JButton("View Users");

        // Adding components to panel
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(mobileLabel);
        panel.add(mobileField);
        panel.add(genderLabel);
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        panel.add(genderPanel);
        panel.add(dobLabel);
        panel.add(dobField);
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(termsCheck);
        panel.add(new JLabel()); // Empty space
        panel.add(submitButton);
        panel.add(resetButton);
        panel.add(new JLabel()); // Empty space
        panel.add(viewUsersButton);

        registrationFrame.add(panel);
        registrationFrame.setVisible(true);

        // Button Actions
        submitButton.addActionListener(e -> {
            if (termsCheck.isSelected()) {
                String gender = maleButton.isSelected() ? "Male" : (femaleButton.isSelected() ? "Female" : "N/A");
                addToDatabase(nameField.getText(), mobileField.getText(), gender, dobField.getText(), addressField.getText());
                JOptionPane.showMessageDialog(registrationFrame, "User Registered Successfully!");
            } else {
                JOptionPane.showMessageDialog(registrationFrame, "You must accept the terms and conditions.");
            }
        });

        resetButton.addActionListener(e -> {
            nameField.setText("");
            mobileField.setText("");
            genderGroup.clearSelection();
            dobField.setText("");
            addressField.setText("");
            termsCheck.setSelected(false);
        });

        viewUsersButton.addActionListener(e -> showDataFrame());
    }

    // Display Data in Second Frame
    private void showDataFrame() {
        JFrame dataFrame = new JFrame("Registered Users");
        dataFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dataFrame.setSize(500, 300);

        String[] columnNames = {"ID", "Name", "Gender", "Address", "Contact"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        // Fetch Data from Database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String gender = resultSet.getString("gender");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("mobile");
                tableModel.addRow(new Object[]{id, name, gender, address, contact});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dataFrame, "Error fetching data from database: " + e.getMessage());
        }

        // Exit Button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            dataFrame.dispose();
            registrationFrame.setVisible(true);
        });

        // Adding Components
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(exitButton);

        dataFrame.add(new JScrollPane(table), BorderLayout.CENTER);
        dataFrame.add(buttonPanel, BorderLayout.SOUTH);
        dataFrame.setVisible(true);

        registrationFrame.setVisible(false);
    }

    // Insert User into Database
    private void addToDatabase(String name, String mobile, String gender, String dob, String address) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users (name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)")) {

            statement.setString(1, name);
            statement.setString(2, mobile);
            statement.setString(3, gender);
            statement.setString(4, dob);
            statement.setString(5, address);
            statement.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(registrationFrame, "Error inserting data into database: " + e.getMessage());
        }
    }
}

