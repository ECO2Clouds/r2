#!/bin/bash


#PARAMETERS: 
#       par $1: ip address of oceanographic data VM
#       par $2: starting year
#       par $3: eels number
#       par $4: log file folder
#	par $5: sim number

echo Neel = "$3" >> data/init.dat
echo year_start = "$2" >> data/init.dat
year=`expr $2 + 1`
echo year_stop = "$year" >> data/init.dat
echo \/ >> data/init.dat

unbuffer ./eels_single.out 2>&1 | tee run.txt 1> "$4"/tpstep"$5".log 

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

 
