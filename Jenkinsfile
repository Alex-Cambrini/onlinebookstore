pipeline {
    agent any
    tools {
        maven 'Maven 3.9.10'
        jdk 'JDK 8'
        nodejs 'NodeGlobal'
    }
    options {
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
    }
    stages {
        stage('Checkout') {
            steps {
                echo 'Starting Checkout stage...'
                checkout scm
                echo 'Checkout completed.'
            }
        }
        stage('Build, Test & PMD Analysis') {
            steps {
                ansiColor('xterm') {
                    echo 'Starting Build, Test & PMD Analysis...'
                    timeout(time: 20, unit: 'MINUTES') {
                        sh 'mvn clean verify pmd:pmd'
                    }
                    echo 'Build, Test & PMD Analysis completed.'
                }
            }
        }
        stage('SCA - OWASP Dependency-Check') {
            steps {
                ansiColor('xterm') {
                    script {
                        echo 'Starting OWASP Dependency-Check...'    
                        dependencyCheck odcInstallation: 'Dependency-Check', additionalArguments: '--format HTML --out dependency-check-report'
                        echo 'OWASP Dependency-Check scan completed.'
                        archiveArtifacts artifacts: 'dependency-check-report/dependency-check-report.html', fingerprint: true
                    }
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                ansiColor('xterm') {
                    script {
                        echo 'Starting SonarQube analysis...'
                        retry(2) {
                            withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                                withSonarQubeEnv('LocalSonarQube') {
                                    sh 'mvn sonar:sonar -Dsonar.projectKey=onlinebookstore -Dsonar.login=$SONAR_TOKEN -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'
                                }
                            }
                        }
                        echo 'SonarQube analysis completed.'
                    }
                }
            }
        }
        stage('Security Gates Evaluation') {
            steps {
                echo 'Evaluating all security gates...'
                echo 'Waiting for SonarQube Quality Gate result...'
                script { sleep 15 }
                timeout(time: 30, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
                echo 'SonarQube Quality Gate passed.'

                echo 'Evaluating OWASP Dependency-Check Quality Gate...'
                script {                
                    dependencyCheckPublisher pattern: 'dependency-check-report/dependency-check-report.xml',
                                            severityThreshold: 8.0
                }
                echo 'OWASP Dependency-Check Quality Gate passed.'
            }
        }
        stage('Archiviazione Artefatti') {
            steps {
                script {
                    echo 'Checking for WAR files to archive...'
                    def warFiles = findFiles(glob: 'target/*.war')
                    if (warFiles.length > 0) {
                        archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                        echo 'WAR files archived.'
                    } else {
                        echo 'No WAR files found to archive.'
                    }
                }
            }
        }
    }
    post {
        always {
            recordIssues tools: [pmdParser(pattern: 'target/pmd/pmd.xml')]
        }
        success {
            withCredentials([string(credentialsId: 'BUILD_NOTIFICATION_EMAIL', variable: 'EMAIL')]) {
                emailext (
                    subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - SUCCESS",
                    body: "Build and tests succeeded. Check build details: ${env.BUILD_URL}",
                    to: EMAIL
                )
            }
        }
        failure {
            withCredentials([string(credentialsId: 'BUILD_NOTIFICATION_EMAIL', variable: 'EMAIL')]) {
                emailext (
                    subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - FAILURE",
                    body: "Build or tests failed. Check build details: ${env.BUILD_URL}",
                    to: EMAIL
                )
            }
        }
    }
}