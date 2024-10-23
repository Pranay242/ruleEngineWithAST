package com.RuleEngineWithAST.repository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

public class MongoClientUtils {

    private MongoClient mongoClient;

    public MongoClientUtils(String username, String password, String host, int port) {
        String connectionString;
        if (StringUtils.isNotEmpty(password)) {
            connectionString = String.format("mongodb://%s:%s@%s:%d/", username, password, host, port);
        } else {
            connectionString = String.format("mongodb://%s:%d/", host, port);
        }

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        mongoClient = MongoClients.create(settings);
    }

    public MongoCollection<Document> getCollection(String databaseName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection(collectionName);
    }

    public void close() {
        mongoClient.close();
    }

}
