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
                stage('Software Composition Analysis (SCA)') { // NUOVO STAGE
            steps {
                echo 'Running OWASP Dependency-Check...'
                dependencyCheck additionalArguments: '--failOnCVSS 7', odcHtmlReport: 'reports/dependency-check-report.html'
                archiveArtifacts artifacts: 'reports/dependency-check-report.html', fingerprint: true
            }
        }
        stage('SonarQube Analysis') {
            steps {
                sh "mvn sonar:sonar -Dsonar.projectKey=onlinebookstore -Dsonar.login=${env.SONAR_TOKEN}"
            }
        }
    }
}
