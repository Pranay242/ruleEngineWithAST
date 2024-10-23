package com.RuleEngineWithAST.ruleEngineWithAST;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.RuleEngineWithAST.*")
public class RuleEngineWithAstApplication {

	public static void main(String[] args) {
		SpringApplication.run(RuleEngineWithAstApplication.class, args);
	}

}
