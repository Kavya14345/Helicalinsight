package com.helicalinsight.datasource.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.MongoClientSettings;
import org.bson.Document;
import java.util.Collections;
import java.util.Map;

public class MongoDbConnectionProvider {

    public static MongoClient createConnection(Map<String, String> config) {
        Map<String, String> options = config == null ? Collections.emptyMap() : config;
        String database = firstValue(options, "databaseName", "database");
        String uri = firstValue(options, "uri", "jdbcUrl");
        if (uri == null || uri.trim().isEmpty()) {
            String host = options.getOrDefault("host", "localhost");
            String port = options.getOrDefault("port", "27017");
            uri = String.format("mongodb://%s:%s/%s", host, port, database == null ? "" : database);
        }

        MongoClientSettings.Builder settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri));
        String username = firstValue(options, "username", "userName");
        String password = options.get("password");
        if (username != null && !username.trim().isEmpty()) {
            String authenticationDatabase = options.getOrDefault("authSource", database);
            settings.credential(MongoCredential.createCredential(
                    username, authenticationDatabase == null ? "admin" : authenticationDatabase,
                    password == null ? new char[0] : password.toCharArray()));
        }
        return MongoClients.create(settings.build());
    }

    public static boolean testConnection(Map<String, String> config) {
        try (MongoClient client = createConnection(config)) {
            String databaseName = firstValue(config, "databaseName", "database");
            if (databaseName == null || databaseName.trim().isEmpty()) {
                return false;
            }
            MongoDatabase db = client.getDatabase(databaseName);
            Document ping = db.runCommand(new Document("ping", 1));
            Object result = ping.get("ok");
            return result instanceof Number && ((Number) result).doubleValue() == 1.0;
        } catch (Exception e) {
            return false;
        }
    }

    private static String firstValue(Map<String, String> config, String... keys) {
        if (config == null) {
            return null;
        }
        for (String key : keys) {
            String value = config.get(key);
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
    }
}