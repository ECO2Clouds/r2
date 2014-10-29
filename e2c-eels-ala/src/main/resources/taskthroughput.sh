#!/bin/bash 
#used by zabbix agent to read the task performance

cat /home/zabbix/eels/tp4ALA.log

# metrics to be added on zabbix for monitoring the application
#applicationmetric_1, /home/zabbix/eels/taskthroughput.sh, valuetype=3
