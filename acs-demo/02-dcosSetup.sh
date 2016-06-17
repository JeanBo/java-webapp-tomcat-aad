#!/bin/bash

##########################################
#
# Name:        02-dcosSetup.sh
# Description: Executes the steps needed on mesos in order
#   to make the demo run, the following steps:
#   - Installs a loadbalancer
#   - create network loadbalancer rule (also for loadbalancer if needed)
#   - create network security group rule (also for loadbalancer if needed)
# Contact:     chrvugrin@microsoft.com, ver 1.0
#
##########################################

if [ "$#" -ne 1 ]
then
  echo "please provide the following parameters: "
  echo "- resourcegroup"
  exit 1
fi
resourcegroup=$1

# only install if not already installed
lbInstall=`dcos package list | grep -i marathon-lb`
if [ "$lbInstall" == "" ]
then
  dcos package install marathon-lb

  lbName=$(azure group show $resourcegroup | grep -i lb | grep agent | grep Name | sed 's/^.*[:][ ]//')
  azure network lb rule create -g $resourcegroup --lb-name  $lbName -n haproxy -p tcp -f 9090 -b 9090

  nsgName=$(azure network nsg list -g $resourcegroup| grep agent | grep public | awk '{print $2}')
  azure network nsg rule create -g $resourcegroup -a $nsgName -n haproxy-rule -c Allow -p Tcp -r Inbound -y 410 -f Internet -u 9090
  exit 0
fi


#lbName=$(azure group show $resourcegroup | grep -i lb | grep agent | grep Name | sed 's/^.*[:][ ]//')
#azure network lb rule create -g $resourcegroup --lb-name $lbName -n $rulename -p tcp -f $appPort -b $appPort

#nsgName=$(azure network nsg list -g $resourcegroup| grep agent | grep public | awk '{print $2}')
#azure network nsg rule create -g $resourcegroup -a $nsgName -n $rulename-rule -c Allow -p Tcp -r Inbound -y $rulenumber -f Internet -u $appPort
