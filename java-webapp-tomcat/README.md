# Java WebApp on Tomcat

## What is it

A simple java webapp that demonstrates the following:
* simple counter, using jsf 
* simple tomcat authentication, using the tomcat-users.xml 
* automatic creation of docker container using maven plugin

## HowTO

Create and build your app with the following command:
* mvn clean install docker:build
* this will create a docker image named: cvugrinec/java-webapp-tomcat:1.0 (see pom.xml, the docker-maven-plugin part)
* run the docker image with:  docker run -p 8080:8080 cvugrinec/java-webapp-tomcat:1.0
* you can change/ add users by editing the following file:  src/main/tomcat/tomcat-users.xml 
* access the counter app with the following url: http://localhost:8080/java-webapp-tomcat
* you can access the secret part of the app (see the web.xml) by accessing the following url: http://localhost:8080/java-webapp-tomcat/secure

## How does it work

* you can see the maven configuration in the pom.xml file
* the docker images is being build by the maven plugin: io.fabric.docker-maven-plugin
* the docker-maven-plugin configures the following:
  * builds the image on an inherrited other image configured in the from tag
  * the assemby.xml file in the src/main/docker folder container the references to files that need to be copied from local to the docker image
  * the runCmd tags do commands on the newly created docker image 
* authenication is configured in the src/main/webapp/WEB-INF/web.xml file
* the web.xml file refers to a realm that has been configured in the tomcat-users configuration file
