for the demo this will suffice:
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)
docker run -d -p 6379:6379 --name=redis redis:latest
docker run -d -p 8080:8080 --link=redis cvugrinec/java-webapp-redisdemo:1.1
