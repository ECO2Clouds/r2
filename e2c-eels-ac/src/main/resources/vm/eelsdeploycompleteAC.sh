IP_AC="$1"

echo "Installing Application Controller (AC) script"
echo "for installing the packages in $IP_AC …"

scp ACCreator.sh root@$IP_AC:/root/
ssh $IP_AC "chmod +x /root/ACCreator.sh"
echo "Installation done."
echo "******************"
echo "Remember to run /root/ACCreator.sh on $IP_AC to install "
echo "all the required packages before running the application controller"
echo "******************"

echo 
echo "Installing the application controller code on $IP_AC …"

ssh $IP_AC "mkdir /home/zabbix; mkdir /home/zabbix/eels; mkdir /root/eelsAC; mkdir /root/eelsAC/lib"
scp ../target/E2CEelsAC-1.2-SNAPSHOT.jar root@$IP_AC:eelsAC/
scp ../target/lib/*.jar root@$IP_AC:eelsAC/lib/
scp ../applicationcontroller.properties root@$IP_AC:eelsAC/
scp ../applicationcontroller_infra.properties root@$IP_AC:eelsAC/
scp ../eelsapplication.properties root@$IP_AC:eelsAC/
scp ../startALA.sh root@$IP_AC:/root/eelsAC
scp ../stopALA.sh root@$IP_AC:/root/eelsAC
scp ../metric_app.sh root@$IP_AC:/home/zabbix/eels
ssh $IP_AC "> /home/zabbix/eels/appthroughput.log; > /home/zabbix/eels/apppower.log; > /home/zabbix/eels/appresponsetime.log; > /home/zabbix/eels/apppue.log; > /home/zabbix/eels/appenergyproductivity.log; chmod 777 /home/zabbix/eels/*.*; chmod 777 /root/eelsAC/*.sh "
 ssh $IP_AC "chown -R zabbix:zabbix /home/zabbix/eels"
echo "Installation done." 

