// -*- mode: groovy -*-


node {
    
    properties([ 
        parameters([
            string(name: 'INT_TEST_JOB', defaultValue: "jcwatts2-github/iceburg-events/master", description: 'Path to the integration test project (wsneo)'),
            booleanParam(name: 'RUN_INT_TEST', defaultValue: true, description: 'Run the integration tests?')
        ])
    ])

    try {
        
        stage ("Checkout") {
            checkout scm
        }

        if (env.BRANCH_NAME == "master") {

            stage ("Build") {
                sh "echo Building ${env.JOB_NAME}"
                // sh "mvn clean package"
            }

            stage ("Verify") {

                if (params.RUN_INT_TEST && params.INT_TEST_JOB != "") {
                    sh "echo Running integration test for $env.JOB_NAME $env.BUILD_NUMBER"

                    try {
                        build job: "${params.INT_TEST_JOB}"
                    } catch(e) {
                        sendFailEmail("Integration Tests Failed")
                        throw e
                    }

                } else {
                    sh "echo Integration test was NOT run RUN_INT_TEST=${params.RUN_INT_TEST}, INT_TEST_JOB=${params.INT_TEST_JOB}"
                }
            }
        }
    } catch (e) {
        currentBuild.result = "FAILED"
        sh "echo ${e.getCause().toString()}"
        throw e
    }
}

def sendFailEmail(String failureReason) {

    lastCommitAuthor = sh(script: 'git show -s --format=\'%ce\'', returnStdout: true).trim()
    commitHash = sh(script: 'git show -s --format=\'%H\'', returnStdout: true).trim()
    commitSubject = sh(script: 'git show -s --format=\'%s\'', returnStdout: true).trim()

    mail(to: "${lastCommitAuthor}", 
        from: 'no-reply@imanage.com', 
        subject: "Failed Build ${env.JOB_NAME} ${env.BUILD_NUMBER} - ${failureReason}",
        body: "Build job #${env.BUILD_NUMBER} of ${env.JOB_NAME} failed.\n\nGit Commit Hash: ${commitHash}\n\nCommit Subject: ${commitSubject}")
     
}
