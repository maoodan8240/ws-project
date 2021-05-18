#!/bin/sh


source ./readModule.sh
# 注意和 ./readModule.sh 的区别 ，使用${#serverNames} 将为空


################################################################################################
#                   无须修改        
################################################################################################


function controlFunc() {
    local idxInner=$1
    local commondInner=$2
    local name=${serverNames[$idxInner]}
    echo -e "\n\n-------------------------------------模块["$name"]------------------------------"
    local commondInnerNew=${commondInner}" "${name}
    echo "执行的命令为: "${commondInnerNew}
    eval ${commondInnerNew}
    ((idxInner++))
    if [ $idxInner -ge ${#serverNames[*]} ]
    then
        return 0
    fi
    controlFunc "$idxInner" "$commondInner"
}

function oneControlFunc() {
    local idxOuter=0
    local commondOuter=$1
    controlFunc "$idxOuter" "$commondOuter"
}

################################################################################################

####  命令0 开始
rm -rf updateServerFiles
rm -rf updateServerFiles.zip
mkdir -p updateServerFiles/data/tab

cd ./relationship
mvn clean
mvn compile
mvn package
mvn install
cd ..

cd ./gameServer
mvn clean
mvn compile
mvn package
cd ..

cd ./aggregator
mvn clean
mvn compile
mvn package
cd ..

#cd ./ws-gm-server
#mvn clean
#mvn compile
#mvn package
#cd ..


cd ./gameServer
rm -rf ./libs
mkdir ./libs
mvn dependency:copy-dependencies -DoutputDirectory=libs
\cp ./libs/ws-common-* ./../updateServerFiles/
\cp ./libs/ws-protos-* ./../updateServerFiles/
\cp ./libs/relationship-* ./../updateServerFiles/
cd ..


####  命令1 开始
function commond_1_Func() {
    serverName=$1
    
    cd ./${serverName}
    echo "============== copy ${serverName} jar ====================="
    cp ./target/*.jar ./../updateServerFiles/
    cd ..
}
oneControlFunc "commond_1_Func"

echo "============== copy tab ====================="
#cp -r /zww/d/所有SVN/双截龙/策划/数值/ServerTab/* ./updateServerFiles/data/tab/
cp -r /home/lee/data/share/project/策划/数值/ServerTab/* ./updateServerFiles/data/tab/

echo "============== zipping All libs ====================="
zip -r updateServerFiles.zip ./updateServerFiles






