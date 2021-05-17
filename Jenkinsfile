pipeline {
    parameters {
        choice(choices: ['DC','DR'],name: 'DATACENTER', description: 'Select DataCenter')
        choice(choices: ['PROD','DEV','QA'], name: 'ENVIRONMENT', description: 'Select Environment.')
    }

    agent any

    tools {
	maven 'default'
    } 

    stages {
        stage('Config Vars') {
        steps {
            script {
                if(params.DATACENTER == 'DC'){
                    echo "Datacenter =  ${params.DATACENTER}"
                    //urls for DC Datacenter to be configured
                    env.REGISTRY_URL = "http://harbor.coe.com/"
                    env.GIT_URL = "http://git.coe.com:3000/developer.os3/Microservices-Train-ticket-Demo-App.git"
                    env.NAMESPACE = 'train-app'

                    //project parameters.
                    if(params.ENVIRONMENT == 'PROD'){
                        env.KUBERNETESserver = "https://rancher.coe.com/k8s/clusters/c-hfvb8"
                        env.BUILD_TYPE = 'train_app_prod'
                        env.REGISTRY_HOST = "harbor.coe.com/${BUILD_TYPE}"

                    } else if(params.ENVIRONMENT == 'DEV'){
                        env.KUBERNETESserver = "https://rancher.coe.com/k8s/clusters/c-gl6rx"
                        env.BUILD_TYPE = 'train_app_dev'
                        env.REGISTRY_HOST = "harbor.coe.com/${BUILD_TYPE}"
                    } else {
                        env.KUBERNETESserver = "https://rancher.coe.com/k8s/clusters/c-d7sbh"
                        env.BUILD_TYPE = 'train_app_qa'
                        env.REGISTRY_HOST = "harbor.coe.com/${BUILD_TYPE}"
                    }

                }else {
                    echo "Datacenter =  ${params.DATACENTER}"
                    //urls for DC Datacenter to be configured
                    env.REGISTRY_URL = "http://harbor.coe.com/"
                    env.GIT_URL = "https://dcvl-git.ns.keralapolice.gov.in/jenkins-admin/ui.git"
                    env.NAMESPACE = 'train-app'

                    if(params.ENVIRONMENT == 'PROD'){
                        env.KUBERNETESserver = "https://rancher.coe.com/k8s/clusters/c-v8dj4"
                        env.BUILD_TYPE = 'train_app_prod'
                        env.REGISTRY_HOST = "harbor.coe.com/${BUILD_TYPE}"
                    }else {
                        error 'For DR only PROD Environment can be selected.'
                    }
                }
            }
            }
        }
        stage('SonarQube') {
            steps {
		script {
                    withSonarQubeEnv('sonarqube') {
                    	sh 'mvn clean package sonar:sonar'
                    }
		}
            }
        }
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
                script{
                    withKubeConfig([credentialsId: 'rancher-api-login', serverUrl: "env.KUBERNETESserver"]){
                        sh "BUILD_TYPE=${BUILD_TYPE} && BUILD_NUMBER=${BUILD_NUMBER} && REGISTRY_HOST=${REGISTRY_HOST} && NAMESPACE=${NAMESPACE} && envsubst < ./deployment/kubernetes-manifests/k8s-with-istio/ts-deployment-part3.yml > ./deployment/kubernetes-manifests/k8s-with-istio/ts-deployment-part6.yml"
                        //sh "kubectl apply -f ./deployment/kubernetes-manifests/k8s-with-istio/ts-deployment-part1.yml"
                        //sh "kubectl apply -f ./deployment/kubernetes-manifests/k8s-with-istio/ts-deployment-part2.yml"
                        //sh "kubectl apply -f ./deployment/kubernetes-manifests/k8s-with-istio/ts-deployment-part6.yml"
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



