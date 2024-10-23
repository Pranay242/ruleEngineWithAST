package com.RuleEngineWithAST.repository;

import com.RuleEngineWithAST.mongobean.RuleMetadata;

import java.util.List;

public interface RuleDAO {
    List<RuleMetadata> getRules();

    RuleMetadata insertOne(RuleMetadata ruleMetadata);

    void deleteById(String uniqueId);
}
