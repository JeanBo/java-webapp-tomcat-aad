# Documentation for the Azure Container Service Demo (mesos template)

In the Azure Container Service Demo I demo the following
 * Create an ACS environment, use the source 01-createAcsDcos.sh
   * I start with this because it takes time to create the acs environment..during the create I will show docker basics
   * Show how you can start this script from a CD pipeline with jenkins as example
 * Docker, start a local twitterapp using the sources in acs-demo/twitterapp 
   * a lightweight springboot app, explain about the dockerfile
   * go into the dockerfile, commit, volumes, tag en submit to repository
 * Docker Compose, using the sources in acs-demo/java-webapp-redisdemo
   * explain about relationship between containers with link 
   * docker-compose up
 * Now Explain about complexity of containers in productionlandscape --> container orchestation required 
   * bringing the java-webapp-redisdemo to production with mesos
     * load the marathon-lb
     * redis.json, explain about persistancy and this (non clustered solution) about linking with labels and healthcheck
     * java-webapp-redisdemo.json, explain this technique new Jedis("1.1.1.1") ...also explain more desired scenarios
     * show loadbalancer function by accessing the webapp
 * Now go for the PAAS experience
   * install from universe the following:
     * cassandra, keyvalue database
     * zeppelin, notebook
     * kafka, messenger for putting stuff in zookeeper
     * tweeter.json ./03-deployApp.sh tweeter.json chris-acsdemo1-northeurope 10000 503
     * go to the app 
     * go to notepad...http://localhost/service/zeppelin/#/
    
