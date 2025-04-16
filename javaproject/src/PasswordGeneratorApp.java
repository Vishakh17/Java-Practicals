import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;

public class PasswordGeneratorApp {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?";
    private static final String USER_DATA_FILE = "user_data.txt";
    private static final Map<String, String> userDatabase = new HashMap<>();

    public static void main(String[] args) {
        loadUserData();
        showLoginPage();
    }

    private static void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    userDatabase.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing user data found. Starting fresh.");
        }
    }

    private static void saveUserData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE))) {
            for (Map.Entry<String, String> entry : userDatabase.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving user data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showLoginPage() {
        JFrame loginFrame = new JFrame("Login / Signup");
        loginFrame.setSize(300, 250);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridLayout(5, 2, 10, 10));

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");
        JButton forgotPasswordButton = new JButton("Forgot Password");

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                JOptionPane.showMessageDialog(loginFrame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loginFrame.dispose();
                showPasswordGeneratorPage();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        signupButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (userDatabase.containsKey(username)) {
                JOptionPane.showMessageDialog(loginFrame, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                userDatabase.put(username, password);
                saveUserData();
                JOptionPane.showMessageDialog(loginFrame, "Signup successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        forgotPasswordButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(loginFrame, "Enter your username:");
            if (username != null && userDatabase.containsKey(username)) {
                String newPassword = JOptionPane.showInputDialog(loginFrame, "Enter your new password:");
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    userDatabase.put(username, newPassword.trim());
                    saveUserData();
                    JOptionPane.showMessageDialog(loginFrame, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Username not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginFrame.add(new JLabel("Username:"));
        loginFrame.add(usernameField);
        loginFrame.add(new JLabel("Password:"));
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);
        loginFrame.add(signupButton);
        loginFrame.add(forgotPasswordButton);

        loginFrame.setVisible(true);
    }

    private static void showPasswordGeneratorPage() {
        JFrame frame = new JFrame("Random Password Generator");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField passwordField = new JTextField(25);
        passwordField.setEditable(false);
        JTextField lengthField = new JTextField(5);
        JButton generateButton = new JButton("Generate Password");
        JButton logoutButton = new JButton("Log Out");

        generateButton.addActionListener(e -> {
            try {
                int length = Integer.parseInt(lengthField.getText());
                if (length <= 0) {
                    JOptionPane.showMessageDialog(frame, "Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String password = generateRandomPassword(length);
                    passwordField.setText(password);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            showLoginPage();
        });

        frame.add(new JLabel("Desired Password Length:"));
        frame.add(lengthField);
        frame.add(new JLabel("Generated Password:"));
        frame.add(passwordField);
        frame.add(generateButton);
        frame.add(logoutButton);

        frame.setVisible(true);
    }

    private static String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
