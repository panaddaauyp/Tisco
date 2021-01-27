#!/bin/bash

war_directory=/home/ec2-user/war/
[ -d "$war_directory" ] || mkdir $war_directory

script_directory=/home/ec2-user/scripts/
[ -d "$script_directory" ] || mkdir $script_directory

{

#ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | cut -f1  -d'/'

# Initial Variable
war_name=dep-api
installed_tomcat_dir=/usr/local/tomcat8

# Remove old war and script
rm -fr /home/ec2-user/war/*
rm -fr /home/ec2-user/scripts/*

# Check tomcat status
result_server=`ps -ef | grep tomcat | grep java | wc -l`
result_port=`netstat -na | grep 8080 | wc -l`

echo "Stop tomcat server"
if [ $result_server != 0 ] && [ $result_port != 0 ]
then
 echo "Process to stop tomcat server"
 cd $installed_tomcat_dir/bin
 ./shutdown.sh
fi

echo "Uninstall application '"$war_name"'"
rm -fr $installed_tomcat_dir/webapps/$war_name $installed_tomcat_dir/webapps/$war_name.war

} 2>&1 | tee "/home/ec2-user/scripts/clean_log_$(date +%Y%m%d_%H%M%S).log"
