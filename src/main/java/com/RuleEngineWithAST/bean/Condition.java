package com.RuleEngineWithAST.bean;

import com.RuleEngineWithAST.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
public class Condition {
    private String lhs;
    private Evaluator evaluator;
    private DataType dataType;
    private Object rhs;


    public enum Evaluator {
        EQ,
        GT,
        GTE,
        LT,
        LTE,
        NE
    }

    public enum DataType {
        NUM,
        STRING
    }

    public boolean evaluate(User user) {
        Object valueToCompare = null;
        switch (this.lhs) {
            case "age" :
                valueToCompare = user.getAge();
                break;
            case "salary":
                valueToCompare = user.getSalary();
                break;
            case "experience" :
                valueToCompare = user.getExperience();
                break;
            case "department" :
                valueToCompare = user.getDepartment();
                break;
            default:
                throw new RuntimeException("Invalid LHS in rule");
        }

        if(dataType == DataType.NUM) {
            return evaluateForNum(this.evaluator, valueToCompare, this.rhs);
        } else if (dataType == DataType.STRING) {
            return evaluateForString(this.evaluator, valueToCompare, this.rhs);
        } else {
            throw new RuntimeException("Invalid datatype");
        }

    }

    private boolean evaluateForString(Evaluator evaluator, Object lhs, Object rhs) {
        if(!(lhs instanceof String))  {
            throw new RuntimeException("Wrong lhs");
        }
        if (!(rhs instanceof String)) {
            throw new RuntimeException("Wrong RHS");
        }
        switch (evaluator) {
            case EQ :
                return StringUtils.equals((String) lhs, (String) rhs);
            case NE:
                return !StringUtils.equals((String) lhs, (String) rhs);
            default:
                throw new RuntimeException("Wrong evaluator for string data type");
        }
    }

    private boolean evaluateForNum(Evaluator evaluator, Object lhs, Object rhs) {
       Double lhsValue = (Double) lhs;
       Double rhsValue = (Double) rhs;
        switch (evaluator) {
            case EQ :
                return rhsValue == lhsValue;
            case NE:
                return rhsValue != lhsValue;
            case GT:
                return lhsValue > rhsValue;
            case GTE:
                return lhsValue >= rhsValue;
            case LT:
                return lhsValue < rhsValue;
            case LTE:
                return lhsValue <= rhsValue;
            default:
                throw new RuntimeException("Wrong evaluator for Num data type");

        }
    }
}
