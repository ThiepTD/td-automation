#!/bin/bash
echo execute command td query -d $1 -w -T presto -f csv -o $2 "'$3'"
td query -d $1 -w -T presto -f csv -o $2 "$3"