#!/bin/bash 
#used by zabbix agent to read the task performance
no_lines=`cat /home/zabbix/eels/responsetime.log | wc -l`
if [ "$no_lines" -eq "0" ]
  then > /home/zabbix/eels/rt4ALA.log
  else 
       cat /home/zabbix/eels/responsetime.log > /home/zabbix/eels/rt4ALA.log
fi
> /home/zabbix/eels/responsetime.log

