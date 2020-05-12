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
                    echo "Running integration test for ${env.JOB_NAME}"

                    try {
                        build job: "${params.INT_TEST_JOB}"
                    } catch(e) {
                        echo "Integration test failure. Integration test job: ${params.INT_TEST_JOB}"
                        sendFailEmail("Integration Tests Failed")
                        throw e
                    }

                } else {
                    echo "Integration test was NOT run RUN_INT_TEST=${params.RUN_INT_TEST}, INT_TEST_JOB=${params.INT_TEST_JOB}"
                }
            }
        }
    } catch (e) {
        currentBuild.result = "FAILED"
        echo "Error running build ${e.getCause().toString()}"
        throw e
    }
}

def sendFailEmail(String failureReason) {

    lastCommitAuthor = sh(script: 'git show -s --format=\'%ce\'', returnStdout: true).trim()
    commitHash = sh(script: 'git show -s --format=\'%H\'', returnStdout: true).trim()
    commitSubject = sh(script: 'git show -s --format=\'%s\'', returnStdout: true).trim()

    mailBody = "<html><body><p>Build job <a href=\"${env.BUILD_URL}\">#${env.BUILD_NUMBER} of ${env.JOB_NAME}</a> failed.</p>" +
                "<p>Git Commit Hash: ${commitHash}</p>" + "<p>Commit Subject: ${commitSubject}<p></body></html>"; 

    emailext(to: "${lastCommitAuthor}", 
        from: 'no-reply@imanage.com', 
        subject: "Failed Build for Master Branch (${failureReason})",
        mimeType:'text/html', body: '${SCRIPT, template="groovy-html.template"}')
}
