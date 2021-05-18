#!/bin/zsh

cd /home/lee/project/gameServer
echo "updating gameServer....."
git pull

cd /home/lee/project/loginServer
echo "updating .....loginServer"
git pull

cd /home/lee/project/aggregator
echo "updating aggregator....."
git pull

cd /home/lee/project/gatewayServer 
echo "updating gatewayServer....."
git pull

cd /home/lee/project/relationship
echo "updating relationship....."
git pull

cd /home/lee/project/clusterCenterServer  
echo "updating clusterCenterServer....."
git pull

