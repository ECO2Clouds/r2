IP_AC="$1"


echo "Installing the application controller code on $IP_AC …"

scp ../target/E2CEelsAC-1.1-SNAPSHOT.jar root@$IP_AC:eelsAC/
#scp ../target/lib/*.jar root@$IP_AC:eelsAC/lib/
scp ../target/lib/E2CApplicationController*.jar root@$IP_AC:eelsAC/lib/
scp ../applicationcontroller.properties root@$IP_AC:eelsAC/
scp ../applicationcontroller_infra.properties root@$IP_AC:eelsAC/
scp ../eelsapplication.properties root@$IP_AC:eelsAC/
scp ../metric_app.sh root@$IP_AC:/home/zabbix/eels
ssh $IP_AC "chmod 777 /home/zabbix/eels/*.*"
ssh $IP_AC "chown -R zabbix:zabbix /home/zabbix/eels"

echo "Installation done." 