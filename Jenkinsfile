pipeline {    
    agent {
        node{
            label 'maven-builder'
            customWorkspace "workspace/${env.JOB_NAME}"
            }
    }
    environment {
        GITHUB_TOKEN = credentials('github-02')
    }
    options { 
        buildDiscarder(logRotator(artifactDaysToKeepStr: '30', artifactNumToKeepStr: '5', daysToKeepStr: '30', numToKeepStr: '5'))
        timestamps()
    }
    tools {
        maven 'linux-maven-3.3.9'
        jdk 'linux-jdk1.8.0_102'
    }
    stages {
        stage('Compile') {
            steps {
                sh "mvn install -DskipTests=true -DskipITs"
            }
        }
        stage('Unit Testing') {
            steps {
                sh "mvn test"
            }
        }
        stage('PasswordScan') {
            steps {
                doPwScan()
            }
        }
        stage('Deploy') {
            when {
                expression {
                    return env.BRANCH_NAME ==~ /develop|release\/.*/
                }
            }
            steps {
                sh "mvn deploy -DskipTests=true -DskipITs"
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') { 
                    doSonarAnalysis()    
                }
            }
        }
        stage('Third Party Audit') {
            steps {
                doThirdPartyAudit()
            }
        }
        stage('Github Release') {
            steps {
                githubRelease()
            }
        }
        stage('NexB Scan') {
            steps {
                doNexbScanning()
            }
        }
    }
    post {
        always{
            step([$class: 'WsCleanup'])   
        }
        success {
            emailext attachLog: true, 
                body: 'Pipeline job ${JOB_NAME} success. Build URL: ${BUILD_URL}', 
                recipientProviders: [[$class: 'CulpritsRecipientProvider']], 
                subject: 'SUCCESS: Jenkins Job- ${JOB_NAME} Build No- ${BUILD_NUMBER}', 
                to: 'pebuildrelease@vce.com'            
        }
        failure {
            emailext attachLog: true, 
                body: 'Pipeline job ${JOB_NAME} failed. Build URL: ${BUILD_URL}', 
                recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider'], [$class: 'FailingTestSuspectsRecipientProvider'], [$class: 'UpstreamComitterRecipientProvider']], 
                subject: 'FAILED: Jenkins Job- ${JOB_NAME} Build No- ${BUILD_NUMBER}', 
                to: 'pebuildrelease@vce.com'
        }
    }
}
