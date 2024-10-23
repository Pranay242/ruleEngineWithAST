package com.RuleEngineWithAST.controllers;

import com.RuleEngineWithAST.ASTNode;
import com.RuleEngineWithAST.RuleParser;
import com.RuleEngineWithAST.repository.RuleService;
import com.RuleEngineWithAST.entity.User;
import com.RuleEngineWithAST.repository.RuleDAO;
import com.RuleEngineWithAST.mongobean.RuleMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/rules")
public class UIController {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleDAO ruleDAO;

    @PostMapping("/check")
    @ResponseBody
    public ResponseEntity<String> getResult(@RequestBody User user) {
        try {
            List<RuleMetadata> allRules = ruleDAO.getRules();
            if(CollectionUtils.isEmpty(allRules)) {
                return ResponseEntity.status(500).body("No Rules Found");
            }
            List<String> allRulesInStr = allRules.stream()
                    .map(RuleMetadata::getRuleAsStr)
                    .collect(Collectors.toList());
            ASTNode astNode = ruleService.combineRules(allRulesInStr);
            boolean res = astNode.evaluateExpression(user);
            return ResponseEntity.ok(res ? "True" : "False");
        } catch (Exception e) {
            // Log the error
            return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @ResponseBody
    public List<RuleMetadata> getAllRules() {
        return ResponseEntity.ok(ruleDAO.getRules()).getBody();
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addRule(@RequestBody RuleMetadata ruleMetadata) {
        // Validate rule logic here
        if (ruleMetadata.getRuleAsStr() == null) {
            return ResponseEntity.status(500).body("Invalid rule format");
        }
        if(ruleMetadata.getRuleName() == null) {
            return ResponseEntity.status(500).body("Please provide rule name");

        }
        try {
            boolean res = isValidRule(ruleMetadata.getRuleAsStr());
            if(!res) {
                return ResponseEntity.status(500).body("Invalid rule format");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
        ruleMetadata.setRuleAsStr("(" + ruleMetadata.getRuleAsStr() + ")");
        ruleDAO.insertOne(ruleMetadata);
        return ResponseEntity.ok("Rule added successfully");
    }

    @DeleteMapping("/delete/{uniqueId}")
    @ResponseBody
    public ResponseEntity<String> deleteRule(@PathVariable String uniqueId) {
        ruleDAO.deleteById(uniqueId);
        return ResponseEntity.ok("Rule deleted successfully");
    }

    private boolean isValidRule(String rule) {
        return RuleParser.isValid(rule);
    }
}
