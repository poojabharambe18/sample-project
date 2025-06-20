pipeline {
    agent { label 'jenkins-agent1' }

    environment {
        SONARQUBE_ENV = 'sonarqube'
    }

    tools {  
        jdk 'Jdk17' 
        maven 'maven3'
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo 'Checking out source code'
                checkout scm
            }
        }
        stage('Copy API DATA to workspace') {
            steps {
                script {
                    sh 'cp -r /opt/allservice_APIDATA/${MICROSERVICE}/API_DATA/ ${WORKSPACE}/${MICROSERVICE}/'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube analysis...'
                withSonarQubeEnv("${SONARQUBE_ENV}") { 
                    sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=Enfinity-AccountService -DskipTests'
                }
            }
        }

        stage('Build with Maven') {
            steps {
                echo 'Building project using Maven...'
                sh 'mvn clean package -DskipTests'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}

