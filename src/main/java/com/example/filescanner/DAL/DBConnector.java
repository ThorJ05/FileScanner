    package com.example.filescanner.DAL;

    import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
    import com.microsoft.sqlserver.jdbc.SQLServerException;

    import java.io.IOException;
    import java.io.InputStream;
    import java.sql.Connection;
    import java.util.Properties;

    public class DBConnector {

        private static SQLServerDataSource dataSource;

        // Prevent instantiation
        private DBConnector() {
        }

        private static void initialize() throws IOException {
            if (dataSource != null) {
                return;
            }

            Properties props = new Properties();

            try (InputStream is = DBConnector.class
                    .getClassLoader()
                    .getResourceAsStream("config.settings")) {

                if (is == null) {
                    throw new IOException("config.settings not found in src/main/resources");
                }

                props.load(is);
            }

            dataSource = new SQLServerDataSource();
            dataSource.setServerName(props.getProperty("Server"));
            dataSource.setDatabaseName(props.getProperty("Database"));
            dataSource.setUser(props.getProperty("User"));
            dataSource.setPassword(props.getProperty("Password"));
            dataSource.setPortNumber(
                    Integer.parseInt(props.getProperty("Port", "1433"))
            );
            dataSource.setTrustServerCertificate(true);
        }

        public static Connection getConnection()
                throws SQLServerException, IOException {

            initialize();
            return dataSource.getConnection();
        }

        /**
         * Tests whether a database connection can be opened and closed.
         */
        public static boolean testConnection() {
            try (Connection con = getConnection()) {
                return con != null && !con.isClosed();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Optional manual test entry point.
         * Can be removed after verification.
         */
        public static void main(String[] args) {
            System.out.println("Testing database connection...");
            System.out.println("Connected: " + testConnection());
        }
    }