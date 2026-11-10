pipeline {
    agent any

    environment {
        IMAGE = "java-app:${BUILD_NUMBER}"
    }

    stages {
        stage('Build') {
            steps {
                sh 'docker build -t $IMAGE .'
            }
        }

        stage('Security Scan') {
            steps {
                sh 'trivy image --exit-code 1 --severity CRITICAL,HIGH --ignore-unfixed $IMAGE'
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USER',
                    passwordVariable: 'DOCKERHUB_PASS'
                )]) {
                    sh '''
                        echo "$DOCKERHUB_PASS" | docker login -u "$DOCKERHUB_USER" --password-stdin
                        docker tag $IMAGE $DOCKERHUB_USER/java-app:$BUILD_NUMBER
                        docker push $DOCKERHUB_USER/java-app:$BUILD_NUMBER
                        docker logout
                    '''
                }
            }
        }
    }
}
