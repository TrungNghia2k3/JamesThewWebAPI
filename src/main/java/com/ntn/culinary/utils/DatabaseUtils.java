package com.ntn.culinary.utils;

// import io.github.cdimascio.dotenv.Dotenv; // Xóa dòng này

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {
    private static final String JDBC_URL;
    private static final String JDBC_USER;
    private static final String JDBC_PASS;

    static {
        if (isTestEnvironment()) {
            // H2
            JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
            JDBC_USER = "sa";
            JDBC_PASS = "";
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to load H2 driver", e);
            }
        } else {
            // MySQL
            // Dotenv dotenv = Dotenv.load(); // Xóa hoặc comment dòng này
            JDBC_URL = System.getenv("DB_URL");       // Sửa thành System.getenv()
            JDBC_USER = System.getenv("DB_USERNAME"); // Sửa thành System.getenv()
            JDBC_PASS = System.getenv("DB_PASSWORD"); // Sửa thành System.getenv()

            if (JDBC_URL == null || JDBC_USER == null || JDBC_PASS == null) {
                throw new RuntimeException("Database environment variables (DB_URL, DB_USERNAME, DB_PASSWORD) are not set.");
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to load MySQL driver", e);
            }
        }
    }

    private static boolean isTestEnvironment() {
        // Ví dụ đơn giản: kiểm tra system property
        return "true".equals(System.getProperty("TEST_ENV"));
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }
}