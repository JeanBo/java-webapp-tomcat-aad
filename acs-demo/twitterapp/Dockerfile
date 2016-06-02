FROM centos
MAINTAINER chvugrin@microsoft.com
RUN  yum -y install  java-1.7.0-openjdk
COPY build/libs/twitterapp-0.1.0.jar /opt
CMD ["java","-jar","/opt/twitterapp-0.1.0.jar"]
