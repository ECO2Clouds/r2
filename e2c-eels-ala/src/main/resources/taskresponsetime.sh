#!/bin/bash 
#used by zabbix agent to read the task performance

cat /home/zabbix/eels/rt4ALA.log

# metrics to be added on zabbix for monitoring the application
#applicationmetric_2, /home/zabbix/eels/taskresponsetime.sh, valuetype=3
