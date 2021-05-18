#! /bin/sh

function readServerNames()
{
    i=0
    serverNames={}
    while read line
    do
        newLine=`echo ${line}|sed 's/[[:space:]]//g'`
        if [ "$newLine" =  "" ]
        then
            continue
        fi
        serverNames[$i]="${newLine}"
        ((i++))
    done < module.txt

    echo "服务个数: ${#serverNames[*]} > ${serverNames[*]}"
}

readServerNames

