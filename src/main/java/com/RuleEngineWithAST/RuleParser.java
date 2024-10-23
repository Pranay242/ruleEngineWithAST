package com.RuleEngineWithAST;

import com.RuleEngineWithAST.bean.Condition;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleParser {

    public static boolean isValid(String rule) {
        validateParentheses(rule);
        List<String> tokens = tokenize(rule);
        validateTokenSequence(tokens);
        return true;
    }

    public static ASTNode parseRule(String rule) {
        validateParentheses(rule);
        List<String> tokens = tokenize(rule);
        validateTokenSequence(tokens);
        return buildAST(tokens);
    }

    private static void validateParentheses(String rule) {
        Stack<Character> stack = new Stack<>();
        boolean wasLastCharOpen = false;
        for (char c : rule.toCharArray()) {
            if (c == '(') {
                wasLastCharOpen = true;
                stack.push(c);
            } else if (c == ')') {
                if(wasLastCharOpen == true) {
                    throw new IllegalArgumentException("Unmatched closing parenthesis in rule: " + rule);
                }
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Unmatched closing parenthesis in rule: " + rule);
                }
                stack.pop();
            } else if (!" ".equals(c)){
                wasLastCharOpen = false;
            }
        }
        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("Unmatched opening parenthesis in rule: " + rule);
        }
    }

    private static void validateTokenSequence(List<String> tokens) {
        int i = 0;
        for (String token : tokens) {
            if(isValidOperator(token)) {
               if(i == 0 || i == tokens.size() - 1) {
                   throw new IllegalArgumentException("First/last character can not be operator");
               }
               String left = tokens.get(i - 1);
               String right = tokens.get(i + 1);
               if(!isCorrectField(left)) {
                   throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
               }
               Condition.DataType dataType = determineDataType(left);
               if(dataType == Condition.DataType.NUM && !right.matches("-?\\d+(\\.\\d+)?")) {
                   throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
               }
               if(dataType == Condition.DataType.STRING && !right.matches("'[^']*'")) {
                   throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
               }
            }
            if(token.matches("'[^']*'") || token.matches("-?\\d+(\\.\\d+)?")) {
                if(i < 2) {
                    throw new IllegalArgumentException("First/last character can not be operator");
                }
                String left = tokens.get(i - 1);
                if(!isValidOperator(left)) {
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }
                if(token.matches("'[^']*'") && !isOperatorCorrectDataType(Condition.DataType.STRING, left)) {
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }

                if (token.matches("-?\\d+(\\.\\d+)?") && !isOperatorCorrectDataType(Condition.DataType.NUM, left)) {
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }

                String leftLeft = tokens.get(i - 2);
                if(!isCorrectField(leftLeft)) {
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }

            }
            if(isCorrectField(token)) {
                if(i >= tokens.size() - 2) {
                    throw new IllegalArgumentException("First/last character can not be operator");
                }
                String right = tokens.get(i + 1);
                if(!isValidOperator(right)) {
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }
                if("department".equals(token) && !isOperatorCorrectDataType(Condition.DataType.STRING, right)) {
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }
                if (!"department".equals(token) && !isOperatorCorrectDataType(Condition.DataType.NUM, right)){
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }

                String rightRight = tokens.get(i +2);
                if(!rightRight.matches("'[^']*'") && !rightRight.matches("-?\\d+(\\.\\d+)?")) {
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }

                if("department".equals(token) && !rightRight.matches("'[^']*'")) {
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }

                if (!"department".equals(token) && !rightRight.matches("-?\\d+(\\.\\d+)?")){
                    throw new IllegalArgumentException("Wrong Rule please check near '" + token + "'");
                }

            }
            i++;
        }
    }

    private static List<String> tokenize(String rule) {
        List<String> tokens = new ArrayList<>();
        String regex = "\\s*(=>|<|>|<=|>=|!=|=|\\(|\\)|AND|OR|\\w+|'[^']*')\\s*";
        Matcher matcher = Pattern.compile(regex).matcher(rule);

        while (matcher.find()) {
            String token = matcher.group(1);

            // Check if the token is a valid field, operator, or a valid string literal
            if (isValidToken(token)) {
                tokens.add(token);
            } else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        return tokens;
    }

    private static boolean isValidToken(String token) {
        List<String> validFields = List.of("age", "salary", "experience", "department");
        if (validFields.contains(token)) {
            return true;
        }
        String[] validOperators = { "=>", "<", ">", "<=", ">=", "!=", "=", "AND", "OR", "(", ")" };
        for (String operator : validOperators) {
            if (token.equals(operator)) {
                return true;
            }
        }
        return token.matches("'[^']*'") ||token.matches("-?\\d+(\\.\\d+)?"); // Match single-quoted strings
    }


    private static ASTNode buildAST(List<String> tokens) {
        Stack<ASTNode> stack = new Stack<>();
        Stack<ASTNode.Operator> operatorStack = new Stack<>();
        int i = -1;
        for (String token : tokens) {
            i++;
            if (token.equals("(")) {
                // Do nothing for left parentheses
                continue;
            } else if (token.equals(")")) {
                // Pop until we find the corresponding operator
                while (!operatorStack.isEmpty()) {
                    ASTNode right = stack.pop();
                    ASTNode left = stack.pop();
                    ASTNode.Operator operator = operatorStack.pop();
                    stack.push(new ASTNode(left, right, operator, ASTNode.NodeType.EXPRESSION, null));
                }
            } else if (token.equals("AND")) {
                operatorStack.push(ASTNode.Operator.AND);
            } else if (token.equals("OR")) {
                operatorStack.push(ASTNode.Operator.OR);
            } else if (isValidOperator(token)){
                // It's a condition
                String operator = token;
                String lhs = tokens.get(i-1); // get next token as operator
                String rhs = tokens.get(i + 1).replace("'", ""); // get the right-hand side, removing quotes

                Condition condition = new Condition(lhs, operatorToEvaluator(operator), determineDataType(lhs), parseValue(rhs));
                stack.push(new ASTNode(null, null, ASTNode.Operator.AND, ASTNode.NodeType.VALUE, condition));
            }
        }

        // If there are remaining nodes, combine them
        if(stack.size() == 2 && operatorStack.size() == 1) {
            ASTNode right = stack.pop();
            ASTNode left = stack.pop();
            ASTNode.Operator operator = operatorStack.pop();
            stack.push(new ASTNode(left, right, operator, ASTNode.NodeType.EXPRESSION, null));
        }

        return stack.pop();
    }

    private static Condition.Evaluator operatorToEvaluator(String operator) {
        return switch (operator) {
            case "=" -> Condition.Evaluator.EQ;
            case ">" -> Condition.Evaluator.GT;
            case ">=" -> Condition.Evaluator.GTE;
            case "<" -> Condition.Evaluator.LT;
            case "<=" -> Condition.Evaluator.LTE;
            case "!=" -> Condition.Evaluator.NE;
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    private static Condition.DataType determineDataType(String lhs) {
        return switch (lhs) {
            case "age", "salary", "experience" -> Condition.DataType.NUM;
            case "department" -> Condition.DataType.STRING;
            default -> throw new IllegalArgumentException("Invalid LHS: " + lhs);
        };
    }

    private static boolean isCorrectField(String lhs) {
        return switch (lhs) {
            case "age", "salary", "experience", "department" -> true;
            default -> false;
        };
    }

    private static Object parseValue(String value) {
        if (value.matches("-?\\d+(\\.\\d+)?")) {
            return Double.valueOf(value); // Convert numeric strings to numbers
        } else {
            return value; // Return as string
        }
    }

    private static boolean isValidOperator(String operator) {
        switch (operator) {
            case "=" :
            case ">" :
            case ">=" :
            case "<" :
            case "<=" :
            case "!=" :
                return true;
            default :
                return false;
        }
    }

    private static boolean isOperatorCorrectDataType(Condition.DataType dataType, String operator) {
        if(dataType == Condition.DataType.NUM) {
            return isValidOperator(operator);
        } else if(dataType == Condition.DataType.STRING) {
            return "=".equals(operator) || "!=".equals(operator);
        } else {
            return false;
        }
    }
}
