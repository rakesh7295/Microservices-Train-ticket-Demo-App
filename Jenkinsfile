pipeline {
  parameters {
      choice(choices: ['DC','DR'],name: 'DATACENTER', description: 'Select DataCenter')
      choice(choices: ['PROD','DEV','QA'], name: 'ENVIRONMENT', description: 'Select Environment.')
  }

  environment {
    REGISTRY_URL = "http://harbor.coe.com/"
    SONARQUBE_PROJECT_TITLE = '${JOB_NAME%/*}-${BRANCH_NAME}'
    REGISTRY_HOST = 'harbor.coe.com/demo-application'
    //REGISTRY_HOST = 'harbor.coe.com/Train_ticket_Demo_MicroservicesApp'
    BUILD_TYPE = "development"
    //GIT_URL = "http://git.coe.com:3000/developer.os3/Microservices-Train-ticket-Demo-App.git"
    SONARQUBE_URL = "http://10.0.100.16"
    NAMESPACE = "testing"
    KUBERNETESserver = "https://rancher.coe.com/k8s/clusters/c-gl6rx"
  }
  agent any

  tools{
      maven 'default'
  }

  stages {
    //multiline comment from stage "Config Vars" to "Quality Gate" stage
    /*    
    stage('Config Vars') {
      steps {
        script {
          if(params.DATACENTER == 'DC'){
            echo "Datacenter =  ${params.DATACENTER}"
            //urls for DC Datacenter to be configured
            env.REGISTRY_URL = "dcvl-har.ns.keralapolice.gov.in"
            env.REGISTRY_HOST = "${env.REGISTRY_URL}/test-deploy"
            env.GIT_URL = "https://dcvl-git.ns.keralapolice.gov.in/jenkins-admin/ui.git"
            env.SONARQUBE_URL = "https://dcvl-snrq.ns.keralapolice.gov.in"


            //project parameters.
            if(params.ENVIRONMENT == 'PROD'){
                env.KUBERNETESserver = "https://dcvl-haplb.ns.keralapolice.gov.in/k8s/clusters/c-v9p8d"
                env.BUILD_TYPE = 'development'
                env.profile = '1'
                env.NAMESPACE = 'test'
                env.base_url = 'testapp.ns.keralapolice.gov.in'
                echo "Profile =  ${env.profile}"
                echo "Profile =  ${env.NAMESPACE}"
                echo "base_URL =  ${env.base_url}"

            } else if(params.ENVIRONMENT == 'DEV'){
                env.KUBERNETESserver = "https://dcvl-haplb.ns.keralapolice.gov.in/k8s/clusters/c-8k8tj"
                env.base_url = 'testapp.ns.keralapolice.gov.in'
                env.profile = 'serverdev'
                env.namespace = 'test'
                env.BUILD_TYPE = 'development'
                echo "Profile =  ${env.profile}"
                echo "Profile =  ${env.namespace}"
                echo "base_URL =  ${env.base_url}"
            } else {
                env.KUBERNETESserver = "https://dcvl-haplb.ns.keralapolice.gov.in/k8s/clusters/c-8k8tj"
                profile = 'qa'
                namespace = 'qa'
                env.BUILD_TYPE = 'development'
                echo "Profile =  ${env.profile}"
                echo "Profile =  ${env.namespace}"
                echo "base_URL =  ${env.base_url}"
            }
          }else {
            //urls for DR Datacenter to be configured
            env.KUBERNETESserver = "https://drvl-haplb.ns.keralapolice.gov.in/k8s/clusters/c-8k8tj"
            env.REGISTRY_URL = "drvl-har.ns.keralapolice.gov.in"
            env.REGISTRY_HOST = "dcvl-har.ns.keralapolice.gov.in/test-deploy"
            env.GIT_URL = "https://drvl-git.ns.keralapolice.gov.in/jenkins-admin/ui.git"
            env.SONARQUBE_URL = "https://drvl-snrq.ns.keralapolice.gov.in"



            if(params.ENVIRONMENT == 'PROD'){
              env.profile = ''//profile for production env
              env.namespace = ''//namespace for production deploy
            }else {
                error 'For DR only PROD Environment can be selected.'
            }
          }
        }
      }
    }
    

    stage('Build') {
      steps {
        sh ''' mvn clean package -DskipTests -DactiveProfile=serverdev'''
      }
    }

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
          sh "docker build --tag $REGISTRY_HOST/ts-ui-dashboard-${BUILD_TYPE}:${BUILD_NUMBER} ./ts-ui-dashboard/"
        }
      }
    }
    stage('Push Image') {
      steps{
        script {
          sh 'docker push $REGISTRY_HOST/ts-ui-dashboard-${BUILD_TYPE}:${BUILD_NUMBER}'
        }
      }
    }
    stage('Deploy to Kubernetes'){
      steps{
        //sh "chmod +x changeTag.sh"
        //sh "./changeTag.sh ${BUILD_NUMBER} ${BUILD_NUMBER}  ${NAMESPACE}"
        script{
          withKubeConfig([credentialsId: 'jenkins-user-in-rancher', serverUrl: "${env.KUBERNETESserver}"]){
            try{
              //sh "chmod +x changeTag.sh"
              //sh "./changeTag.sh ${BUILD_NUMBER} ${BUILD_NUMBER} ${BUILD_TYPE} ${NAMESPACE}"
              //sh "kubectl apply -n testing -f deployments5.yml"
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
