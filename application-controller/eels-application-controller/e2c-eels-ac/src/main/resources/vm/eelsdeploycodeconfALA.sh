for ipaddress in $@
do

  echo "Installing the ALA code on $ipaddress … "

  scp ../../E2CEelsALA/target/E2CEelsALA-1.1-SNAPSHOT.jar root@$ipaddress:eelsALA/
  scp ../../E2CEelsALA/target/lib/E2CEelsAC-1.1-SNAPSHOT.jar root@$ipaddress:eelsALA/lib
  #scp ../../E2CEelsALA/target/lib/*.jar root@$ipaddress:eelsALA/lib/
  scp ../../E2CEelsALA/target/lib/E2CApplication*.jar root@$ipaddress:eelsALA/lib/
  scp ../../E2CEelsALA/eelslocalagent.properties root@$ipaddress:eelsALA/
  scp ../../E2CEelsALA/localagent.properties root@$ipaddress:eelsALA/
  scp ../../E2CEelsALA/tp_item.sh root@$ipaddress:/home/zabbix/eels
  ssh $ipadress "chmod 777 /home/zabbix/eels/*.*"
  ssh $ipadress "chown -R zabbix:zabbix /home/zabbix/eels"
  echo "Installation done." 

done
