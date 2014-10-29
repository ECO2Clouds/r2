if [[ -z $(ps aux | grep "java -jar E2CEelsALA" | grep -v grep) ]]; then
    newline="* * * * * /home/zabbix/eels/computecpu.sh"
    (crontab -l; echo "$newline") | crontab -
    newline="* * * * * /home/zabbix/eels/computethroughput.sh"
    (crontab -l; echo "$newline") | crontab -
    newline="* * * * * /home/zabbiz/eels/computeresponsetime.sh"
    (crontab -l; echo "$newline") | crontab -
    cd /root/eelsALA
    java -jar E2CEelsALA-1.2-SNAPSHOT.jar >serverALA.log 2>serverALA.log &
else
    echo "process already running"
fi
