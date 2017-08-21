import pytest
from . import globals as gbl
import af_support_tools

@pytest.fixture(scope='module', autouse=True)
def populate_globals():
    my_data = {'dne_base_url': '', 'add_node_url': '', 'add_node_resp': '', 'preprocess_workflow_id': '', 'workflow_id': '', 'uuid': ''}
    # Get vars from env ini file
    env_file = 'env.ini'
    host = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS', property='hostname')
    gbl.my_data = my_data  # Assign the master value to the global before each test

@pytest.fixture(scope='module', autouse=True)
def dne_paqx_base_url():
    my_data = {'dne_base_url': '', 'add_node_url': '', 'add_node_resp': '', 'preprocess_workflow_id': '', 'workflow_id': '', 'uuid': ''}
    # Get vars from env ini file
    env_file = 'env.ini'
    host = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS', property='hostname')
    protocol = 'http://'
    port = '8071'
    endpoint = '/dne'
    url = protocol + host + ':' + port + endpoint
    my_data['dne_base_url'] = url
    return url

@pytest.fixture(scope='module', autouse=True)
def add_node_url(dne_paqx_base_url):
    my_data = {'dne_base_url': '', 'add_node_url': '', 'add_node_resp': '', 'preprocess_workflow_id': '', 'workflow_id': '', 'uuid': ''}
    # Get vars from env ini file
    env_file = 'env.ini'
    host = af_support_tools.get_config_file_property(config_file=env_file, heading='Base_OS', property='hostname')
    endpoint = '/nodes'
    url = dne_paqx_base_url + endpoint
    my_data['add_node_url'] = url

@pytest.fixture(scope='module', autouse=True)
def restart_dne_paqx_log():
    # TBD
    pass
