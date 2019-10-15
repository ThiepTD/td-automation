@echo off
echo ---------		Option:  %1, %2
cd ../../../
mvn -Dtest=%1#%2 test
