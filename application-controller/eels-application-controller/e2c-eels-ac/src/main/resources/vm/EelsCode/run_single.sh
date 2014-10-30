#!/bin/sh

#PARAMETERS: 
#	par $1: ip address of oceanographic data VM
#	par $2: starting year
#	par $3: simulation number


gfortran -O3 -oSimulazione_single/eels_single.out Codice/distributions_001.f90 Codice/eels_mod_030.f90 Codice/eels_main_027.f90 
#check directory of mounted OceanographicData disk AND CHANGE THE IP ADDRESS
sudo service gdm3 stop

./TransferFile.sh "$2" 5 "$1"

cd Simulazione_single

./eels_single.out > run.txt 2>&1

./run_RunPostProcess.sh /usr/local/MATLAB/MATLAB_Compiler_Runtime/v717 > PP.txt 2>&1

zip -9 wholedata data/*.* data/output/*.* PostProcess/*.* PostProcess/graf/*.* *.txt

rm PostProcess/*.*
rm PostProcess/graf/*.*
rm data/output/*.*
rm *.txt
rm eels_single.out
rm ../*.mod

sshpass -p bonfire scp -o StrictHostKeyChecking=no  wholedata.zip "$1":/media/OceanographicData/Results/"$2".zip
 #check directory of mounted OceanographicData disk, the name of zip file AND THE IP ADDRESS

rm wholedata.zip

exit
