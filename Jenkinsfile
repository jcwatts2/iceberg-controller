// -*- mode: groovy -*-


properties([
    parameters({
        string(name: 'INT_TEST_JOB', defaultValue: "", description: 'Path to the integration test project (wsneo)')
        booleanParam(name: 'RUN_INT_TEST', defaultValue: true, description: 'Run the integration tests?')
    })
])

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

                if (params.RUN_INT_TEST == "Y" && params.INT_TEST_JOB != "") {
                    sh "echo Running integration test" 

                } else {
                    sh "echo Integration test was NOT run RUN_INT_TEST=${params.RUN_INT_TEST}, INT_TEST_JOB=${params.INT_TEST_JOB}"
                }
            }
        }

    } catch (e) {
        currentBuild.result = "FAILED"
        throw e 
    }
}
