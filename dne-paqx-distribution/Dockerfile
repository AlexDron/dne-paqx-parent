#
# Copyright (c) 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
# Dell EMC Confidential/Proprietary Information
#
#  USAGE: (from .. dir)
#    docker build -f dne-paqx-distribution/Dockerfile --tag=dne-paqx dne-paqx
#

# Start from base image with Java 8
FROM java:8-jre-alpine

# Install the service file(s) to run
WORKDIR /opt/dell/cpsd/dne-paqx/

COPY target/dependency/lib/dne-paqx-web*.jar ./lib/dne-paqx-web.jar
COPY build/conf/* ./conf/
COPY build/service/run.sh ./bin/

ARG DELL_JAVA_VERSION=unknown
ARG BUILD_NUMBER=unknown
ARG GIT_COMMIT=unknown
ARG BUILD_TYPE=dev
ARG CPSD_SOFTWARE_VERSION=unknown

LABEL   com.dell.cpsd.version=$CPSD_SOFTWARE_VERSION\
        com.dell.cpsd.git_revision=$GIT_COMMIT\
        com.dell.cpsd.build_number=$BUILD_NUMBER\
        com.dell.cpsd.build_type=$BUILD_TYPE\
        com.dell.cpsd.java=$DELL_JAVA_VERSION
RUN mkdir ./logs \
&& chmod 755 ./bin/run.sh
      
EXPOSE 8071
EXPOSE 8555

ENTRYPOINT ["./bin/run.sh"]
