#!/bin/bash

read -p "Press enter to stop the server..." press
echo ".";
echo ".";
echo ".";
echo ".";
cd ../
sudo docker compose down
sleep 3
echo "=============> SERVER STOPPED - PORT: 8081";
