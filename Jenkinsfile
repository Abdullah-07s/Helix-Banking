// CI/CD pipeline for the Helix multi-module project. Builds, tests, and
// packages each service module independently using the Maven reactor,
// then builds Docker images, pushes them to Azure Container Registry,
// and deploys to Azure Container Apps.

pipeline {
    agent any

    tools {
        jdk 'jdk21' // Configure a JDK 21 tool named 'jdk21' in Jenkins Global Tool Configuration
    }

    environment {
        // CI builds don't need real secrets - JWT signing, DB creds etc.
        // are only required at runtime (spring-boot:run / actual deploy),
        // not for compile+test+package. Placeholder values keep any
        // context-loading tests from failing on missing env vars.
        JWT_SECRET = 'ci-build-placeholder-secret-not-for-production-use-32ch'
        DB_USERNAME = 'ci_placeholder'
        DB_PASSWORD = 'ci_placeholder'
        RABBITMQ_USER = 'ci_placeholder'
        RABBITMQ_PASSWORD = 'ci_placeholder'

        ACR_NAME = 'helixacr'
        ACR_LOGIN_SERVER = 'helixacr.azurecr.io'
        RESOURCE_GROUP = 'helix-rg'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build helix-common') {
            steps {
                sh './mvnw -pl helix-common -am clean install -DskipTests'
            }
        }

        stage('Build & Test Account Service') {
            steps {
                sh './mvnw -pl helix-account-service -am clean verify'
            }
        }

        stage('Build & Test Transaction Service') {
            steps {
                sh './mvnw -pl helix-transaction-service -am clean verify'
            }
        }

        stage('Build & Test Card Service') {
            steps {
                sh './mvnw -pl helix-card-service -am clean verify'
            }
        }

        stage('Build & Test Fraud Service') {
            steps {
                sh './mvnw -pl helix-fraud-service -am clean verify'
            }
        }

        stage('Build Gateway') {
            steps {
                sh './mvnw -pl helix-gateway -am clean package -DskipTests'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }

        stage('Azure Login') {
            steps {
                withCredentials([
                    string(credentialsId: 'azure-sp-clientid', variable: 'AZ_CLIENT_ID'),
                    string(credentialsId: 'azure-sp-clientsecret', variable: 'AZ_CLIENT_SECRET'),
                    string(credentialsId: 'azure-tenantid', variable: 'AZ_TENANT_ID')
                ]) {
                    sh '''
                        az login --service-principal -u $AZ_CLIENT_ID -p $AZ_CLIENT_SECRET --tenant $AZ_TENANT_ID
                    '''
                }
            }
        }

        stage('Build & Push Images to ACR') {
            steps {
                withCredentials([
                    string(credentialsId: 'acr-username', variable: 'ACR_USER'),
                    string(credentialsId: 'acr-password', variable: 'ACR_PASS')
                ]) {
                    sh '''
                        echo $ACR_PASS | docker login $ACR_LOGIN_SERVER -u $ACR_USER --password-stdin

                        # Each service's Dockerfile does: COPY target/<jar>.jar app.jar
                        # so docker build must run from inside that service's own
                        # directory, AFTER the mvnw package stages above have already
                        # produced target/*.jar for it.
                        for svc in helix-account-service helix-transaction-service helix-card-service helix-fraud-service helix-gateway; do
                          echo "Building $svc..."
                          docker build -t $ACR_LOGIN_SERVER/$svc:$IMAGE_TAG -t $ACR_LOGIN_SERVER/$svc:latest ./$svc
                          docker push $ACR_LOGIN_SERVER/$svc:$IMAGE_TAG
                          docker push $ACR_LOGIN_SERVER/$svc:latest
                        done
                    '''
                }
            }
        }

        stage('Deploy to Container Apps') {
            steps {
                withCredentials([
                    string(credentialsId: 'db-password', variable: 'REAL_DB_PASSWORD'),
                    string(credentialsId: 'rabbitmq-password', variable: 'REAL_RABBITMQ_PASSWORD'),
                    string(credentialsId: 'jwt-secret', variable: 'REAL_JWT_SECRET')
                ]) {
                    sh '''
                        DB_HOST=$(az mysql flexible-server show --resource-group $RESOURCE_GROUP --name helix-mysql --query fullyQualifiedDomainName -o tsv)

                        for svc in helix-account-service helix-transaction-service helix-card-service helix-fraud-service; do
                          echo "Deploying $svc..."
                          az containerapp update \
                            --name $svc \
                            --resource-group $RESOURCE_GROUP \
                            --image $ACR_LOGIN_SERVER/$svc:$IMAGE_TAG \
                            --set-env-vars \
                              DB_HOST=$DB_HOST \
                              DB_PORT=3306 \
                              DB_USERNAME=helix_app \
                              DB_PASSWORD=$REAL_DB_PASSWORD \
                              RABBITMQ_HOST=helix-rabbitmq \
                              RABBITMQ_PORT=5672 \
                              RABBITMQ_USER=helix \
                              RABBITMQ_PASSWORD=$REAL_RABBITMQ_PASSWORD \
                              KAFKA_BOOTSTRAP_SERVERS=helix-kafka:9092 \
                              JWT_SECRET=$REAL_JWT_SECRET \
                              JWT_EXPIRATION_MS=3600000
                        done

                        echo "Deploying helix-gateway..."
                        az containerapp update \
                          --name helix-gateway \
                          --resource-group $RESOURCE_GROUP \
                          --image $ACR_LOGIN_SERVER/helix-gateway:$IMAGE_TAG \
                          --set-env-vars \
                            JWT_SECRET=$REAL_JWT_SECRET \
                            JWT_EXPIRATION_MS=3600000
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'All Helix services built, tested, and deployed to Azure successfully.'
        }
        failure {
            echo 'Build failed - check stage logs above for the failing module.'
        }
        always {
            cleanWs()
        }
    }
}