package com.researchmate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Everything about the PostgreSQL connection:
 *  - reads the settings from db.properties (the password is NOT written in the code),
 *  - opens connections,
 *  - creates the database and the table on the first start.
 */
public class Db {

    private static String url = "jdbc:postgresql://localhost:5432/researchmate";
    private static String user = "postgres";
    private static String password = "";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS papers ("
            + " id      SERIAL PRIMARY KEY,"
            + " title   VARCHAR(500)  NOT NULL,"
            + " authors VARCHAR(1000) NOT NULL,"
            + " year    INTEGER       NOT NULL CHECK (year BETWEEN 1900 AND 2100),"
            + " content TEXT,"
            + " UNIQUE (title, year))";   // the same title + year twice is rejected (no duplicates)
    /** Reads db.properties if it exists in the folder where the program is started. */
    public static void loadSettings() {
        File file = new File("db.properties");
        if (!file.exists()) {
            return;
        }
        try (FileInputStream in = new FileInputStream(file)) {
            Properties p = new Properties();
            p.load(in);
            url = p.getProperty("db.url", url);
            user = p.getProperty("db.user", user);
            password = p.getProperty("db.password", password);
        } catch (IOException e) {
            System.out.println("Could not read db.properties: " + e.getMessage());
        }
    }

    /** Opens a new connection. Always use it inside try-with-resources so it is closed again. */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** Creates the table (and the database, if it does not exist yet). */
    public static void setup() throws SQLException {
        try (Connection c = connect()) {
            createTable(c);
        } catch (SQLException e) {
            if (!"3D000".equals(e.getSQLState())) {   // 3D000 = "database does not exist"
                throw e;
            }
            createDatabase();
            try (Connection c = connect()) {
                createTable(c);
            }
        }
    }

    private static void createTable(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.executeUpdate(CREATE_TABLE);
        }
    }

    /** Connects to the built-in "postgres" database and creates our database. */
    private static void createDatabase() throws SQLException {
        int slash = url.lastIndexOf('/');
        int question = url.indexOf('?', slash);
        String end = question < 0 ? "" : url.substring(question);
        String name = url.substring(slash + 1, question < 0 ? url.length() : question);
        if (!name.matches("[A-Za-z0-9_]+")) {   // a database name cannot be a ? parameter, so check it carefully
            throw new SQLException("Please create the database \"" + name + "\" yourself.");
        }
        String adminUrl = url.substring(0, slash + 1) + "postgres" + end;
        try (Connection c = DriverManager.getConnection(adminUrl, user, password);
             Statement st = c.createStatement()) {
            st.executeUpdate("CREATE DATABASE " + name);
        }
    }
}
