#!/bin/bash

{

ip_addr=$(ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | cut -f1  -d'/')

# Initial Variable
war_name=DigitalLending
installed_tomcat_dir=/usr/local/tomcat8

# Deploy application to tomcat directory
cp /home/ec2-user/war/* $installed_tomcat_dir/webapps/

# Check tomcat status
result_server=`ps -ef | grep tomcat | grep java | wc -l`
result_port=`netstat -na | grep 8080 | wc -l`

echo "Restart tomcat server"
if [ $result_server != 0 ] && [ $result_port != 0 ]
then
 echo " -------- Apache Tomcat Running -------- "
 cd $installed_tomcat_dir/bin
 ./shutdown.sh
 sleep 10
 ./startup.sh
else
 echo " -------- Apache Tomcat Stopped -------- "
 cd $installed_tomcat_dir/bin
 ./startup.sh
 echo " -------- Apache Tomcat is starting -------- "
fi

# Delay 100 second
echo "Waiting..."
sleep 100

response=$(curl -sb -H "Accept: application/json" "http://127.0.0.1:8080/$war_name");
echo "response: '$response'"
if [ `echo $response | grep -c "Alive" ` -gt 0 ]
then
  echo "Your application is available now."
else
  echo "Your application '$war_name' is not available because cannot start in [$ip_addr], please see log." 1>&2
  exit 1
fi

} 2>&1 | tee "/home/ec2-user/scripts/start_log_$(date +%Y%m%d_%H%M%S).log"