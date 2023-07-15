package id.rajaopak.gorgon.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import id.rajaopak.common.utils.Debug;
import id.rajaopak.gorgon.Gorgon;
import id.rajaopak.gorgon.enums.FilterState;
import id.rajaopak.gorgon.enums.HelpMeState;
import id.rajaopak.gorgon.object.HelpMeData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MySql implements Database {

    private final Gorgon plugin;
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String pass;
    private final boolean ssl;
    private final boolean certificateVerification;
    private final int poolSize;
    private final int maxLifetime;
    private HikariDataSource dataSource;

    public MySql(Gorgon plugin) {
        this.plugin = plugin;
        this.host = plugin.getConfigFile().getHost();
        this.port = plugin.getConfigFile().getPort();
        this.database = plugin.getConfigFile().getDatabaseName();
        this.user = plugin.getConfigFile().getUsername();
        this.pass = plugin.getConfigFile().getPassword();
        this.ssl = plugin.getConfigFile().isUseSSL();
        this.certificateVerification = plugin.getConfigFile().isCertificateVerification();
        this.poolSize = plugin.getConfigFile().getPoolSize();
        this.maxLifetime = plugin.getConfigFile().getMaxLifeTime();
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public boolean connect() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setPoolName("ChatLoggerMySQLPool");

        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);

        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);

        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification) {
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        }

        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");

        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

        dataSource = new HikariDataSource(hikariConfig);

        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            Debug.error("Error while trying to connect to database!", e, true);
            return false;
        }

        return true;
    }

    @Override
    public void initialize() {
        try (Connection connection = getConnection()) {
            String helpMeHistory = "CREATE TABLE IF NOT EXISTS helpme_history (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, helpme_uuid VARCHAR(200)," +
                    "sender_uuid VARCHAR(200), sender_name VARCHAR(200), staff_uuid VARCHAR(200), staff_name VARCHAR(200), " +
                    "state VARCHAR(200), server_name VARCHAR(200), accepted_time TIMESTAMP NULL DEFAULT NULL, message_time TIMESTAMP NULL DEFAULT NULL, message VARCHAR(512));";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(helpMeHistory);
            }

            String staffData = "CREATE TABLE IF NOT EXISTS staff_data (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(200), name VARCHAR(200), money BIGINT(255) NOT NULL DEFAULT 0, notify_gui BOOLEAN DEFAULT true);";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(staffData);
            }

        } catch (SQLException e) {
            Debug.error("Error while initialize database!", e, true);
        }
    }

    @Override
    public CompletableFuture<Void> setHelpMeData(HelpMeData data) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                String query = "INSERT INTO helpme_history " +
                        "(helpme_uuid, " +
                        "sender_uuid, " +
                        "sender_name, " +
                        "staff_uuid, " +
                        "staff_name, " +
                        "state, " +
                        "server_name, " +
                        "accepted_time, " +
                        "message_time, " +
                        "message) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?);";

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, data.getHelpMeUUID().toString());
                    statement.setString(2, data.getSenderUUID().toString());
                    statement.setString(3, data.getSenderName());
                    statement.setString(4, data.getStaffUUID().toString());
                    statement.setString(5, data.getStaffName());
                    statement.setString(6, data.getState().getStatus());
                    statement.setString(7, data.getServerName());
                    statement.setTimestamp(8, Timestamp.from(data.getAcceptedTime()));
                    statement.setTimestamp(9, Timestamp.from(data.getSendTime()));
                    statement.setString(10, data.getMessage());
                    statement.executeUpdate();
                }

            } catch (SQLException e) {
                Debug.error("Error while set data to database!", e, true);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<HelpMeData>> getHelpMeData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection()) {
                String query = "SELECT * FROM helpme_history WHERE helpme_uuid=?;";

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, uuid.toString());
                    try (ResultSet result = statement.executeQuery()) {
                        result.next();
                        return Optional.of(new HelpMeData.HelpMeDataBuilder(
                                UUID.fromString(result.getString("helpme_uuid")),
                                UUID.fromString(result.getString("sender_uuid")),
                                result.getString("sender_name"),
                                result.getString("message"),
                                result.getTimestamp("message_time"),
                                result.getString("server_name"),
                                UUID.fromString(result.getString("staff_uuid")),
                                result.getString("staff_name"),
                                result.getTimestamp("accepted_time"),
                                HelpMeState.fromString(result.getString("state"))
                        ).build());
                    }
                }
            } catch (SQLException e) {
                Debug.error("Error while gettering data from database!", e, true);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<List<HelpMeData>>> getHelpMeDataByPlayer(UUID playeruuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<HelpMeData> data = new ArrayList<>();

            try (Connection connection = getConnection()) {
                String query = "SELECT * FROM helpme_history WHERE sender_uuid=?;";

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, playeruuid.toString());
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            data.add(new HelpMeData.HelpMeDataBuilder(
                                    UUID.fromString(result.getString("helpme_uuid")),
                                    UUID.fromString(result.getString("sender_uuid")),
                                    result.getString("sender_name"),
                                    result.getString("message"),
                                    result.getTimestamp("message_time"),
                                    result.getString("server_name"),
                                    UUID.fromString(result.getString("staff_uuid")),
                                    result.getString("staff_name"),
                                    result.getTimestamp("accepted_time"),
                                    HelpMeState.fromString(result.getString("state"))
                            ).build());
                        }
                    }
                }
            } catch (SQLException e) {
                Debug.error("Error while gettering data from database!", e, true);
                return Optional.empty();
            }

            return Optional.of(data);
        });
    }

    @Override
    public CompletableFuture<Optional<List<HelpMeData>>> getHelpMeData(int limit, int offset, FilterState filter) {
        return CompletableFuture.supplyAsync(() -> {
            List<HelpMeData> data = new ArrayList<>();

            try (Connection connection = getConnection()) {
                if (filter == FilterState.ALL) {
                    String query = "SELECT * FROM helpme_history ORDER BY id LIMIT ? OFFSET ?;";

                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, limit);
                        statement.setInt(2, offset);
                        try (ResultSet result = statement.executeQuery()) {
                            while (result.next()) {
                                data.add(new HelpMeData.HelpMeDataBuilder(
                                        UUID.fromString(result.getString("helpme_uuid")),
                                        UUID.fromString(result.getString("sender_uuid")),
                                        result.getString("sender_name"),
                                        result.getString("message"),
                                        result.getTimestamp("message_time"),
                                        result.getString("server_name"),
                                        UUID.fromString(result.getString("staff_uuid")),
                                        result.getString("staff_name"),
                                        result.getTimestamp("accepted_time"),
                                        HelpMeState.fromString(result.getString("state"))
                                ).build());
                            }
                        }
                    }
                } else if (filter == FilterState.PLAYER) {
                    String query = "SELECT * FROM helpme_history WHERE sender_name=? ORDER BY id LIMIT ? OFFSET ?;";

                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setString(1, filter.getObject());
                        statement.setInt(2, limit);
                        statement.setInt(3, offset);
                        try (ResultSet result = statement.executeQuery()) {
                            while (result.next()) {
                                data.add(new HelpMeData.HelpMeDataBuilder(
                                        UUID.fromString(result.getString("helpme_uuid")),
                                        UUID.fromString(result.getString("sender_uuid")),
                                        result.getString("sender_name"),
                                        result.getString("message"),
                                        result.getTimestamp("message_time"),
                                        result.getString("server_name"),
                                        UUID.fromString(result.getString("staff_uuid")),
                                        result.getString("staff_name"),
                                        result.getTimestamp("accepted_time"),
                                        HelpMeState.fromString(result.getString("state"))
                                ).build());
                            }
                        }
                    }
                } else if (filter == FilterState.STATE) {
                    String query = "SELECT * FROM helpme_history WHERE state=? ORDER BY id LIMIT ? OFFSET ?;";

                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setString(1, filter.getObject());
                        statement.setInt(2, limit);
                        statement.setInt(3, offset);
                        try (ResultSet result = statement.executeQuery()) {
                            while (result.next()) {
                                data.add(new HelpMeData.HelpMeDataBuilder(
                                        UUID.fromString(result.getString("helpme_uuid")),
                                        UUID.fromString(result.getString("sender_uuid")),
                                        result.getString("sender_name"),
                                        result.getString("message"),
                                        result.getTimestamp("message_time"),
                                        result.getString("server_name"),
                                        UUID.fromString(result.getString("staff_uuid")),
                                        result.getString("staff_name"),
                                        result.getTimestamp("accepted_time"),
                                        HelpMeState.fromString(result.getString("state"))
                                ).build());
                            }
                        }
                    }
                } else {
                    return Optional.empty();
                }

            } catch (SQLException e) {
                Debug.error("Error while gettering data from database!", e, true);
                return Optional.empty();
            }

            return Optional.of(data);
        });
    }

    @Override
    public CompletableFuture<Void> updateHelpMeData(HelpMeData data) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = getConnection()) {
                if (hasHelpMe(data.getHelpMeUUID())) {
                    String query = "UPDATE helpme_history SET staff_uuid=?, staff_name=?, accepted_time=?, state=?;";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setString(1, data.getStaffUUID().toString());
                        statement.setString(2, data.getStaffName());
                        statement.setTimestamp(3, Timestamp.from(data.getAcceptedTime()));
                        statement.setString(4, data.getState().getStatus());
                    }
                } else {
                    setHelpMeData(data);
                }
            } catch (SQLException e) {
                Debug.error("Error while gettering data from database!", e, true);
            }
        });
    }

    @Override
    public boolean hasHelpMe(UUID uuid) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM helpme_history WHERE helpme_uuid=?;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    return result.next();
                }
            }
        } catch (SQLException e) {
            Debug.error("Error while gettering data from database!", e, true);
            return false;
        }
    }

    @Override
    public int sizeHelpMe(FilterState filter) {
        try (Connection connection = getConnection()) {
            if (filter == FilterState.ALL) {
                String query = "SELECT COUNT(*) AS row_count FROM helpme_history;";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    try (ResultSet result = statement.executeQuery()) {
                        result.next();
                        return result.getInt("row_count");
                    }
                }
            } else if (filter == FilterState.PLAYER) {
                String query = "SELECT COUNT(*) AS row_count FROM helpme_history WHERE sender_name=?;";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, filter.getObject());
                    try (ResultSet result = statement.executeQuery()) {
                        result.next();
                        return result.getInt("row_count");
                    }
                }
            } else if (filter == FilterState.STATE) {
                String query = "SELECT COUNT(*) AS row_count FROM helpme_history WHERE state=?;";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, filter.getObject());
                    try (ResultSet result = statement.executeQuery()) {
                        result.next();
                        return result.getInt("row_count");
                    }
                }
            }

        } catch (SQLException e) {
            Debug.error("Error while gettering data from database!", e, true);
            return 0;
        }
        return 0;
    }

    @Override
    public void close() {
        if (dataSource != null) {
            if (!dataSource.isClosed()) {
                dataSource.close();
            }
        }
    }
}
