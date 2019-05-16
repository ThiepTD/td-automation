# td-automation
This is an automation framework for our QA team to support plugin automation testing. It supports:
 - API testing using Rest Assured
 - CSV and file content comparison
 - Execute OS commands or user defined commands
 - Test data generation by using existing Python code
## Setup
 - Make sure you have Java jdk installed & configured (JAVA_HOME) on your machine
 - Make sure you have Mave installed & configured (MVN_HOME) on your machine
 - Check out the source code from [here](https://github.com/ThiepTD/td-automation). Noted that we will move the source code to an official QA repo soon
 - Run command "mvn clean install" to download dependencies
## IDE
 - You can use any IDE you want or you can use Eclipse or Intellij (Community version)
 - Import a maven project then you can start using the framework
## Add permission for shell scripts
 - There is one script tdQuery.sh under folder td-automation/src/main/resources
 - At the first time using it, you may need to add execution permission for it by running command "chmod u+x tdQuery.sh"
## Execute test scripts
 - All test scripts should be under foder td-automation/src/test
 - They are java classes so can just right click on any functions which have annotation @Test and run it using TestNg
