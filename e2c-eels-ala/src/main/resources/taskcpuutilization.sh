#!/bin/bash 
#used by zabbix agent to read the task cpu usage

cat /home/zabbix/eels/cpuutil4ALA.log

# metrics to be added on zabbix for monitoring the application
#applicationmetric_3, /home/zabbix/eels/taskcpuutilization.sh, valuetype=3
