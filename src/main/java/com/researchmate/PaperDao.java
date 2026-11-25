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

    public void add(Paper p) throws SQLException {
        String sql = "INSERT INTO papers (title, authors, year, content) VALUES (?, ?, ?, ?)";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getAuthors());
            ps.setInt(3, p.getYear());
            ps.setString(4, p.getContent());
            ps.executeUpdate();
        }
    }

    /** Changes title, authors and year (the text of the paper stays as it is). */
    public void update(Paper p) throws SQLException {
        String sql = "UPDATE papers SET title = ?, authors = ?, year = ? WHERE id = ?";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getAuthors());
            ps.setInt(3, p.getYear());
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM papers WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Paper> findAll() throws SQLException {
        return search("");
    }

    /** Papers whose title or authors contain the text (upper/lower case does not matter). */
    public List<Paper> search(String text) throws SQLException {
        String sql = "SELECT id, title, authors, year FROM papers "
                + "WHERE LOWER(title) LIKE ? OR LOWER(authors) LIKE ? ORDER BY id DESC";
        String like = "%" + text.trim().toLowerCase() + "%";
        List<Paper> result = new ArrayList<>();
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
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
             PreparedStatement ps = c.prepareStatement("SELECT content FROM papers WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString(1) != null) {
                    return rs.getString(1);
                }
                return "";
            }
        }
    }
}
