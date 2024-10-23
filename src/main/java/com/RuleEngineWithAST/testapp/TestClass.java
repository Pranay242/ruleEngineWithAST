package com.RuleEngineWithAST.testapp;

import com.RuleEngineWithAST.ASTNode;
import com.RuleEngineWithAST.repository.RuleServiceImpl;
import com.RuleEngineWithAST.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class
 */
@Slf4j
public class TestClass {

    public static void main(String[] args) {

        RuleServiceImpl ruleService = new RuleServiceImpl();
        String rule1 = "(((age > 30 AND department = 'Sales') OR (age < 25 AND department = 'Marketing')) AND (salary > 50000 OR experience > 5))";
        String rule2 = "((age > 30 AND department = 'Marketing')) AND (salary > 20000 OR experience > 5)";

        List<String> rules = new ArrayList<>();
        rules.add(rule1);
        rules.add(rule2);

        ASTNode astNode = ruleService.combineRules(rules);


        //log.info("ast" + astNode);
        User user = new User();
        user.setAge(90.0);
        user.setDepartment("Sales");
        user.setSalary(10000.0);
        user.setExperience(4.0);

        boolean res = astNode.evaluateExpression(user);

        log.info("value - " + res);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            ASTNode astNode = objectMapper.readValue(new File("D:\\zeotap\\ruleEngineWithAST\\src\\main\\resources\\test\\sample.json"), ASTNode.class);
//            boolean val = astNode.evaluateExpression(user);
//            log.info("value : " + val);
//        } catch (IOException e) {
//            log.error("error : {}", e.getMessage(), e);
//        }

    }

}
