pipeline {
    parameters {
        choice(choices: ['DC','DR'],name: 'DATACENTER', description: 'Select DataCenter')
        choice(choices: ['PROD','DEV','QA'], name: 'ENVIRONMENT', description: 'Select Environment.')
    }

    agent any

    stages {
        stage('Config Vars') {
        steps {
            script {
                if(params.DATACENTER == 'DC'){
                    echo "Datacenter =  ${params.DATACENTER}"
                    //urls for DC Datacenter to be configured
                    env.REGISTRY_URL = "http://harbor.coe.com/"
                    env.GIT_URL = "http://git.coe.com:3000/developer.os3/Microservices-Train-ticket-Demo-App.git"
                    env.SONARQUBE_URL = "http://10.0.100.16"
                    env.NAMESPACE = 'train-app'

                    //project parameters.
                    if(params.ENVIRONMENT == 'PROD'){
                        env.KUBERNETESserver = "https://rancher.coe.com/k8s/clusters/c-hfvb8"
                        env.BUILD_TYPE = 'train_app_prod'
                        env.REGISTRY_HOST = "${env.REGISTRY_URL}/${BUILD_TYPE}"

                    } else if(params.ENVIRONMENT == 'DEV'){
                        env.KUBERNETESserver = "https://rancher.coe.com/k8s/clusters/c-gl6rx"
                        env.BUILD_TYPE = 'train_app_dev'
                        env.REGISTRY_HOST = "${env.REGISTRY_URL}/${BUILD_TYPE}"
                    } else {
                        env.KUBERNETESserver = "https://rancher.coe.com/k8s/clusters/c-d7sbh"
                        env.BUILD_TYPE = 'train_app_qa'
                        env.REGISTRY_HOST = "${env.REGISTRY_URL}/${BUILD_TYPE}"
                    }

                }else {
                    echo "Datacenter =  ${params.DATACENTER}"
                    //urls for DC Datacenter to be configured
                    env.REGISTRY_URL = "http://harbor.coe.com/"
                    env.GIT_URL = "https://dcvl-git.ns.keralapolice.gov.in/jenkins-admin/ui.git"
                    env.SONARQUBE_URL = "http://10.0.100.16"
                    env.NAMESPACE = 'train-app'

                    if(params.ENVIRONMENT == 'PROD'){
                        env.KUBERNETESserver = "https://rancher.coe.com/k8s/clusters/c-v8dj4"
                        env.BUILD_TYPE = 'train_app_prod'
                        env.REGISTRY_HOST = "${env.REGISTRY_URL}/${BUILD_TYPE}"
                    }else {
                        error 'For DR only PROD Environment can be selected.'
                    }
                }
            }
            }
        }
/*
        stage('SonarQube') {
            steps {
                withSonarQubeEnv('dc-sonar') {
                        sh '''mvn sonar:sonar'''
                }
            }
        }

        stage('Quality Gate'){
            steps {
                script {
                    timeout(time: 1, unit: 'HOURS') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }
*/
        stage('Building image') {
            steps{
                script {
                    sh "docker build --tag $REGISTRY_HOST/ts-ui-dashboard:${BUILD_NUMBER} ./ts-ui-dashboard/"
                }
            }
        }
        stage('Push Image') {
            steps{
            script {
                docker.withRegistry(REGISTRY_URL , 'harbor') {
                    sh 'docker push $REGISTRY_HOST/ts-ui-dashboard:${BUILD_NUMBER}'
                }
            }
            }
        }
        stage('Deploy to Kubernetes'){
            steps{
                sh "chmod +x changeTag.sh"
                sh "./changeTag.sh ${BUILD_NUMBER} ${BUILD_NUMBER} ${BUILD_TYPE} ${namespace}"
                script{
                    withKubeConfig([credentialsId: 'jenkins-user-in-rancher', serverUrl: "env.KUBERNETESserver"]){
                        try{
                            //sh "chmod +x changeTag.sh"
                            //sh "./changeTag.sh ${BUILD_NUMBER} ${BUILD_NUMBER} ${BUILD_TYPE} ${namespace}"
                            //sh "kubectl apply -n test -f deployments5.yml"
                            //sh "kubectl apply -f deployments5.yml"


                            sh "BUILD_TYPE=${BUILD_TYPE} && BUILD_NUMBER=${BUILD_NUMBER} && REGISTRY_HOST=${REGISTRY_HOST} && NAMESPACE=${NAMESPACE} && envsubst < ./deployment/kubernetes-manifests/k8s-with-istio/ts-deployment-part3.yml > ./deployment/kubernetes-manifests/k8s-with-istio/ts-deployment-part6.yml"
                            sh "kubectl apply -f ts-deployment-part6.yml"
                        }catch(ex){
                            // sh "kubectl create -f deployments5.yml"
                        }
                    }
                }
            }
        }
        stage('Cleanup'){
            steps{
                sh '''
                docker rmi $(docker images -f 'dangling=true' -q) || true
                '''
            }
        }
    }
}


