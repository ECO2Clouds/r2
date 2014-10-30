#!/bin/bash 

year=`expr $1 - 1`
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/dec"$year"*.* ~/OceanData/

for((i=0;i<$2;i++)); do
	year=`expr $1 + $i`
	sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/*"$year"*.* ~/OceanData/
done
year=`expr $1 + $2`
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/jan"$year"*.* ~/OceanData/
sshpass -p bonfire rsync -av -e --ignore-existing -e "ssh -o StrictHostKeyChecking=no" root@"$3":/media/OceanographicData/OceanData/feb"$year"*.* ~/OceanData/

