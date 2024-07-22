package org.example;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class UserManager {
    private static final String DB_URL = "jdbc:sqlite:users.db";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private Set<String> usedPasswords;

    public UserManager() {
        usedPasswords = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY, " +
                        "password TEXT NOT NULL)";
                stmt.execute(sql);
                loadUsedPasswords(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsedPasswords(Connection conn) {
        String sql = "SELECT password FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usedPasswords.add(rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean signup(String username, String password) {
        if (!isEmailValid(username)) {
            System.out.println("The username must be a valid email address.");
            return false;
        }

        if (!isPasswordStrong(password)) {
            System.out.println("Your password is not strong enough. Please follow these guidelines:");
            System.out.println("1. At least 8 characters long");
            System.out.println("2. Includes both upper and lower case letters");
            System.out.println("3. Includes at least one digit");
            System.out.println("4. Includes at least one special character (e.g., !@#$%^&*)");
            return false;
        } else if (isPasswordUsed(password)) {
            System.out.println("Password has been used before. Try again.");
            return false;
        }

        return insertUser(username, password);
    }

    private boolean insertUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                usedPasswords.add(password);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean isPasswordStrong(String password) {
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    boolean isEmailValid(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public boolean isPasswordUsed(String password) {
        return usedPasswords.contains(password);
    }
}
