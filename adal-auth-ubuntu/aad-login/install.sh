#!/bin/bash

#######################
#
# Name:	install.sh
# Description: sets up your Azure Active Directory Authentication on your ubuntu machine
# Author: chvugrin@microsoft.com
# 
#######################

# OS Check
if [ ! -f /etc/lsb-release ]
then
  echo "Only tested on debian like systems"
  exit 1
else
  id=$(cat /etc/lsb-release | grep DISTRIB_ID | sed 's/^.*[=]//')
  if [[ $id -ne "Ubuntu" ]]
  then
    echo "Only tested on Ubuntu"
    exit 1
  fi
fi
# are you root
$id=$(whoami)
if [ $id != "root" ] 
then
  echo "you need to be root"
  exit 1
fi

echo "starting setting up your Azure Active Directory Authentication on your ubuntu machine"
mkdir -p /opt/aad-login
cp aad-login.js package.json /opt/aad-login/
cp aad-login /usr/local/bin/
tar xzf aad-login_0.1.tar.gz
