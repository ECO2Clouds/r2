crontab -r
ps -ef | grep "java -jar E2CEelsALA-1.2-SNAPSHOT.jar" | awk '{print $2}' | xargs kill
