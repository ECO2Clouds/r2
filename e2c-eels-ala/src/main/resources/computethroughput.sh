#!/bin/bash 
#used by zabbix agent to read the task performance

no_lines=`cat /home/zabbix/eels/tpstep0.log | wc -l`
if [ "$no_lines" -eq "0" ]
  then echo "0" > /home/zabbix/eels/tp4ALA.log
  #else echo `cat $1 | awk '{s+=$2} END {print NR/s*1000}'`
  else value=$((`cat /home/zabbix/eels/tpstep0.log | wc -l` / 30))
       echo $value > /home/zabbix/eels/tp4ALA.log
 #multiply by 2 as the samplig time is every 30 secs
fi
> /home/zabbix/eels/tpstep0.log

