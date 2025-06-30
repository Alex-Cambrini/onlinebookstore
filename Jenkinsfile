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
                sh 'mvn clean verify'
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
                sh "mvn sonar:sonar -Dsonar.projectKey=onlinebookstore -Dsonar.login=${env.SONAR_TOKEN}"
            }
        }
    }
}
