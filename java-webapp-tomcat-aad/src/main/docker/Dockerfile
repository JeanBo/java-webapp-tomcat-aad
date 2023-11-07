FROM docker.io/library/tomcat:8.5
MAINTAINER jean@chop.co.za
EXPOSE 8080
RUN echo "login.config.url.1=file:/usr/local/tomcat/conf/jaas.config" >> /opt/java/openjdk/conf/security/java.security
RUN echo "Jean" > /opt/java/openjdk/conf/security/jean.test
CMD ["/usr/local/tomcat/bin/catalina.sh","run"]
