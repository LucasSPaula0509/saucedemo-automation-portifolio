// Pipeline opcional equivalente ao GitHub Actions (smoke -> regressão -> defeitos -> report).
// Requer: JDK 21 cadastrado em "Global Tool Configuration" com o nome abaixo, Chrome/Firefox no
// agente e o plugin Allure Jenkins (ferramenta "allure").
pipeline {
  agent any

  tools {
    jdk 'temurin-21'
  }

  options {
    timestamps()
    timeout(time: 60, unit: 'MINUTES')
  }

  parameters {
    choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Navegador da regressão')
    string(name: 'TAGS', defaultValue: 'not @defeito and not @performance', description: 'Tags da regressão')
  }

  environment {
    CI = 'true'
    MAVEN_ARGS = '-B -ntp'
  }

  stages {
    stage('Smoke') {
      steps {
        sh './mvnw clean test -Dbrowser=chrome -Dcucumber.filter.tags="@smoke and not @defeito"'
      }
    }

    stage('Regressão') {
      steps {
        // Sem "clean": os resultados do smoke continuam em target/allure-results para o report.
        sh "./mvnw test -Dbrowser=${params.BROWSER} -Dcucumber.filter.tags=\"${params.TAGS}\""
      }
    }

    stage('Defeitos conhecidos') {
      steps {
        // Falha esperada enquanto o defeito existir: marca o estágio como instável sem quebrar o build.
        catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
          sh './mvnw test -Dbrowser=chrome -Dcucumber.filter.tags="@defeito"'
        }
      }
    }
  }

  post {
    always {
      allure includeProperties: false, results: [[path: 'target/allure-results']]
      archiveArtifacts artifacts: 'target/cucumber.html, target/surefire-reports/**', allowEmptyArchive: true
    }
  }
}
