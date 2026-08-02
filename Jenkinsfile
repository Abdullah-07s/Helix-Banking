// CI pipeline for the Helix multi-module project. Builds, tests, and
// packages each service module independently using the Maven reactor,
// so a failure in one module's tests doesn't block visibility into
// the others. Runs on any available Jenkins agent with Java 21 + Maven
// available (via the Maven wrapper, so no global Maven install needed).

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
    }

    post {
        success {
            echo 'All Helix services built and tested successfully.'
        }
        failure {
            echo 'Build failed - check stage logs above for the failing module.'
        }
        always {
            // Clean workspace of build output between runs to keep the
            // Jenkins agent disk usage predictable across many builds.
            cleanWs()
        }
    }
}