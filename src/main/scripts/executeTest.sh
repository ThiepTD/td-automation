#!/bin/bash
echo ---------		Option:  $1, $2
cd ../../../
mvn -Dtest=$1#$2 test

cd src/main/scripts
read -p "Press enter to continue"