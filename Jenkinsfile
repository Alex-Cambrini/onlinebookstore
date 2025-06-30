pipeline {
    agent any
    environment {
        SONAR_TOKEN = credentials('sonar-token-id')
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
        stage('SonarQube Analysis') {
            steps {
                sh "mvn sonar:sonar -Dsonar.projectKey=onlinebookstore -Dsonar.host.url=http://localhost:9001 -Dsonar.login=${env.SONAR_TOKEN}"
            }
        }
    }
}
