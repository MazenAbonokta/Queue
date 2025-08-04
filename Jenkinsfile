pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'docker_credential'
        DOCKERHUB_REPO = 'mazenabonoktah'
        IMAGE_VERSION = 'v1' // Set the version of the image here
        RECIPIENTS = 'mazen.abonoktah@gmail.com'
    }
    stages {

        stage("Check Docker Version")
                {
                    steps {
                        sh 'docker version'
                    }
                }

        stage("Build Service")
                {

                    steps {
                        script {

                            sh "mvn test compile"


                        }
                    }
                }

        stage("Build image and  Push it to docker hub")
                {
                    steps {
                        script {

                            sh "mvn compile jib:dockerBuild -Dimage=${DOCKERHUB_REPO}/queue:${IMAGE_VERSION}"
                        }
                    }
                }
        stage("Remove old containers ") {
            steps {
                script {
                    sh '''
                        echo "<<<<<<<<<<<<Start remove containers >>>>>>>>>>>>>>>>>"

                                if docker ps -a | grep "ms" | awk '{print $1}' | xargs docker rm -f; then
                                    printf 'Clearing old conatainers succeeded\n'
                                else
                                    printf 'Clearing old conatainers failed\n'
                                fi

                                echo "<<<<<<<<<<<<End remove containers >>>>>>>>>>>>>>>>>"
                    '''
                }
            }
        }

        stage("Remove images") {
            steps {
                script {
                    sh '''
                                echo "<<<<<<<<<<<<Start remove images >>>>>>>>>>>>>>>>>"

                                if docker images -a | grep "img" | awk '{print $1":"$2}' | xargs docker rmi -f; then
                                    printf 'Clearing old images succeeded\n'
                                else
                                    printf 'Clearing old images failed\n'
                                fi

                                echo "<<<<<<<<<<<<End remove images >>>>>>>>>>>>>>>>>"
                            '''
                }
            }
        }
        stage('Deploy with Docker Compose') {
            steps {
                script {

                    sh "docker-compose up -d"

                }
            }
        }

        post {
            success {
                echo "Pipeline completed successfully!"
            }
            failure {
                echo "Pipeline failed."
            }
        }
    }
}