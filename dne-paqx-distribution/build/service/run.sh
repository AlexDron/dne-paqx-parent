#!/bin/sh
#
# Copyright (c) 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
# Dell EMC Confidential/Proprietary Information
#

CONTAINERID=$(basename "$(cat /proc/1/cpuset)" | cut -c 1-12)
java  -Xms64m -Xmx192m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8555 -Dspring.profiles.active=production -Dcontainer.id=$CONTAINERID -Dlog4j.configuration=file:/opt/dell/cpsd/dne-paqx/conf/dne-paqx-log4j.xml -jar /opt/dell/cpsd/dne-paqx/lib/dne-paqx-web.jar
