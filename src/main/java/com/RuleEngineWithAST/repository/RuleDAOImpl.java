package com.RuleEngineWithAST.repository;


import com.RuleEngineWithAST.RuleParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.RuleEngineWithAST.mongobean.RuleMetadata;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Repository
@Component
public class RuleDAOImpl implements RuleDAO {

    @Autowired
    private Environment environment;

    private volatile MongoClientUtils mongoClientUtils;

    @Override
    public List<RuleMetadata> getRules() {
        setMongoObject();
        String dbName = environment.getProperty("mongo.db.name");
        List<RuleMetadata> rules = new ArrayList<>();

        MongoCollection<Document> collection = mongoClientUtils.getCollection(dbName, "RulesApp1");

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                RuleMetadata ruleMetadata = mapDocumentToRuleMetadata(doc);
                rules.add(ruleMetadata);
            }
        }
        return rules;
    }

    @Override
    public RuleMetadata insertOne(RuleMetadata ruleMetadata) {
        setMongoObject();
        String dbName = environment.getProperty("mongo.db.name");

        MongoCollection<Document> collection = mongoClientUtils.getCollection(dbName, "RulesApp1");
        Document doc = new Document("ruleName", ruleMetadata.getRuleName())
                .append("ruleAsStr", ruleMetadata.getRuleAsStr()).append("uniqueId", ruleMetadata.getUniqueId());
        collection.insertOne(doc);
        return ruleMetadata;
    }

    @Override
    public void deleteById(String uniqueId) {
        setMongoObject(); // Ensure MongoDB connection is set up
        String dbName = environment.getProperty("mongo.db.name");

        MongoCollection<Document> collection = mongoClientUtils.getCollection(dbName, "RulesApp1");

        // Create a filter to find the document by uniqueId
        Bson filter = Filters.eq("uniqueId", uniqueId);

        // Perform the deletion
        DeleteResult result = collection.deleteOne(filter);

        // Optionally handle the result
        if (result.getDeletedCount() == 0) {
            throw new RuntimeException("No rule found with uniqueId: " + uniqueId);
        }
    }


    private RuleMetadata mapDocumentToRuleMetadata(Document doc) {
        RuleMetadata ruleMetadata = new RuleMetadata();
        ruleMetadata.setRuleAsStr(doc.getString("ruleAsStr"));
        ruleMetadata.setRuleName(doc.getString("ruleName"));
        ruleMetadata.setUniqueId(doc.getString("uniqueId"));
        return ruleMetadata;
    }

    private synchronized void setMongoObject() {
        if(this.mongoClientUtils == null) {
            synchronized (new Object()) {
                String url = environment.getProperty("mongo.url");
                int port = environment.getProperty("mongo.port", Integer.class);
                String userName = environment.getProperty("mongo.username");
                String password = environment.getProperty("mongo.password");

                mongoClientUtils = new MongoClientUtils(userName, password, url, port);
            }
        }
    }
}
