package lib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    // Cấu hình kết nối Aiven Cloud MySQL mặc định (có thể ghi đè qua biến môi
    // trường)
    private static final String DEFAULT_HOST = "mysql-22176-buivietbacn01-1ff7.a.aivencloud.com";
    private static final String DEFAULT_PORT = "10055";
    private static final String DEFAULT_DB = "affiliate-book";
    private static final String DEFAULT_USER = "avnadmin";
    // Mật khẩu được bảo mật và truyền qua biến môi trường DB_PASSWORD trên Render
    private static final String DEFAULT_PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("[MySQLConnection] MySQL Driver not found: " + ex.getMessage());
            }
        }
    }

    /**
     * Lấy chuỗi JDBC URL hoàn chỉnh dựa trên biến môi trường hoặc cấu hình mặc định
     */
    private static String getJdbcUrl() {
        // 1. Kiểm tra nếu có DB_URL hoặc DATABASE_URL đầy đủ
        String envUrl = System.getenv("DB_URL");
        if (envUrl == null || envUrl.trim().isEmpty()) {
            envUrl = System.getenv("DATABASE_URL");
        }

        if (envUrl != null && !envUrl.trim().isEmpty()) {
            String url = envUrl.trim();
            // Nếu là URL dạng mysql://... chuyển sang jdbc:mysql://...
            if (url.startsWith("mysql://")) {
                url = "jdbc:" + url;
            }
            if (!url.contains("sslMode=") && !url.contains("ssl-mode=")) {
                url += (url.contains("?") ? "&" : "?") + "sslMode=REQUIRED&allowPublicKeyRetrieval=true";
            }
            return url;
        }

        // 2. Ghép từ các biến môi trường rời rạc
        String host = getEnvOrDefault("DB_HOST", DEFAULT_HOST);
        String port = getEnvOrDefault("DB_PORT", DEFAULT_PORT);
        String dbName = getEnvOrDefault("DB_NAME", DEFAULT_DB);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("jdbc:mysql://").append(host).append(":").append(port).append("/").append(dbName);

        // Nếu kết nối tới Cloud (Aiven / không phải localhost), bắt buộc bật SSL
        if (!host.equalsIgnoreCase("localhost") && !host.equals("127.0.0.1")) {
            urlBuilder.append(
                    "?useSSL=true&sslMode=REQUIRED&allowPublicKeyRetrieval=true&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh");
        } else {
            urlBuilder.append("?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&characterEncoding=UTF-8");
        }

        return urlBuilder.toString();
    }

    private static String getUsername() {
        return getEnvOrDefault("DB_USER", DEFAULT_USER);
    }

    private static String getPassword() {
        return getEnvOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    /**
     * Tạo một kết nối vật lý thực tế tới database
     */
    private static Connection createPhysicalConnection() throws SQLException {
        String url = getJdbcUrl();
        String user = getUsername();
        String pass = getPassword();

        System.out.println("[MySQLConnection] Connecting to DB at: " + url.replaceAll(":[^:@]+@", ":****@"));
        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("[MySQLConnection] Database connected successfully!");
            return conn;
        } catch (SQLException e) {
            System.err.println("[MySQLConnection] Connection failed: " + e.getMessage());
            throw e;
        }
    }

    public static Connection getConnection() {
        return (Connection) Proxy.newProxyInstance(
                MySQLConnection.class.getClassLoader(),
                new Class<?>[] { Connection.class },
                new ReconnectingConnectionHandler());
    }

    private static class ReconnectingConnectionHandler implements InvocationHandler {
        private Connection realConnection;

        private synchronized Connection getValidConnection() throws SQLException {
            if (realConnection == null || realConnection.isClosed() || !isHealthy(realConnection)) {
                closeQuietly(realConnection);
                realConnection = createPhysicalConnection();
            }
            return realConnection;
        }

        private boolean isHealthy(Connection conn) {
            try {
                return conn.isValid(2);
            } catch (Exception e) {
                return false;
            }
        }

        private void closeQuietly(Connection conn) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ignored) {
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Không đóng physical connection khi proxy.close() được gọi từ ngoài
            if ("close".equals(method.getName())) {
                return null;
            }
            if ("isClosed".equals(method.getName())) {
                return realConnection != null && realConnection.isClosed();
            }

            Connection conn = getValidConnection();
            try {
                return method.invoke(conn, args);
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                // Nếu kết nối bị rớt giữa chừng, thử kết nối lại 1 lần
                if (cause instanceof SQLException) {
                    System.err.println("[MySQLConnection] Warning: Stale connection detected, attempting reconnect: "
                            + cause.getMessage());
                    try {
                        closeQuietly(realConnection);
                        realConnection = createPhysicalConnection();
                        return method.invoke(realConnection, args);
                    } catch (Exception ex) {
                        throw cause;
                    }
                }
                throw cause;
            }
        }
    }
}
