#!/bin/bash
export AZURE_STORAGE_CONNECTION_STRING=$(cat /opt/scripts/config/config.txt | grep azure_connection_string | sed 's/azure_connection_string=//')
filetoget=$1
wget $filetoget -O /opt/scripts/temp.zip
xxx=$(unzip /opt/scripts/temp.zip)
unzippedfile=$(echo $xxx | awk '{print $4}')
echo "unzipped file: "$unzippedfile
azure storage blob upload /$unzippedfile ahndata
rm -f /opt/scripts/temp.zip
rm -f /$unzippedfile
