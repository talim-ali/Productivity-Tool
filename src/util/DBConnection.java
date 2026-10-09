package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties properties = new Properties();

    static {
        try (FileInputStream input =
                     new FileInputStream("src/util/db.properties")) {

            properties.load(input);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Could not load database configuration: "
                            + e.getMessage()
            );
        }
    }

    public static Connection getConnection() throws SQLException {

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException(
                    "Database configuration is incomplete."
            );
        }

        return DriverManager.getConnection(url, user, password);
    }
}