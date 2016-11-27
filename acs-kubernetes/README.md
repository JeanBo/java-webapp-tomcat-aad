# Azure Container Service Kubernetes Demo

see it in action: https://youtu.be/jxpW7xpAkGQ

## What is it

This project creates a docker image that enables you to rollout and manage a kubernetes cluster on azure. This automates the steps desribed in this article: https://github.com/Azure/acs-engine/blob/master/docs/kubernetes.md ...This way you can give a cool demo in no time, of course you can also deploy the cluster directly via this template: <a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FAzure%2Fazure-quickstart-templates%2Fmaster%2F101-acs-kubernetes%2Fazuredeploy.json" target="_blank"><img src="http://azuredeploy.net/deploybutton.png"/></a>
...but then you miss all the cool automation/scripting stuff.
From this docker image you can access your azure subscription and you have all the kubernetes tools installed in order to do management on your cluster.

## HowTO

You need to be a able to create/run a docker image/container on your local machine.
* git clone https://github.com/cvugrinec/microsoft.git
* cd acs-kubernetes
* docker build -t [YOUR DOCKER IMAGE NAME] .   (for eg docker build -t cvugrinec/acs-kube-local:1.0 .)
* docker run -it [YOUR DOCKER IMAGE NAME] bash
* cd /opt
* run this script: ./create-kubecluster.sh
* login to azure
* enter your subscription ID (please wait a little)
* enter a resourcegroup name
* the script will do everything for you...
* lastly you need to copy the ~/.kube/config file from the remote kubernetes master to your docker image...then you can manage the cluster from your docker image.
* if you like to access the proxy...this is unfortunately not possible from your docker image...have a look a the youtube flic on how to access this
* lastly play with kubernetes.. the script directs you to a folder with some examples  

## How does it work

the create-kubecluster.sh does the following:
* login to azure (if you do this from a buid machine, you will authenticate using a certificate)
* it will create a resourcegroup with cli
* the acs-engine creates templates based on the  /opt/acs-kube-template.json template (you can edit the values yourself as well)
* the endresult will be:
  * azuredeploy.json
  * azuredeploy.parameters.json
* with these files genereated files you can create the ACS kubernetes cluster with the azure cli:
  * azure group deployment create 
