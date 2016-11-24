#!//bin/bash

if [[ ! -f ~/.ssh/id_rsa.pub ]]
then
  echo "Could not find id_rsa.pub file....please create it with ssh-keygen -t rsa"
  exit 1
else
  echo "have you pasted your SSH KEY in the azuredeploy.parameters.json file? , type Y for yes"
  read ask1
  if [[ $ask1 != "Y" ]]
  then
    echo "exiting..."
    exit 0
  fi
fi

#azure login
azure account show
#echo "enter subscription id"
#read subid
#azure account set --name $subid

#echo "enter resourcegroup name"
#read rgroupname
dname=`uuidgen`


sed -in 's/ENDPOINTPREFIX/'$dname'/' azuredeploy.parameters.json


exit 1
azure group create \
    --name=$rgroupname \
    --location="east us"

azure group deployment create \
    --name=$dname \
    --resource-group="$rgroupname" \
    --template-file="azuredeploy.json" \
    --parameters-file="azuredeploy.parameters.json"
