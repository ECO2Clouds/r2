for ipaddress in $@
do

  echo "Installing the ALA code on $ipaddress … "

  scp ../../E2CEelsALA/target/E2CEelsALA-1.2-SNAPSHOT.jar root@$ipaddress:eelsALA/
  scp ../../E2CEelsALA/target/lib/E2CEelsAC-1.2-SNAPSHOT.jar root@$ipaddress:eelsALA/lib
  #scp ../../E2CEelsALA/target/lib/*.jar root@$ipaddress:eelsALA/lib/
  scp ../../E2CEelsALA/target/lib/E2CApplication*.jar root@$ipaddress:eelsALA/lib/
  ssh $ipaddress "chmod 777 /home/zabbix/eels/*.*"
  ssh $ipaddress "chown -R zabbix:zabbix /home/zabbix/eels"
  echo "Installation done." 

done
