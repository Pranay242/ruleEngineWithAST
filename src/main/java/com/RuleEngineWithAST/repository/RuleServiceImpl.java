package com.RuleEngineWithAST.repository;

import com.RuleEngineWithAST.ASTNode;
import com.RuleEngineWithAST.RuleParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleServiceImpl implements RuleService {
    @Override
    public ASTNode createRule(String rule) {
        if(StringUtils.isEmpty(rule)) {
            throw new RuntimeException("Rule is empty");
        }
        return RuleParser.parseRule(rule);
    }

    @Override
    public ASTNode combineRules(List<String> rules) {
        ASTNode parentNode = null;
        for (String rule : rules) {
            ASTNode astNode = createRule(rule);
            parentNode = joinASTNodeByAnd(parentNode, astNode);
        }
        return parentNode;
    }

    private ASTNode joinASTNodeByAnd(ASTNode parentNode, ASTNode node) {
        if(parentNode == null) {
            return node;
        }
        return new ASTNode(parentNode, node, ASTNode.Operator.AND, ASTNode.NodeType.EXPRESSION, null);
    }
}
