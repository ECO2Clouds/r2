#install application controller
IP_AC="${1}"
scp ../target/E2CEelsAC-1.1-SNAPSHOT.jar root@$IP_AC:eelsAC/
scp ../applicationcontroller.properties root@$IP_AC:eelsAC/
scp ../eelsapplication.properties root@$IP_AC:eelsAC/

#install application local agent
shift
for ipaddress in $@
do
  scp ../../E2CEelsALA/target/E2CEelsALA-1.1-SNAPSHOT.jar root@$ipaddress:eelsALA/
  scp ../../E2CEelsALA/eelslocalagent.properties root@$ipaddress:eelsALA/
done
