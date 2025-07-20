pipeline {
    agent any
    tools {
        maven 'Maven 3.9.10'
        jdk 'JDK 8'
    }
    environment {
        SONAR_TOKEN = credentials('sonar-token')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify package'
            }
        }

        stage('Software Composition Analysis (SCA)') {
            steps {
                echo 'Running OWASP Dependency-Check...'
                dependencyCheck odcInstallation: 'Dependency-Check', additionalArguments: '--format HTML --failOnCVSS 7 --out dependency-check-report'
                archiveArtifacts artifacts: 'dependency-check-report/dependency-check-report.html', fingerprint: true
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('LocalSonarQube') {
                    sh "mvn sonar:sonar -Dsonar.projectKey=onlinebookstore -Dsonar.login=${env.SONAR_TOKEN}"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script { sleep 15 }
                timeout(time: 30, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Archiviazione Artefatti') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
    post {
        success {
            emailext (
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - SUCCESS",
                body: "Build and tests succeeded. Check build details: ${env.BUILD_URL}",
                to: "alex.cambrini@studio.unibo.it"
            )
        }
        failure {
            emailext (
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - FAILURE",
                body: "Build or tests failed. Check build details: ${env.BUILD_URL}",
                to: "alex.cambrini@studio.unibo.it"
            )
        }
    }
}
