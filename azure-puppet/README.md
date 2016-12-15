# Documentation Azure Puppet

See it in action: XXXX,  sorry for the timegaps in the movie...had a busy day with a lot of interruptions ;)

## What is it

This demo shows you how to setup an initial puppet configuration on Azure with the scripts that reside in this repo.
Register the machines to the Puppet Master (see movie) . Create a group that identifies the machines and apply modules/templates on this group.
You can also write custom modules and make them availble in the puppet master.
The Puppet version used here is: 2016.1  Enterprise Edition.

## How to

* setup
  * create the puppet master
    * By following this link, https://ms.portal.azure.com/?flight=1#create/puppet.puppet-enterprise-2016-1puppet-enterprise-2016-1 or you can use this template file yourself: https://gallery.azure.com/artifact/20161101/puppet.puppet-enterprise-2016-1puppet-enterprise-2016-1.1.0.3/Artifacts/mainTemplate.json
  * create the 2 agents with the scripts
    * change the properties in the template file: templates/az-puppetagent-parameters.json
      * change the hostnames
      * change the internal and external FQDN to point to the puppetmaster you created earlier
    * per machine you need to register to the puppet master, you can find the link in the puppet master console under the unsigned Certificates tab. (not quite sure, but I expected this step to be done by the azure puppet agent extension). The command you have to execute on each machine will look something like this: curl -k https://puppetzdemo.eastus.cloudapp.azure.com:8140/packages/current/install.bash | sudo bash
  * after a refresh and a couple of minutes the machines will appear in the puppet mgmt console in the unsigned certificates tab. Make sure you confirm the requests.
  * define a group which applies to your machines
  * on this group define your template with available modules and classes 
  * if a class is not available...create your own module and upload it to the puppet master
    * on the puppet master, you can start with calling a scafolder: puppet module generate [your username]-[your module name]
    * make a tar.gz file of the module, for eg: xxxx.tar.gz
    * install the module: puppet module install [name of your tar.gz file]
