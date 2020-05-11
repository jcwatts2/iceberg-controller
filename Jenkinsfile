// -*- mode: groovy -*-
node {

    try {
        
        stage ("Checkout") {
            checkout scm
        }

        if (env.BRANCH_NAME == "master") {

            stage ("Build") {
                sh "echo Building ${env.JOB_NAME}"
            }

            stage ("Verify") {

                if (params.INT_TEST == "Y" && params.INT_TEST_JOB != "") {
                    sh "echo Running integration test" 

                } else {
                    sh "echo Integration test was NOT run (INT_TEST=${params.INT_TEST}, INT_TEST_JOB=${params.INT_TEST_JOB})"
                }
            }
        }

    } catch (e) {
        currentBuild.result = "FAILED"
        throw e 
    }
}
