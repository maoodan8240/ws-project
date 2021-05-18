#!/usr/bin/expect

send_user "please choose server address:\n(1) 106.75.33.217 out server\n(2) 192.168.0.243 in server\n"

#接收第一个参数,并设置IP
set ip ""
#接收第二个参数,并设置密码
set password ""

expect {
"1" {
  set ip "106.75.33.217"
  set password "hd@awo.cn"
  }
"2" {
   set ip "192.168.0.243"
   set password "com.ws123"
  }
}

#设置超时时间
set timeout 300

set file_1 updateServerFiles.zip

set file_2 /unzip-dir
#发送ssh请滶
spawn \scp $file_1 root@$ip:$file_2
#返回信息匹配
expect {
#第一次ssh连接会提示yes/no,继续
"*yes/no" { send "yes\r"; exp_continue}
#出现密码提示,发送密码
"*password:" { send "$password\r" }
}
#交互模式,用户会停留在远程服务器上面.
expect "]*"
spawn ssh root@$ip
expect "password:"
send "$password\r"
expect "]#"
send "cd /unzip-dir\r"
expect "dir]#"
send "./deploy.sh > deploy.log\r"
expect "]#"
exit
interact

