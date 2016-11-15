FROM alpine
MAINTAINER chvugrin@microsoft.com
RUN apk --update add openjdk8-jre
COPY build/libs/fatjar-all-1.0.jar /etc
CMD ["/usr/bin/java","-jar","/etc/fatjar-all-1.0.jar"]
