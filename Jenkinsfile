pipeline {
    agent any
    tools {
        maven 'Maven 3.9.10'
        jdk 'JDK 8'
    }
    options {
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
    }
    stages {
        //stage 2
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
        //stage 3
        stage('SCA - OWASP Dependency-Check') {
            steps {
                ansiColor('xterm') {
                    script {
                        echo 'Starting OWASP Dependency-Check...'
                        dependencyCheck odcInstallation: 'Dependency-Check',
                            additionalArguments: '--format HTML --format XML --out target'
                        echo 'OWASP Dependency-Check scan completed.'
                        archiveArtifacts artifacts: 'target/dependency-check-report.html, target/dependency-check-report.xml', fingerprint: true
                    }
                }
            }
        }
        //stage 4
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
        //stage 5
        stage('Security Gates Evaluation') {
            steps {
                script {
                    def sonarQGStatus
                    def owaspQGStatus = 'PASSED'

                    echo 'Evaluating all security gates...'

                    echo 'Waiting for SonarQube Quality Gate result...'
                    sleep 20
                    timeout(time: 30, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        sonarQGStatus = qg.status
                    }
                    if (sonarQGStatus == 'OK') {
                        echo 'SonarQube Quality Gate passed.'
                    } else {
                        echo "SonarQube Quality Gate failed with status: ${sonarQGStatus}."
                    }

                    echo 'Evaluating OWASP Dependency-Check Quality Gate...'
                    try {
                        dependencyCheckPublisher pattern: 'dependency-check-report/dependency-check-report.xml',
                            failedTotalHigh: 0,
                            failedTotalCritical: 0,
                            stopBuild: true

                        echo 'OWASP Dependency-Check Quality Gate passed.'
                        owaspQGStatus = 'PASSED'
                    } catch (Exception e) {
                        echo "OWASP Dependency-Check Quality Gate failed: ${e.message}"
                        owaspQGStatus = 'FAILED'
                    }

                    echo '--- Final Security Gate Decision ---'
                    if (sonarQGStatus != 'OK' || owaspQGStatus == 'FAILED') {
                        echo 'One or more security quality gates failed. Aborting pipeline.'
                        currentBuild.result = 'FAILURE'
                        error 'Security Quality Gates Failed!'
                    } else {
                        echo 'All security quality gates passed. Pipeline can proceed.'
                    }
                }
            }
        }
        //stage 6
        stage('Artifact Archiving') {
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
            withCredentials([
                string(credentialsId: 'BUILD_NOTIFICATION_EMAIL', variable: 'EMAIL'),
                string(credentialsId: 'TELEGRAM_TOKEN', variable: 'TOKEN'),
                string(credentialsId: 'TELEGRAM_CHAT_ID', variable: 'CHAT_ID')
            ]) {
                emailext (
                    subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - SUCCESS",
                    body: "Build and tests succeeded. Check build details: ${env.BUILD_URL}",
                    to: EMAIL
                )
                sh 'curl -s -X POST https://api.telegram.org/bot$TOKEN/sendMessage ' +
                '-d chat_id=$CHAT_ID ' +
                '-d text="✅ Build SUCCESS: ' + env.JOB_NAME + ' #' + env.BUILD_NUMBER + ' - ' + env.BUILD_URL + '"'
            }
        }
        failure {
            withCredentials([
                string(credentialsId: 'BUILD_NOTIFICATION_EMAIL', variable: 'EMAIL'),
                string(credentialsId: 'TELEGRAM_TOKEN', variable: 'TOKEN'),
                string(credentialsId: 'TELEGRAM_CHAT_ID', variable: 'CHAT_ID')
            ]) {
                emailext (
                    subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - FAILURE",
                    body: "Build or tests failed. Check build details: ${env.BUILD_URL}",
                    to: "${EMAIL}"
                )
                sh 'curl -s -X POST https://api.telegram.org/bot$TOKEN/sendMessage ' +
                '-d chat_id=$CHAT_ID ' +
                '-d text="❌ Build FAILURE: ' + env.JOB_NAME + ' #' + env.BUILD_NUMBER + ' - ' + env.BUILD_URL + '"'
            }
        }
    }    
}
