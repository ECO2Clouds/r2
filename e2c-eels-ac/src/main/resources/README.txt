******************
Eels Application v.1.1
provided by POLIMI
******************

This file contains information about how to run the Eels Application, starting from the creation of the VMs to the execution. 
All the required scripts can be found under the ‘vm’ folder


*** WHAT IS THE EELS APPLICATION ***

The case study is composed by 2 projects and needs a VM configured as described below:
* E2CEelsAC: the application controller responsible to run the entire experiment. The VM hosting this module could not contain the eels application.
* E2CEelsALA: the Application Local Agent responsible to start the simulation task installed on the VMs

*** HOW TO INSTALL ***
1) create one or more virtual machines with at least 10Gb of disk
2) Run the eelsdeploy.sh under the folder 'vm'

OPTIONS:
  -h	show this message
  -t	type of installation: complete|codeconf|code. complete is the default
  -c	ip address of the vm hosting the Application Controller
  -a  list of ip addresses of the vm hosting the Application Local Agents

e.g. eelsdeploy.sh -t complete -c 121.18.2.111 -a "121.18.2.112 121.18.2.113"

This command installs the script for customizing the VMs and the installs the AC in *.111 and ALA in *.112 and *.113

A complete installation is required the first time as it copies the script to be run for installing the required packages


*** HOW TO CUSTOMIZE A VM HOSTING THE AC ***

Log in through ssh to the VM selected as AC and run the ACCreator.sh script you can find in the root folder as follows:
	 ./ACCreator.sh (say YES to everything when prompted)
	
Now you have a virtual machine ready to run the Application Controller as described next.

*** HOW TO CUSTOMIZE A VM HOSTING THE ALA ***

Log in through ssh to the VMs (could be more than one) selected as ALA and run the EelsImageCreator.sh script you can find in the root folder as follows:
	 ./EelsImageCreator.sh (say YES to everything when prompted)
	
Now you have a virtual machine ready to run the Application LocalAgent as described next.

*** HOW TO CONFIGURE ZABBIX ON AC ***

1) ssh on the virtual machine of the AC
2) run the command /usr/local/bin/zabbix-add-metric
3) when requested add the following metrics (one by one)
   - applicationmetric_1, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/appthroughput.log, valuetype=3
   - applicationmetric_2, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/apppower.log, valuetype=3
   - applicationmetric_3, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/appresponsetime.log, valuetype=3
   - applicationmetric_4, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/apppue.log, valuetype=3
   - applicationmetric_5, /home/zabbix/eels/metric_app.sh /home/zabbix/eels/appenergyproductivity.log, valuetype=3

*** HOW TO CONFIGURE ZABBIX ON ALA ***

These steps need to be done for each of the VMs where the ALA is installed

1) ssh on the virtual machine of the ALA
2) run the command /usr/local/bin/zabbix-add-metric
3) when requested add the following metric
   - applicationmetric_1, /home/zabbix/eels/taskthroughput.sh, valuetype=3
   - applicationmetric_2, /home/zabbix/eels/taskresponsetime.sh, valuetype=3
   - applicationmetric_3, /home/zabbix/eels/taskcpuutilization.sh, valuetype=3

*** HOW TO CONFIGURE THE Eels Application Controller ***
1) connect to the VM where the E2CEelsAC has been deployed
2) move to the /root/eelsAC directory
3) open the eelsapplication.properties to set of the case study:
    3a) configure the parameter for the experiments (start_year, stop_year, and number of eels)
    3b) indicates the Ip address of the VM to which the storage having the OceanographicData is mounted on
4) open the applicationcontroller.properties to set up the application controller:
    4a) portal_username, portal_password (user credentials to the bonfire portal)
    4c) experiment_no (id of the experiment as it is shown in the eco2clouds portal)
    4d) vms (list of the ip:port of the ALA servers. e.g.: “172.18.1.1,172.18.1.2)

5) open the applicationcontroller_infra.properties to set up the access to the scheduler:
    5a) scheduler_url (URL of the scheduler. Usually https://scheduler.eco2clouds.eu/scheduler)
    5b) keystore (jks file containing the key to access to the scheduler. If you do not have it, you need to ask to the Eco2Clouds administrator)
    5c) password (password to open the jks file)
    5d) scheduler_state = on
    5e) monitoring_state = on
    5f) ala_server_port = 9998

*** HOW TO RUN THE APPLICATION ***
1) run the Application Controller
    1a) connect to the VM where the Application Controller is installed
    1b) import you bonfire access key-pair to .ssh folder (required to make possible the connection to the other VMs of the same experiment)
    1c) move to eelsAC
    1d) run the AC with java -jar E2CEelsAC-1.1-SNAPSHOT.jar
    1e) it may be required a confirmation for trusting to the vms (say ‘yes’)

*** RESULTS ***
1) results of the experiment are stored under the /home/zabbix/eels directory in files with prefix eelsresultYYYYMMDD-HHmmSS.log
