for ipaddress in $@
do

 
  echo "Installing Application Local Agent (ALA) script"
  echo "for installing the packages in $ipaddress …"
  scp -r EelsCode/* root@$ipaddress:/root/
  scp EelsImageCreator.sh root@$ipaddress:/root/
  ssh $ipaddress "chmod +x /root/EelsImageCreator.sh"

  echo "Installation done." 
  echo "******************"
  echo "Remember to run /root/EelsImageCreator.sh on $ipaddress to install "
  echo "all the required packages before running the ALA"
  echo "******************"


  echo "Installing the ALA code on $ipaddress … "

  ssh $ipaddress "mkdir /home/zabbix; mkdir /home/zabbix/eels; mkdir /root/eelslog; mkdir /root/eelsALA; mkdir /root/eelsALA/lib"
  scp ../../E2CEelsALA/target/E2CEelsALA-1.2-SNAPSHOT.jar root@$ipaddress:eelsALA/
  scp ../../E2CEelsALA/target/lib/*.jar root@$ipaddress:eelsALA/lib/
  scp ../../E2CEelsALA/eelslocalagent.properties root@$ipaddress:eelsALA/
  scp ../../E2CEelsALA/startServer.sh root@$ipaddress:eelsALA/
  scp ../../E2CEelsALA/stopServer.sh root@$ipaddress:eelsALA/
  ssh $ipaddress "chmod 777 /root/eelsALA/startServer.sh; chmod 777 /root/eelsALA/stopServer.sh"
  scp ../../E2CEelsALA/localagent.properties root@$ipaddress:eelsALA/
  scp ../../E2CEelsALA/taskcpuutilization.sh root@$ipaddress:/home/zabbix/eels/
  scp ../../E2CEelsALA/taskresponsetime.sh root@$ipaddress:/home/zabbix/eels/
  scp ../../E2CEelsALA/taskthroughput.sh root@$ipaddress:/home/zabbix/eels/
  scp ../../E2CEelsALA/computecpu.sh root@$ipaddress:/home/zabbix/eels/
  scp ../../E2CEelsALA/computethrouhgput.sh root@$ipaddress:/home/zabbix/eels/
  scp ../../E2CEelsALA/computeresponsetime.sh root@$ipaddress:/home/zabbix/eels/
  ssh $ipaddress "> /home/zabbix/eels/tpstep0.log; > /home/zabbix/eels/responsetime.log; chmod 777 /home/zabbix/eels/tpstep0.log; chmod 777 /home/zabbix/eels/*.*"
  ssh $ipaddress "chown -R zabbix:zabbix /home/zabbix/eels"
echo "Installation done." 

done
