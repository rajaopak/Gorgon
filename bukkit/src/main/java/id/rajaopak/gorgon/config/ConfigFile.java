package id.rajaopak.gorgon.config;

import id.rajaopak.common.config.CustomConfig;

public class ConfigFile extends CustomConfig {

    public ConfigFile(String configName, String directory) {
        super(configName, directory);
    }

    public boolean isDebug() {
        return getConfig().getBoolean("debug");
    }

    public String getServerName() {
        return getConfig().getString("server-name");
    }

    public String getHost() {
        return getConfig().getString("database.host");
    }

    public int getPort() {
        return getConfig().getInt("database.port");
    }

    public String getDatabaseName() {
        return getConfig().getString("database.database");
    }

    public String getUsername() {
        return getConfig().getString("database.username");
    }

    public String getPassword() {
        return getConfig().getString("database.password");
    }

    public boolean isUseSSL() {
        return getConfig().getBoolean("database.ssl");
    }

    public boolean isCertificateVerification() {
        return this.getConfig().getBoolean("database.verify-certificate", true);
    }

    public int getPoolSize() {
        return this.getConfig().getInt("database.pool-size", 30);
    }

    public int getMaxLifeTime() {
        return this.getConfig().getInt("database.max-lifetime", 1800000);
    }
}
