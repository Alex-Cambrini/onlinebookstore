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
                        try {
                            dependencyCheck odcInstallation: 'Dependency-Check', additionalArguments: '--format HTML --failOnCVSS 10 --out dependency-check-report'
                            echo 'Dependency-Check completed successfully.'
                            archiveArtifacts artifacts: 'dependency-check-report/dependency-check-report.html', fingerprint: true
                        } catch (err) {
                            echo "Dependency-Check failed: ${err}"
                        }
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
        stage('Quality Gate') {
            steps {
                echo 'Waiting for SonarQube Quality Gate result...'
                script { sleep 15 }
                timeout(time: 30, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
                echo 'Quality Gate passed.'
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
            recordIssues tools: [pmd(pattern: 'target/pmd/pmd.xml')]
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
