#!/bin/bash

# DockerHub username
DOCKERHUB_USERNAME="zsq0216"

# Image names and tags
IMAGES=(
  "my-local-mysql:latest"
  "api-gateway:0.0.1-SNAPSHOT"
  "poem-service:0.0.1-SNAPSHOT"
  "eureka-server:0.0.1-SNAPSHOT"
  "user-service:0.0.1-SNAPSHOT"
  "ai-chat-service:0.0.1-SNAPSHOT"
)

# DockerHub repository names (optional, can be same as image names)
REPOS=(
  "my-local-mysql"
  "api-gateway"
  "poem-service"
  "eureka-server"
  "user-service"
  "ai-chat-service"
)

# Login to DockerHub
echo "Logging in to DockerHub..."
docker login --username $DOCKERHUB_USERNAME

# Loop through images and push them to DockerHub
for i in ${!IMAGES[@]}; do
  IMAGE=${IMAGES[$i]}
  REPO=${REPOS[$i]}
  echo "Tagging image $IMAGE as $DOCKERHUB_USERNAME/$REPO"
  docker tag $IMAGE $DOCKERHUB_USERNAME/$REPO
  echo "Pushing image $DOCKERHUB_USERNAME/$REPO"
  docker push $DOCKERHUB_USERNAME/$REPO
done

echo "All images have been pushed to DockerHub."
