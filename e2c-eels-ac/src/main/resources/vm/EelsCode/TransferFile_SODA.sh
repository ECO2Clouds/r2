#!/bin/bash 

year=`expr $1 + 1899`
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/"$year"12*.* /media/OceanData/

for((i=0;i<$2;i++)); do
	year=`expr $1 + $i + 1900`
	sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/"$year"*.* /media/OceanData/
done
year=`expr $1 + $2 + 1900`
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/"$year"01*.* /media/OceanData/
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/"$year"02*.* /media/OceanData/

sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/coordinate*.* /media/OceanData/
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/speed.* /media/OceanData/
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/temp.* /media/OceanData/
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/salt.* /media/OceanData/
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/date.* /media/OceanData/
