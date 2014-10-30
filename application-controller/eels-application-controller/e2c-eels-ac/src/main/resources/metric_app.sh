
#!/bin/bash 
#used by zabbix agent to read the task performance
cat $1
#no_lines=`cat $1 | wc -l`
#if [ "$no_lines" -eq "0" ]
#  then echo "0"
#  else echo `tail -1 $1`
#fi
#> $1
# metrics to be added on zabbix for monitoring the application
#applicationmetric_1, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/appthroughput.log, valuetype=3
#applicationmetric_2, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/apppower.log, valuetype=3
#applicationmetric_3, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/appresponsetime.log, valuetype=3
#applicationmetric_4, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/apppue.log, valuetype=3
#applicationmetric_5, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/appenergyproductivity.log, valuetype=3

