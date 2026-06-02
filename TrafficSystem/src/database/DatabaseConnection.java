package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/traffic_system";
    private static final String USER     = "root";  
    private static final String PASSWORD = "";        

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTables() {
        try (Connection conn = getConnection();
             var stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id       INT AUTO_INCREMENT PRIMARY KEY,
                    name     VARCHAR(100) NOT NULL,
                    email    VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    role     VARCHAR(20)  NOT NULL CHECK (role IN ('user', 'admin'))
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS incidents (
                    id            INT AUTO_INCREMENT PRIMARY KEY,
                    type          VARCHAR(50)  NOT NULL,
                    location      VARCHAR(200) NOT NULL,
                    severity      VARCHAR(20)  NOT NULL,
                    description   TEXT,
                    status        VARCHAR(20)  DEFAULT 'Open',
                    reported_by   INT,
                    reporter_name VARCHAR(100) DEFAULT 'Anonymous',
                    reported_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
                    latitude      DOUBLE,
                    longitude     DOUBLE,
                    FOREIGN KEY (reported_by) REFERENCES users(id)
                )
            """);

            System.out.println("Tables created successfully! (users, incidents)");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}
