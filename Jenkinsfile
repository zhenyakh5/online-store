pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven'
    }

    stages {
        stage('Check Structure') {
            steps {
                bat '''
                    echo Checking project structure...
                    if exist pom.xml (echo pom.xml: OK) else (echo pom.xml: MISSING & exit /b 1)
                    if exist Jenkinsfile (echo Jenkinsfile: OK) else (echo Jenkinsfile: MISSING & exit /b 1)
                    if exist src (echo src: OK) else (echo src: MISSING & exit /b 1)
                '''
            }
        }

        stage('Build') {
            steps {
                bat 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
    }
}