| Environment | STATUS |
| ---- | ----------- |  
| Development | [![CircleCI](https://circleci.com/gh/treasure-data/td-qa/tree/dev.svg?style=svg&circle-token=b3b46581e8a6ebb86e4d7d2b03e85dc70bd779fb)](https://circleci.com/gh/treasure-data/workflows/td-qa/tree/dev) |
| Development EU01 | NA |  
| Staging | [![CircleCI](https://circleci.com/gh/treasure-data/td-qa/tree/staging.svg?style=svg&circle-token=b3b46581e8a6ebb86e4d7d2b03e85dc70bd779fb)](https://circleci.com/gh/treasure-data/workflows/td-qa/tree/staging) |
| Production | [![CircleCI](https://circleci.com/gh/treasure-data/td-qa/tree/master.svg?style=svg&circle-token=b3b46581e8a6ebb86e4d7d2b03e85dc70bd779fb)](https://circleci.com/gh/treasure-data/td-qa/tree/master) |

# td-automation
This is an automation framework for our QA team to support plugin automation testing. It supports:
 - API testing using Rest Assured
 - CSV and file content comparison
 - Execute OS commands or user defined commands
 - Test data generation by using existing Python code
## Setup
 - Make sure you have Java jdk installed & configured (JAVA_HOME) on your machine
 - Make sure you have Mave installed & configured (MVN_HOME) on your machine
 - Check out the source code from [here](https://github.com/treasure-data/td-qa.git). Noted that we will move the source code to an official QA repo soon
 - Run command "mvn clean install" to download dependencies
 - Since we are using pyhon code to generate test data so please see [here](https://github.com/treasure-data/td-qa/tree/master/Tools/programs/data_generator) for Python setup
## IDE
 - You can use any IDE you want or you can use Eclipse or Intellij (Community version)
 - Import a maven project then you can start using the framework
## Add permission for shell scripts
 - There are some batch/shell scripts under folder td-automation/src/main/scripts
 - At the first time using it, you may need to add execution permission for it by running command "chmod u+x <script file name>"
## Execute test scripts
 - All test scripts should be under foder td-automation/src/test
 - They are java classes so can just right click on any functions which have annotation @Test and run it using TestNg
 - You can cd to src/main/scripts the type executeTest.bat or executeTest.sh <Test class> <test method>.
   - ./executeTest.sh TdExecutorTest devEu01
