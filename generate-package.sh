#!/bin/sh

cd ./relationship
mvn clean
mvn compile
mvn package
mvn install
cd ..

#cd ./gameServer
#mvn clean
#mvn compile
#mvn package
#mvn install
#cd ..

cd ./aggregator
mvn clean  
mvn compile  
mvn package
mvn install

cd ..

if [ -d "./libs" ]; then
  echo "=======remove libs======"
  rm -rf ./libs
fi

echo "=======mkdir libs======"
mkdir ./libs

if [ -d "server-libs" ]; then
echo "=======remove server-libs======"
rm -rf ./server-libs
fi

echo "=========mkdir server-libs=========="
mkdir ./server-libs



echo "执行clusterCenterServer..."
cd clusterCenterServer
rm -rf ./libs
mvn dependency:copy-dependencies -DoutputDirectory=libs
cp target/*.jar ./libs/
cp target/clusterCenterServer*.jar ./../server-libs/


echo "执行gameServer..."
cd ../gameServer
rm -rf ./libs
mkdir ./libs
mvn dependency:copy-dependencies -DoutputDirectory=libs
cp target/*.jar ./libs/
cp ./libs/*.jar ./../libs/
cp target/gameServer*.jar ./../server-libs/


#echo "执行chatServer..."
#cd ../chatServer
#rm -rf ./libs
#mkdir ./libs
#mvn dependency:copy-dependencies -DoutputDirectory=libs
#cp target/*.jar ./libs/
#cp target/chatServer*.jar ./../server-libs/

echo "执行gatewayServer..."
cd ../gatewayServer
rm -rf ./libs
mkdir ./libs
mvn dependency:copy-dependencies -DoutputDirectory=libs
cp target/*.jar ./libs/
cp target/gatewayServer*.jar ./../server-libs/

echo "执行loginServer..."
cd ../loginServer
rm -rf ./libs
mkdir ./libs
mvn dependency:copy-dependencies -DoutputDirectory=libs
cp target/*.jar ./libs/
cp target/loginServer*.jar ./../server-libs/

#echo "执行logServer..."
#cd ../logServer
#rm -rf ./libs
#mkdir ./libs
#mvn dependency:copy-dependencies -DoutputDirectory=libs
#cp target/*.jar ./libs/
#cp target/logServer*.jar ./../server-libs/

echo "执行mongodbRedisServer..."
cd ../mongodbRedisServer
rm -rf ./libs
mkdir ./libs
mvn dependency:copy-dependencies -DoutputDirectory=libs
cp target/*.jar ./libs/
cp target/mongodbRedisServer*.jar ./../server-libs/

echo "执行particularFunctionServer..."
cd ../particularFunctionServer
rm -rf ./libs
mkdir ./libs
mvn dependency:copy-dependencies -DoutputDirectory=libs
cp target/*.jar ./libs/
cp target/particularFunctionServer*.jar ./../server-libs/

echo "执行sdk..."
cd ../sdk
rm -rf ./libs
mkdir ./libs
mvn dependency:copy-dependencies -DoutputDirectory=libs
cp target/*.jar ./libs/
cp ./libs/*.jar ./../libs/
cp target/sdk*.jar ./../server-libs/

echo "执行thirdPartyServer..."
cd ../thirdPartyServer
rm -rf ./libs
mkdir ./libs
mvn dependency:copy-dependencies -DoutputDirectory=libs
cp target/*.jar ./libs/
cp target/thirdPartyServer*.jar ./../server-libs/

rm -rf ~/project/libs/thirdPartyServer*.jar
rm -rf ~/project/libs/gameServer*.jar
rm -rf ~/project/libs/clusterCenterServer*.jar
rm -rf ~/project/libs/gatewayServer*.jar
rm -rf ~/project/libs/mongodbRedisServer*.jar
rm -rf ~/project/libs/particularFunctionServer*.jar
rm -rf ~/project/libs/loginServer*.jar
rm -rf ~/project/libs/logServer*.jar
rm -rf ~/project/libs/sdk*.jar
rm -rf ~/project/libs/relationship*.jar



wrapperlibs="~/project/gatewayServer/libs/libsigar-amd64-linux.so"

if [ ! -f "$wrapperlibs" ]; then
  echo "================can not find wrapper-libs========="
  echo "=====================copying libs================="
 cp ~/wrapper-libs/* ~/project/gameServer/libs/
 cp ~/wrapper-libs/* ~/project/clusterCenterServer/libs/
 cp ~/wrapper-libs/* ~/project/gatewayServer/libs/
 cp ~/wrapper-libs/* ~/project/thirdPartyServer/libs/
 cp ~/wrapper-libs/* ~/project/mongodbRedisServer/libs/
 cp ~/wrapper-libs/* ~/project/particularFunctionServer/libs/
 cp ~/wrapper-libs/* ~/project/loginServer/libs/
 #cp ~/wrapper-libs/* ~/project/logServer/libs/
 cp ~/wrapper-libs/* ~/project/sdk/libs/
 cp ~/wrapper-libs/* ~/project/libs/
# cp ~/wrapper-libs/* ~/project/chatServer/libs/
  echo "====================copy finish==================="
fi


if [ -d "/home/lee/com/ws/libs" ]; then
  rm -rf "/home/lee/com/ws/libs"
fi
  
if [ -d "/home/lee/com/ws/server-libs" ]; then
  rm -rf "/home/lee/com/ws/server-libs"
fi

mv ~/project/libs ~/project/server-libs ~/com/ws/


