package com.RuleEngineWithAST;

import com.RuleEngineWithAST.bean.Condition;
import com.RuleEngineWithAST.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ASTNode {

    public ASTNode(ASTNode left, ASTNode right, Operator operator, NodeType nodeType, Condition condition) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.nodeType = nodeType;
        this.condition = condition;
    }

    private Operator operator;
    private ASTNode left;
    private ASTNode right;

    private Condition condition;
    private NodeType nodeType;


    public enum Operator {
        AND,
        OR
    }

    public enum NodeType {
        VALUE,
        EXPRESSION
    }


    public boolean evaluateExpression(User user) {
        if(nodeType == NodeType.VALUE) {
            return this.condition.evaluate(user);
        } else {
            boolean leftResult = left.evaluateExpression(user);
            if(operator == Operator.AND && leftResult == false) {
                return false;
            }
            if(operator == Operator.OR && leftResult == true) {
                return true;
            }
            boolean rightResult = right.evaluateExpression(user);

            switch (operator) {
                case AND:
                    return leftResult && rightResult;
                case OR:
                    return leftResult || rightResult;
                default:
                    throw new UnsupportedOperationException("Unsupported operator: " + operator);
            }
        }
    }


}
