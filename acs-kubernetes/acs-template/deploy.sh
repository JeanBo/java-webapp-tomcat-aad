azure login
azure account show
echo "enter subscription id"
read subid
azure account set --name $subid


echo "enter resourcegroup name"
read rgroupname
echo "enter deployment name"
read dname

azure group create \
    --name=$rgroupname \
    --location="east us>"

azure group deployment create \
    --name=$dname \
    --resource-group="$rgroupname" \
    --template-file="azuredeploy.json" \
    --parameters-file="azuredeploy.parameters.json"
