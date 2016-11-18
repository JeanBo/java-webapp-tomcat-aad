FROM cvugrinec/tomcat:1.0
MAINTAINER chvugrin@microsoft.com
EXPOSE 8080
COPY maven /usr/local/tomcat/webapps//
RUN mv /usr/local/tomcat/webapps/target/*.war /usr/local/tomcat/webapps/
RUN mv /usr/local/tomcat/webapps/src/main/tomcat/*.xml /usr/local/tomcat/conf
