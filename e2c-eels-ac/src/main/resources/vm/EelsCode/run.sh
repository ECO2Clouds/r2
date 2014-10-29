#!/bin/bash

#PARAMETERS: 
#	par $1: ip address of oceanographic data VM
#	par $2: starting year
#	par $3: eels number
#	par $4: simulation numbers 
#	par $5: log file folder
#	par $6: empty for std sim or "_SODA" to run with SODA dataset

gfortran -O3 -oSimulazione_single/eels_single.out Codice/distributions_001.f90 Codice/eels_mod_030"$6".f90 Codice/eels_main_027"$6".f90 
#check directory of mounted OceanographicData disk AND CHANGE THE IP ADDRESS
sudo service gdm3 stop

for((i=0;i<$4;i++)); do
	mkdir Simulazione"$i"
	cp -R Simulazione_single/* Simulazione"$i"/
	cp -R Simulazione_single/RunPostProcess"$6" Simulazione"$i"/RunPostProcess
done

years=`expr $4 + 4`

./TransferFile"$6".sh "$2" "$years" "$1"

for((i=0;i<$4;i++)); do
	cd Simulazione"$i"
	year=`expr $2 + $i`
	./simulate.sh "$1" "$year" "$3" "$5" "$i" &
	TASK[$i]=$!
	echo "$i"
	cd ..
done

for((i=0;i<$4;i++)); do
	echo "${TASK[$i]}"
	wait "${TASK[$i]}"
done

for((i=0;i<$4;i++)); do
	rm -R Simulazione"$i"
done

rm Simulazione_single/*.out
rm *.mod
rm /media/OceanData/*.*
#rm OceanData/*.*

exit
