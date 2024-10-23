package com.RuleEngineWithAST.mongobean;

import com.RuleEngineWithAST.ASTNode;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.UUID;

@Data
@Document(collection = "RulesApp1")
public class RuleMetadata implements Serializable {
    private String ruleName;
    private String ruleAsStr;
    private String uniqueId = UUID.randomUUID().toString();
}
