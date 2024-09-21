#!/bin/bash

read -p "Press enter to continue..." press
echo "=============> START BUILD DOCKER ON THE LOCAL SERVER - PORT: 8081";
echo ".";
echo ".";
echo ".";
echo ".";
cd ../
mvn clean install
sudo docker compose up -d
echo "================> DOCKER STARTED <===============";
echo ".";
echo ".";
echo ".";
echo ".";
sleep 3
echo "================> NGROK PUBLISH SERVER STARTING...";
echo ".";
echo ".";
echo ".";
echo ".";
ngrok tunnel --label edge=edghts_2mNpsgWe53JpvjWH50GrrtAspg7 http://localhost:8081