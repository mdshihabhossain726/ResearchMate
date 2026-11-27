package com.researchmate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** All SQL for user accounts (register and login). */
public class UserDao {

    /**
     * Checks the registration form.
     * @return the username in clean form (trimmed, lower case)
     * @throws IllegalArgumentException with a message that can be shown to the user
     */
    public static String check(String fullName, String username, String password) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (fullName.trim().length() > 100) {
            throw new IllegalArgumentException("Full name is too long (maximum 100 characters).");
        }
        String name = username == null ? "" : username.trim().toLowerCase();
        if (!name.matches("[a-z0-9_.]{3,30}")) {
            throw new IllegalArgumentException("Username: 3-30 characters, only letters, numbers, . and _");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (password.length() > 100) {
            throw new IllegalArgumentException("Password is too long (maximum 100 characters).");
        }
        return name;
    }

    /** Creates the account and returns it, so the new user is logged in at once. */
    public User register(String fullName, String username, String password) throws SQLException {
        String name = check(fullName, username, password);
        String insert = "INSERT INTO users (username, full_name, password_hash) VALUES (?, ?, ?) RETURNING id";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(insert)) {
            ps.setString(1, name);
            ps.setString(2, fullName.trim());
            ps.setString(3, Passwords.hash(password));
            int id;
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                id = rs.getInt(1);
            }
            // Papers saved before accounts existed belong to the very first user.
            String adopt = "UPDATE papers SET user_id = ? WHERE user_id IS NULL AND (SELECT COUNT(*) FROM users) = 1";
            try (PreparedStatement a = c.prepareStatement(adopt)) {
                a.setInt(1, id);
                a.executeUpdate();
            }
            return new User(id, name, fullName.trim());
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {   // 23505 = duplicate value
                throw new IllegalArgumentException("This username is already taken.");
            }
            throw e;
        }
    }

    /** @return the user, or null when the username or the password is wrong */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id, username, full_name, password_hash FROM users WHERE username = ?";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && Passwords.verify(password, rs.getString("password_hash"))) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("full_name"));
                }
                return null;
            }
        }
    }
}
