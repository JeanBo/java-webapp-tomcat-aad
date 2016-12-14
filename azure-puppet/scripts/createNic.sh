#!/bin/sh
#=========================================
#
#       file:   createNic.sh
#       author: chvugrin@microsoft.com
#       description: creates a NIC based on template
#
#=========================================
echo "please enter the resourcegroup where your Puppet Master resides: "
echo "if you haven't created it yet, do this: https://ms.portal.azure.com/?flight=1#create/puppet.puppet-enterprise-2016-1puppet-enterprise-2016-1"
read rg
#azure group create $rg "westeurope"
azure group deployment create --template-file ../templates/az-puppetagent-nic.json --parameters-file ../templates/az-puppetagent-parameters.json $rg
