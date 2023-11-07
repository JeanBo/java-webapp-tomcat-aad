
docker run -p 8080:8080 --mount type=bind,src=$(pwd)/resources/jaas.config,dst=/usr/local/tomcat/conf/jaas.config   chop/java-webapp-tomcat-aad