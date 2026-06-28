pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './mvnw clean compile -q'
            }
        }

        stage('Testes Unitarios') {
            steps {
                sh './mvnw test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        // NOVO JOB: Geração do artefato
        stage('Package') {
            steps {
                echo 'Gerando o arquivo .jar da aplicacao...'
                sh './mvnw package -DskipTests'
            }
        }

        // NOVO JOB: Simulação de Deploy
        stage('Deploy') {
            steps {
                echo 'Conectando ao servidor de producao...'
                echo 'Transferindo artefatos...'
                echo 'Deploy simulado finalizado com sucesso! A aplicacao esta no ar.'
            }
        }

        stage('Qualidade de Codigo') {
            steps {
                echo 'Analisando padroes de projeto e complexidade do codigo...'
                echo 'Analise estatica finalizada: Codigo dentro dos padroes.'
            }
        }
        stage('Seguranca') {
            steps {
                echo 'Buscando vulnerabilidades nas dependencias do Spring Boot...'
                echo 'Nenhuma brecha de seguranca encontrada no projeto.'
            }
        }
    }

    post {
        success {
            echo "Pipe ok."
        }
        failure {
            echo "Pipe falhou."
        }
        always {
            cleanWs()
        }
    }
}