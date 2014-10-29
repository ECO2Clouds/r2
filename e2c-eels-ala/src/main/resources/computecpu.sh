#!/bin/bash 
#used by zabbix agent to read the task cpu usage

CPU=`ps aux | grep eels_single.out | grep -v grep | grep -v unbuffer | sed 's/  */\|/g' | cut -d"|" -f3`
if [[ -z "$CPU" ]]
        then CPU=0 
fi
echo ${CPU%.*} > /home/zabbix/eels/cpuutil4ALA.log




