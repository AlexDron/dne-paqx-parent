#!/usr/bin/python
# Copyright Â© 01 April 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
#
# Author: russed5
# Revision: 1.2
# Code Reviewed by:
# Description: Verify that the DNE-PAQX service communicates over TLS to the Rabbitmq AMQP bus

import pytest
import requests
import json
import time
import af_support_tools


##############################################################################################

@pytest.fixture(scope="module", autouse=True)
def load_test_data():

     import cpsd
     global cpsd

     # Set config ini file name
     global env_file
     env_file = 'env.ini'

     # Test VM Details
     global ipaddress
     ipaddress = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS', property='hostname')
     global cli_username
     cli_username = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS',
                                                              property='username')
     global cli_password
     cli_password = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS',
                                                              property='password')

##############################################################################################

@pytest.mark.dne_paqx_parent_mvp
@pytest.mark.dne_paqx_parent_mvp_extended
def test_dnePAQX_container_using_AMQP_over_TLS():
    """ Verify DNE-PAQX service communicates with rabbitmq over TLS

    this test uses the  processId of the dne paqx container
    to verify that the network connection to rabbitmq for that container
    is using the rabbitmq tls port"""

    try:

        ssl_listen_port = getRabbitmqSSLport(ipaddress)
        assert ssl_listen_port, "Error : It was not possible to retrieve the rabbitmq SSL port "

        # For the dne paqx, use 'docker network inspect' to ascertain its docker network IP address
        # and use this ip address with the 'netstat' command to find the dne-rabbitmq port-pairing.
        # example return text would be :
        # tcp6       0      0 172.17.0.1:5671         172.21.0.2:47338        ESTABLISHED 987/beam.smp
        connectionPorts = getDockerNetworkConnectionPorts('symphony-dne-paqx')

        # shouldn't be more then 1 line returned, but just in case we split them
        connectionsText = connectionPorts.splitlines()

        # if no lines were returned, then there is an error
        assert connectionsText, 'no rabbitmq connection detected for DNE PAQX'

        for line in connectionsText:
            # check that it is the correct TLS port that is listed for the rabbitmq-dne connection
            assert ssl_listen_port in line, "The DNE PAQX is not using the SSL Port"


    except Exception as err:

        # Return code error (e.g. 404, 501, ...)

        print(err)

        print('\n')

        raise Exception(err)

#####################################################################################################
@pytest.mark.skip(reason="currently failing need to investigate")
@pytest.mark.dne_paqx_parent_mvp
@pytest.mark.dne_paqx_parent_mvp_extended
def test_DNEpaqx_AMQP_data_is_encrypted():
    """ Verify DNE-PAQX network status reads amqps.

    1. get the processId associated with the DNE paqx
    2. use the returned processId to filter the output of the 'lsof' utility
    3. check that the listed AMQP connection is secure by checking for the phrase 'amqps' in the listing"""
    #

    # use the getDockerNetworkConnectionPorts fucntion , with the 'processId' flag, to indicate the
    # processID should be returned.
    processId = getDockerNetworkConnectionPorts('symphony-dne-paqx', "processId")
    assert processId, "There was no PID found for the DNE PAQX"
    # use the getDockerNetworkConnectionPorts function , without a specific flag, to indicate the
    # connection ports should be returned. This is returned in the form :
    # tcp6       0      0 172.17.0.1:5671         172.21.0.2:47338        ESTABLISHED 987/beam.smp
    connectionPortText = getDockerNetworkConnectionPorts('symphony-dne-paqx')
    assert connectionPortText, "There was no rabbitmq connection found for the DNE PAQX"

    # split the dne-port out of the returned text, to be used in the follwoing 'lsof' command
    peerPort = getPeerPortFromNetstatOutput(connectionPortText)
    assert peerPort, "The peer port for the rabbitmq connection could not be obtained"

    # using lsof, we expect to see output similar to below and are particularly interested in the 'amqps' text
    #    java    4969 root   21u  IPv6    57839    0t0    TCP localhost:48786->localhost:amqps (ESTABLISHED)
    commandCheckEncryption = "lsof -p " + processId.rstrip() + " | grep [E]STABLISHED | grep amqps" + " | grep " + peerPort.rstrip()

    return_text = af_support_tools.send_ssh_command(
        host=ipaddress,
        username=cli_username,
        password=cli_password,
        command=commandCheckEncryption,
        return_output=True)

    assert return_text, "Error : no amqps connection was discovered for the dne paqx"

#####################################################################################################

def getRabbitmqSSLport(ip):
    """ Retrieve the rabbitmq ssl port via the rabbitmq http api."""

    apipath = "/api/overview"
    my_url = 'http://' + ipaddress + ':15672' + apipath

    try:
        url_response = requests.get(my_url, auth=(cpsd.props.rmq_username,cpsd.props.rmq_password))
        url_response.raise_for_status()

        # 200
        data = json.loads(url_response.text)
        for i in data['listeners']:
            if i['protocol'] == 'amqp/ssl':
                ssl_port = i['port']

        if ssl_port:
            portToReturn = str(ssl_port)
        else:
            portToReturn = None

        return portToReturn

    except Exception as err:
        # Return code error (e.g. 404, 501, ...)
        print(err)
        print('\n')
        raise Exception(err)


#########################################################################################################

def getPeerPortFromNetstatOutput(netstatText):
    """ A function to extract and return the non-rabbitmq peer port from specific netstat output text.

    the expected format of text is :
    tcp6       0      0 172.17.0.1:5671         172.21.0.2:47338        ESTABLISHED 987/beam.smp
    In this sample case, '47388' would be returned """

    portstext = netstatText.split(':')
    peer_port_raw = portstext[2].split(' ')
    peer_port = peer_port_raw[0]

    return peer_port

######################################################################################################

def getDockerNetworkConnectionPorts(containerName, label="ports"):
    """ A function to determine the docker network ipaddress of a PAQX container.

    Docker commands are used to determine the IP Address of the PAQX container.
    'netstat' is then used to return the connection details for that IP Address.
    If a second parameter is  passed to this function, and is equal to 'processId', then the connection details
    are further broken down to return just the processID.
    Eg. from the sample data below, the process ID = 987 would be returned
    tcp6       0      0 172.17.0.1:5671         172.21.0.2:47338        ESTABLISHED 987/beam.smp"""

    commandGetContainerId = "docker ps | grep " + containerName + " | awk '{print $1}' "
    containerId = af_support_tools.send_ssh_command(
                host=ipaddress,
                username=cli_username,
                password=cli_password,
                command=commandGetContainerId,
                return_output=True)

    commandGetIPAddress = "docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' " + containerId.rstrip()
    ipAddressText = af_support_tools.send_ssh_command(
                host=ipaddress,
                username=cli_username,
                password=cli_password,
                command=commandGetIPAddress,
                return_output=True)

    commandGetContainerConnectionPort = "netstat -tupn | grep " + ipAddressText.rstrip() + ": | grep -v 8500 | grep -v 8071"
    connectionPorts = af_support_tools.send_ssh_command(
                host=ipaddress,
                username=cli_username,
                password=cli_password,
                command=commandGetContainerConnectionPort,
                return_output=True)

    if label == "processId" :
        processtext = connectionPorts.split("ESTABLISHED")
        process_raw = processtext[1].split('/')
        processId = process_raw[0]
        return processId
    else:
        return connectionPorts

#####################################################################################################

