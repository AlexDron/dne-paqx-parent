UPSTREAM_TRIGGERS = getUpstreamTriggers([
    "common-dependencies",
    "common-client-parent",
    "common-messaging-parent",
    "compute-capabilities-api",
    "engineering-standards-service-parent",
    "hdp-capability-registry-client-parent",
    "system-integration-sdk",
    "virtualization-capabilities-api"
])

MAVEN_PHASE = "install"
if (env.BRANCH_NAME ==~ /master|develop|release\/.*/) {
    MAVEN_PHASE = "deploy"
}

pipeline { 
    parameters {
        string(name: 'dockerImagesDel', defaultValue: 'true')
        string(name: 'dockerRegistry',  defaultValue: 'docker-dev-local.art.local')
        string(name: 'dockerImageTag',  defaultValue: '${BRANCH_NAME}.${BUILD_NUMBER}')
    }
    triggers {
        upstream(upstreamProjects: UPSTREAM_TRIGGERS, threshold: hudson.model.Result.SUCCESS)
    }
    agent {
        node {
            label 'maven-builder'
            customWorkspace "workspace/${env.JOB_NAME}"
        }
    }
    environment {
        GITHUB_TOKEN = credentials('github-02')
    }
    options { 
        skipDefaultCheckout()
        buildDiscarder(logRotator(artifactDaysToKeepStr: '30', artifactNumToKeepStr: '5', daysToKeepStr: '30', numToKeepStr: '5'))
        timestamps()
        disableConcurrentBuilds()
    }
    tools {
        maven 'linux-maven-3.3.9'
        jdk 'linux-jdk1.8.0_102'
    }
    stages {
        stage('Checkout') {
            steps {
                doCheckout()
            }
        }
        stage("Build") {
            steps {
                sh "mvn clean ${MAVEN_PHASE} -Dmaven.repo.local=.repo -DskipDocker=false -PbuildDockerImageOnJenkins -Ddocker.registry=${params.dockerRegistry} -DdockerImage.tag=${params.dockerImageTag} -DdeleteDockerImages=${params.dockerImagesDel}"
            }
        }
        stage('Record Test Results') {
            steps {
                junit '**/target/*-reports/*.xml'
            }
        }
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/*.rpm', fingerprint: true 
            }
        }
        stage('Upload to Repo') {
            steps {
                uploadArtifactsToArtifactory()
            }
        }
        stage('SonarQube Analysis') {
            steps {
                doSonarAnalysis()    
            }
        }
        stage('Third Party Audit') {
            steps {
                doThirdPartyAudit()
            }
        }
        stage('PasswordScan') {
            steps {
                doPwScan()
            }
        }
        stage('Github Release') {
            steps {
                githubRelease()
            }
        }
        stage('NexB Scan') {
            steps {
                sh 'rm -rf .repo'
                doNexbScanning()
            }
        }
        stage('Run pytest Scanner') {
          steps {
            runPyTestScanner()
          }
    	}
    }
    post {
        always {
            cleanWorkspace()   
        }
        success {
            successEmail()
        }
        failure {
            failureEmail()
        }
    }
}
