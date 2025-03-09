exathreat-portal
----------------
Online customer portal where Clients can see their network events, view SEIM intelligence, create charts and graphs, search the ElasticSearch index by keywords, etc.


To build your application:
*. ./gradlew clean build

To build your docker image: 
*. docker build -t exathreat-portal .

To list your docker images:
*. docker image list | grep exathreat-portal

To run your docker image (creates a new container):
*. docker run --name exathreat-portal -e PROFILE=aws -e DB_URL=192.168.1.82 -e DB_PORT=3306 -e DB_NAME=exathreat -e DB_USERNAME=portal -e DB_PASSWORD=xsH4RbKADYLr -e ES_DOMAIN=192.168.1.82 -e ES_PORT=9200 -e ES_SCHEME=http -e WEB_DOMAIN=localhost:8080 -p 8080:8080 exathreat-portal

To get an existing container:
*. docker ps

To start an existing container (preserve data):
*. docker start <CONTAINER_NAME>

To stop a container:
*. docker stop <CONTAINER_NAME>

To remove your dangling images:
*. docker rmi -f $(docker images --filter dangling=true)

To authenticate docker to your AWS ECR registry:
*. aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com

---

To tag your docker image to your ECR repository in AWS:
*. docker tag exathreat-portal 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-portal

To push your tagged docker image to your ECR repository in AWS:
*. docker push 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-portal

To pull your docker image from your ECR repository in AWS:
*. docker pull 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-portal
