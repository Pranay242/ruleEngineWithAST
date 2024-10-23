package com.RuleEngineWithAST.repository;

import com.RuleEngineWithAST.ASTNode;

import java.util.List;

public interface RuleService {
    ASTNode createRule(String rule);

    ASTNode combineRules(List<String> rules);
}
