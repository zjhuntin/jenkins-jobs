pipeline {
    agent none

    options {
        timestamps()
        timeout(time: 2, unit: 'HOURS')
        disableConcurrentBuilds()
        ansiColor('xterm')
    }

    stages {
        stage('Install tests and Upgrade tests') {
            parallel {

                stage('Install test') {
                    agent { label 'el' }
                    environment {
                        cico_job_name = "foreman-luna-nightly-test"
                    }

                    steps {
                        git_clone_foreman_infra()

                        withCredentials([string(credentialsId: 'centos-jenkins', variable: 'PASSWORD')]) {
                            runPlaybook(
                                playbook: 'ci/centos.org/ansible/jenkins_job.yml',
                                extraVars: [
                                    "jenkins_job_name": "${env.cico_job_name}",
                                    "jenkins_username": "foreman",
                                    "jenkins_job_link_file": "${env.WORKSPACE}/jobs/${env.cico_job_name}"
                                ],
                                sensitiveExtraVars: ["jenkins_password": "${env.PASSWORD}"]
                            )
                        }
                    }
                    post {
                        always {
                            script {
                                set_job_build_description("${env.cico_job_name}")
                            }
                        }
                    }
                }

                stage('Upgrade test') {
                    agent { label 'el' }
                    environment {
                        cico_job_name = "foreman-luna-upgrade-nightly-test"
                    }

                    steps {
                        git_clone_foreman_infra()
                        sleep(5) //See https://bugs.centos.org/view.php?id=14920

                        withCredentials([string(credentialsId: 'centos-jenkins', variable: 'PASSWORD')]) {
                            runPlaybook(
                                playbook: 'ci/centos.org/ansible/jenkins_job.yml',
                                extraVars: [
                                    "jenkins_job_name": "${env.cico_job_name}",
                                    "jenkins_username": "foreman",
                                    "jenkins_job_link_file": "${env.WORKSPACE}/jobs/${env.cico_job_name}"
                                ],
                                sensitiveExtraVars: ["jenkins_password": "${env.PASSWORD}"]
                            )
                        }
                    }
                    post {
                        always {
                            script {
                                set_job_build_description("${env.cico_job_name}")
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        failure {
            emailext(
                subject: "${env.JOB_NAME} ${env.BUILD_ID} failed",
                to: 'evgeni@redhat.com',
                body: "Luna nightly pipeline failed: \n\n${env.BUILD_URL}"
            )
        }
    }
}
