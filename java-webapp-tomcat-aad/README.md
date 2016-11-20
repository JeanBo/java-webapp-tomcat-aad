# Java WebApp on Tomcat using Azure Active Directory

see it in action: https://youtu.be/i61I3muADDA

## What is it

A simple java webapp that demonstrates the following:
* simple counter, using jsf 
* tomcat authentication, using a custom authenticator based on JAAS framework and using com.microsoft.azure.adal4j lib
* automatic creation of docker container using docker-maven-plugin

## HowTO

Create and build your app with the following command:
* mvn clean install docker:build
* this will create a docker image named: cvugrinec/java-webapp-tomcat-aad:1.1 (see pom.xml, the docker-maven-plugin part)
* run the docker image with:  docker run -p 8080:8080 cvugrinec/java-webapp-tomcat-aad:1.1
* this image is based on cvugrinec/tomcat:1.8 which contains a slightly modified tomcat install:
  * in /usr/local/tomcat/conf there is a jaas.config ( src is in src/main/resources/jaas.config )
  * in $JAVA_HOME/lib/security/java.security I added the following line: login.config.url.1=file:/usr/local/tomcat/conf/jaas.config
* create an application in Azure AD using the classic portal, the role mapping in the new portal didnt work for me
* in the same directory where the app service principal is created create a user
* to see the counter: go to the following url: http://localhost:8080/java-webapp-tomcat-aad
* you can access the secret part of the app (see the web.xml) by accessing the following url: http://localhost:8080/java-webapp-tomcat-aad/secure
* you can login with the user you just created in AAD

## How does it work

* you can see the maven configuration in the pom.xml file
* the docker images is being build by the maven plugin: io.fabric.docker-maven-plugin
* the docker-maven-plugin configures the following:
  * builds the image on an inherrited other image configured in the from tag
  * the assemby.xml file in the src/main/docker folder container the references to files that need to be copied from local to the docker image
  * the runCmd tags do commands on the newly created docker image 
* authenication is configured in the src/main/webapp/WEB-INF/web.xml file
* this project also contains a context.xml file (src/main/webapp/META-INF/context.xml) which does the following:
  * defines that the app uses a custom authentication using the JAAS framework : org.apache.catalina.realm.JAASRealm"
  * the collection of users (REALM) is within the JAAS realm and handled by the app with appName=WebAppTcLogin
* this appName is mapped to a context which is configured in the $JAVA_HOME/lib/security/java.security to point to the jaas.config file, in this file you will see that the WebAppTcLogin is mapped to the nl.microsoft.adalauth.AzureADLoginModule class.
