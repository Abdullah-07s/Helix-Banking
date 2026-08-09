// CI/CD pipeline for the Helix multi-module project. Builds, tests, and
// packages each service module independently using the Maven reactor,
// then builds Docker images, pushes them to Azure Container Registry,
// and deploys to Azure Container Apps.
//
// Runs on a native Windows Jenkins agent - uses `bat` steps, not `sh`.
//
// ACR auth uses `az acr login` with the same service principal used for
// Azure Login, rather than the ACR admin user/password. This avoids
// piping a raw password through cmd.exe (which can mangle special
// characters) and lets access be scoped to an AcrPush role instead of
// full registry admin rights.
//
// One-time setup required before this works:
//   $spAppId = "<azure-sp-clientid value>"
//   $acrId = az acr show --name helixacr --resource-group helix-rg --query id -o tsv
//   az role assignment create --assignee $spAppId --scope $acrId --role AcrPush
//
// NOTE: the helix-frontend Container App must be created manually once
// (az containerapp create) before the Deploy stage's `containerapp update`
// call for it will succeed - see chat history for the one-time create command.
//
// NOTE ON bat STEP STRUCTURE: any `for %%x in (...) do (...)` loop must be
// the LAST thing in its `bat` block. Commands placed after a for-loop in
// the same multi-line bat block have been observed to silently never
// execute (no error, no errorlevel trip - the stage just ends early). This
// bit us twice: once in the ACR build/push stage (az acr login combined
// with the docker build/push for-loop), and once in this Deploy stage
// (the gateway/frontend deploys placed after the four-service for-loop).
// Fix in both cases: give anything after a for-loop its own separate bat
// step, since each bat call is a fresh process invocation.
//
// NOTE ON ACCOUNT_SERVICE_URL: helix-transaction-service calls
// helix-account-service internally via an OpenFeign client
// (AccountServiceClient), whose base URL is driven by the
// helix.services.account-url property (${ACCOUNT_SERVICE_URL:http://localhost:8081}
// in application.yml). It must point at account-service's internal
// Container Apps FQDN, over https - Azure's internal ingress redirects
// plain http to https, and Feign's client doesn't follow redirects, so
// using http here reproduces a "Connection refused executing GET
// http://localhost:8081/..." error in the transaction-service logs.
// It's harmlessly unused by the other three services in this shared loop.

pipeline {
    agent any

    tools {
        jdk 'jdk21' // Configure a JDK 21 tool named 'jdk21' in Jenkins Global Tool Configuration
    }

    environment {
        JWT_SECRET = 'ci-build-placeholder-secret-not-for-production-use-32ch'
        DB_USERNAME = 'ci_placeholder'
        DB_PASSWORD = 'ci_placeholder'
        RABBITMQ_USER = 'ci_placeholder'
        RABBITMQ_PASSWORD = 'ci_placeholder'

        ACR_NAME = 'helixacr'
        ACR_LOGIN_SERVER = 'helixacr.azurecr.io'
        RESOURCE_GROUP = 'helix-rg'
        IMAGE_TAG = "${env.BUILD_NUMBER}"

        ACCOUNT_SERVICE_URL = 'https://helix-account-service.internal.ashydesert-43d28197.uaenorth.azurecontainerapps.io'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build helix-common') {
            steps {
                bat 'mvnw.cmd -pl helix-common -am clean install -DskipTests'
            }
        }

        stage('Build & Test Account Service') {
            steps {
                bat 'mvnw.cmd -pl helix-account-service -am clean verify'
            }
        }

        stage('Build & Test Transaction Service') {
            steps {
                bat 'mvnw.cmd -pl helix-transaction-service -am clean verify'
            }
        }

        stage('Build & Test Card Service') {
            steps {
                bat 'mvnw.cmd -pl helix-card-service -am clean verify'
            }
        }

        stage('Build & Test Fraud Service') {
            steps {
                bat 'mvnw.cmd -pl helix-fraud-service -am clean verify'
            }
        }

        stage('Build Gateway') {
            steps {
                bat 'mvnw.cmd -pl helix-gateway -am clean package -DskipTests'
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
                    bat '''
                        az login --service-principal -u %AZ_CLIENT_ID% -p %AZ_CLIENT_SECRET% --tenant %AZ_TENANT_ID%
                        if errorlevel 1 exit /b 1
                    '''
                }
            }
        }

        stage('Build & Push Images to ACR') {
            steps {
                // Split into separate bat steps (each its own process invocation)
                // to isolate whether `az acr login` is where execution is dying.
                bat 'echo DEBUG: before acr login'
                bat 'az acr login --name %ACR_NAME%'
                bat 'echo DEBUG: acr login step returned, still executing'
                bat 'echo DEBUG: about to enter for loop'
                bat '''
                    for %%s in (helix-account-service helix-transaction-service helix-card-service helix-fraud-service helix-gateway) do (
                      echo DEBUG: loop entered for %%s
                      echo Building %%s...
                      docker build -t %ACR_LOGIN_SERVER%/%%s:%IMAGE_TAG% -t %ACR_LOGIN_SERVER%/%%s:latest .\\%%s
                      if errorlevel 1 exit /b 1
                      echo DEBUG: docker build succeeded for %%s
                      docker push %ACR_LOGIN_SERVER%/%%s:%IMAGE_TAG%
                      if errorlevel 1 exit /b 1
                      docker push %ACR_LOGIN_SERVER%/%%s:latest
                      if errorlevel 1 exit /b 1
                      echo DEBUG: docker push succeeded for %%s
                    )
                    echo DEBUG: for loop finished
                '''
                bat 'echo DEBUG: building frontend'
                bat '''
                    docker build -t %ACR_LOGIN_SERVER%/helix-frontend:%IMAGE_TAG% -t %ACR_LOGIN_SERVER%/helix-frontend:latest .\\frontend
                    if errorlevel 1 exit /b 1
                '''
                bat 'echo DEBUG: docker build succeeded for helix-frontend'
                bat '''
                    docker push %ACR_LOGIN_SERVER%/helix-frontend:%IMAGE_TAG%
                    if errorlevel 1 exit /b 1
                    docker push %ACR_LOGIN_SERVER%/helix-frontend:latest
                    if errorlevel 1 exit /b 1
                '''
                bat 'echo DEBUG: docker push succeeded for helix-frontend'
            }
        }

        stage('Deploy to Container Apps') {
            steps {
                withCredentials([
                    string(credentialsId: 'db-password', variable: 'REAL_DB_PASSWORD'),
                    string(credentialsId: 'rabbitmq-password', variable: 'REAL_RABBITMQ_PASSWORD'),
                    string(credentialsId: 'jwt-secret', variable: 'REAL_JWT_SECRET')
                ]) {
                    bat '''
                        for /f "delims=" %%h in ('az mysql flexible-server show --resource-group %RESOURCE_GROUP% --name helix-mysql --query fullyQualifiedDomainName -o tsv') do set DB_HOST=%%h

                        for %%s in (helix-account-service helix-transaction-service helix-card-service helix-fraud-service) do (
                          echo Deploying %%s...
                          az containerapp update --name %%s --resource-group %RESOURCE_GROUP% --image %ACR_LOGIN_SERVER%/%%s:%IMAGE_TAG% --set-env-vars DB_HOST=%DB_HOST% DB_PORT=3306 DB_USERNAME=helix_app DB_PASSWORD=%REAL_DB_PASSWORD% RABBITMQ_HOST=helix-rabbitmq RABBITMQ_PORT=5672 RABBITMQ_USER=helix RABBITMQ_PASSWORD=%REAL_RABBITMQ_PASSWORD% KAFKA_BOOTSTRAP_SERVERS=helix-kafka:9092 JWT_SECRET=%REAL_JWT_SECRET% JWT_EXPIRATION_MS=3600000 ACCOUNT_SERVICE_URL=%ACCOUNT_SERVICE_URL%
                          if errorlevel 1 exit /b 1
                        )
                    '''
                    bat 'echo Deploying helix-gateway...'
                    bat '''
                        az containerapp update --name helix-gateway --resource-group %RESOURCE_GROUP% --image %ACR_LOGIN_SERVER%/helix-gateway:%IMAGE_TAG% --set-env-vars JWT_SECRET=%REAL_JWT_SECRET% JWT_EXPIRATION_MS=3600000 --min-replicas 1
                        if errorlevel 1 exit /b 1
                    '''
                    bat 'echo Deploying helix-frontend...'
                    bat '''
                        az containerapp update --name helix-frontend --resource-group %RESOURCE_GROUP% --image %ACR_LOGIN_SERVER%/helix-frontend:%IMAGE_TAG% --min-replicas 1
                        if errorlevel 1 exit /b 1
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
            script {
                try {
                    cleanWs()
                } catch (Exception e) {
                    echo "Workspace cleanup failed (non-fatal): ${e.getMessage()}"
                }
            }
        }
    }
}
