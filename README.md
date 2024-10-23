# Rule Engine with AST

## Table of Contents
- [Objective](#objective)
- [Demo link](#demo)
- [Database](#database-)
- [Processing and Analysis](#processing-and-analysis)
- [UI Page](#ui-page)
- [Tech Stack](#tech-stack)
- [API Endpoints](#api-endpoints)
- [Test Cases](#test-cases)
- [Bonus Features](#bonus-features)
- [Installation and Setup](#installation-and-setup)

## Objective
Develop a simple 3-tier rule engine application(Simple UI, API and Backend, Data) to determine
user eligibility based on attributes like age, department, income, spend etc.The system can use
Abstract Syntax Tree (AST) to represent conditional rules and allow for dynamic
creation,combination, and modification of these rules.

## Demo
[Watch the video ](https://drive.google.com/file/d/1-MVA7l6DIo3JPCri8jNjYRjF2SMRpx3x/view?usp=sharing)

## Database 

### 1. RuleMetadata (Mongo)

The `RulesApp1` collection stores all available rules like below
```
{
  "ruleName": "Rule 1",
  "ruleAsStr": "(\"((age > 30 AND department = 'Marketing')) AND (salary > 20000 OR experience > 5))",
  "uniqueId": "bbe3b578-95ec-4993-9331-fca54f589f9c"
}
```


## Processing and Analysis
- Taking rules from user, validate it, and store in the mongo collection [Database](#database-)
- RuleParser is the class, which takes care of validating the input given by User.
- AST is used to evaluate the expression (Rule) given by the user after validation.
- Postorder traversal is used for evaluating the Rule in the class ASTNode.
- Leaf nodes contains the condition (i.e. age > 30), non-leaf nodes contains expression (i.e. (age > 30 AND salary > 50000)).
- Multiple rules are combined using AND operator i.e. Rule 1 becomes left node, Rule 2 becomes right node, and AND is kept at root node (Output node).

## UI Page
- Provision to give JSON data for Rules engine. Backend API will return True, if all rules are satisfied else False. Pop-up will be shown as True/False.
- All available rules are shown in the table in the UI.
- User can delete any rule using delete button.
- User can create a rule using create rule section, backend will validate the rule provided by the user.

## Tech Stack
- Springboot
- Mongo
- HTML
- JavaScript

## API Endpoints
- POST /api/rules/check: Evaluates the user's attributes against all existing rules and returns whether they match.

- GET /api/rules/all: Retrieves and returns a list of all stored rules.

- POST /api/rules/add: Validates and adds a new rule to the database.

- DELETE /api/rules/delete/{uniqueId}: Deletes the specified rule by its unique ID from the database.


## Test Cases

- Created individual rules from the 'example rules'/'user rules' using create_rule(buildAST) and verified their AST representation.

- Combined the 'example rules'/'user rules' using combine_rules (Check logic here [Processing and Analysis](#processing-and-analysis)), ensuring that the resulting AST accurately reflects the combined logic.

- Implemented sample JSON data and successfully tested evaluate_rule using [API endpoints](#api-endpoints) across various scenarios.

- Explored combining additional rules and validated the functionality effectively.


## Bonus Features
- Implement error handling for invalid rule strings or data formats (e.g., missing operators,
  invalid comparisons). Many validations are added like -
  - If '>', '<', etc. operators are used for department, then rules can not be added to the system.
  - If value for department is numerical then rules can not be added to the system.
  - If value is string for age, salary, experience then rules can not be added to the system.
  - If wrong parenthesis are used then rules can not be added to the system.
  - If any other field than 4 given field (salary, age, dept, exp.) then rules can not be added to the system.
  - "=>", "<", ">", "<=", ">=", "!=", "=" any operator other than these are not supported.
  - etc.
  
  


## Installation and Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/Pranay242/ruleEngineWithAST
   cd ruleEngineWithAST
   #mongo.url=localhost
   #mongo.db.name=zeotapApp1Rules
   #mongo.port=27017
   #mongo.username=
   #mongo.password=
   
   Please provide above details about your mongodb. Please create the db if not created.
   
   URL to open the application - your-host-ip:8080/ (localhost:8080/)
     ```
   
