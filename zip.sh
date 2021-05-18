#!/bin/sh
rm -rf libs.zip
cd ./gameServer
echo "============== zipping gameServer ====================="
zip -r gameServer.zip libs
zip -rj tab.zip ./target/classes/data/tab/*

cd ../gatewayServer
echo "============== zipping gatewayServer====================="
zip -r gatewayServer.zip libs

cd ../loginServer         
echo "============== zipping loginServer ====================="
zip -r loginServer.zip libs

cd ../sdk         
echo "============== zipping sdk====================="
zip -r sdk.zip libs

cd ../thirdPartyServer 
echo "============== zipping thirdPartyServer ====================="
zip -r thirdPartyServer.zip libs

cd ../clusterCenterServer  
echo "============== zipping clusterCenterServer ====================="
zip -r clusterCenterServer.zip libs

cd ../mongodbRedisServer
echo "============== zipping mongodbRedisServer ====================="
zip -r mongodbRedisServer.zip libs

cd ../
rm -rf libs/*
mkdir libs
mv ./gameServer/gameServer.zip ./libs
mv ./gameServer/tab.zip ./libs
mv ./gatewayServer/gatewayServer.zip ./libs
mv ./sdk/sdk.zip ./libs
mv ./thirdPartyServer/thirdPartyServer.zip ./libs
mv ./clusterCenterServer/clusterCenterServer.zip ./libs
mv ./mongodbRedisServer/mongodbRedisServer.zip ./libs
mv ./loginServer/loginServer.zip ./libs
echo "============== zipping All libs ====================="
zip -r libs.zip ./libs
