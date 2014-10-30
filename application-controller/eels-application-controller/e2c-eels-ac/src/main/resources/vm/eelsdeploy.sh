#!/bin/bash
usage()
{
cat << EOF
usage: $0 options

This script install the required code to run the eels application according to the eco2clouds application model

OPTIONS:
  -h	show this message
  -t	type of installation: complete|codeconf|code. complete is the default
  -c	ip address of the vm hosting the Application Controller
  -a  list of ip addresses of the vm hosting the Application Local Agents

e.g. eelsdeploy.sh -t complete -c 121.18.2.111 -a "121.18.2.112 121.18.2.113"

EOF
}

AC=
ALA=
TYPE=
while getopts "ht:c:a:" OPTION
do
  case $OPTION in
	h)
	  usage
	  exit 1
	  ;;
	t) 
	  TYPE=$OPTARG
	  ;;
	c)
	  AC=$OPTARG
	  ;;
	a)
	  ALA=($OPTARG)
	  ;;
	?)
	  usage
	  exit
	  ;;
  esac
done

if [ "$TYPE" == "complete" ]
  then
  if [ AC != "" ]
  	then ./eelsdeploycompleteAC.sh $AC
  fi

  if [ ALA != "" ]
	  then ./eelsdeploycompleteALA.sh ${ALA[*]}
  fi
fi

if [ "$TYPE" == "codeconf" ]
  then
  if [ AC != "" ]
	  then ./eelsdeploycodeconfAC.sh $AC
  fi

  if [ ALA != "" ] 
	  then ./eelsdeploycodeconfALA.sh ${ALA[*]}
  fi
fi

if [ "$TYPE" == "code" ] 
  then
  if [ AC != "" ]
	  then ./eelsdeploycodeAC.sh $AC
  fi

  if [ ALA != "" ]
	  then ./eelsdeploycodeALA.sh ${ALA[*]}
  fi
fi
