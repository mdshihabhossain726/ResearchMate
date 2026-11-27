package com.researchmate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO = Data Access Object. All SQL for papers is here, nowhere else.
 * Every query uses PreparedStatement with "?" so user input can never break the SQL (SQL injection safe).
 */
public class PaperDao {

    private final int userId;   // every user sees only his or her own papers

    public PaperDao(int userId) {
        this.userId = userId;
    }

    public void add(Paper p) throws SQLException {
        String sql = "INSERT INTO papers (title, authors, year, content, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getAuthors());
            ps.setInt(3, p.getYear());
            ps.setString(4, p.getContent());
            ps.setInt(5, userId);
            ps.executeUpdate();
        }
    }

    /** Changes title, authors and year (the text of the paper stays as it is). */
    public void update(Paper p) throws SQLException {
        String sql = "UPDATE papers SET title = ?, authors = ?, year = ? WHERE id = ? AND user_id = ?";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getAuthors());
            ps.setInt(3, p.getYear());
            ps.setInt(4, p.getId());
            ps.setInt(5, userId);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM papers WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public List<Paper> findAll() throws SQLException {
        return search("");
    }

    /** Papers whose title or authors contain the text (upper/lower case does not matter). */
    public List<Paper> search(String text) throws SQLException {
        String sql = "SELECT id, title, authors, year FROM papers "
                + "WHERE user_id = ? AND (LOWER(title) LIKE ? OR LOWER(authors) LIKE ?) ORDER BY id DESC";
        String like = "%" + text.trim().toLowerCase() + "%";
        List<Paper> result = new ArrayList<>();
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Paper(rs.getInt("id"), rs.getString("title"),
                            rs.getString("authors"), rs.getInt("year")));
                }
            }
        }
        return result;
    }

    /** The text of one paper ("" if it has none). */
    public String getContent(int id) throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement("SELECT content FROM papers WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString(1) != null) {
                    return rs.getString(1);
                }
                return "";
            }
        }
    }
}
