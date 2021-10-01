# declarative-pipeline-test

## unit test for declarative-pipeline

[declarative-pipeline](https://github.com/JavatarPro/declarative-pipeline)

    mvn clean test

## steps how to contribute to declarative-pipeline

- configure env
    - checkout declarative-pipeline
    - add env variable to your system
    - configure development
    - import maven project to any IDE
- add new features to declarative-pipeline
    - add new feature and add unit test
    - commit changes and create pull request
    - after review it will be merged

## configure env

### checkout declarative-pipeline

    # you can choose other path
    cd ${HOME}/continuous-delivery
    git clone https://github.com/JavatarPro/declarative-pipeline.git
    git clone https://github.com/JavatarPro/declarative-pipeline-test.git
    
### add env variable to your system   
    
    vim ~/.bash_profile
    # add this lines
    export DECLARATIVE_PIPELINE_HOME=${HOME}/continuous-delivery/declarative-pipeline
    export DECLARATIVE_PIPELINE_TEST_HOME=${HOME}/continuous-delivery/declarative-pipeline-test
    # close and open new console, variables should be available
    
### configure development

    ln -s ${DECLARATIVE_PIPELINE_HOME}/src  ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy
    cd ${DECLARATIVE_PIPELINE_TEST_HOME}
    mvn clean package
    # you schoul see BUILD SUCCESS, if not verify: javac -version, should be 1.8
    rm ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy
    
### configure development for debugging

    cp -R ${DECLARATIVE_PIPELINE_HOME}/src/ ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy/
    # now you can debug using Intelij Idea
    
### commit code after successful debug

    rm -rf ${DECLARATIVE_PIPELINE_HOME}/src/ && cp -r ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy/ ${DECLARATIVE_PIPELINE_HOME}/src/
    # commit & push 
    # src/main/groovy already ignored in .gitignore and should not be pushed

## add new features to declarative-pipeline

- add new feature
- commit changes to ${DECLARATIVE_PIPELINE_TEST_HOME}
- commit changes in ${DECLARATIVE_PIPELINE_HOME}
- create pull request on both

## TODO

- additional for build version, we could use build number or commit hash to reload on orchestration tool 
  

pro.javatar.pipeline.builder.model.AutoTest.getJobName by using pro.javatar.pipeline.builder.model.YamlConfig.toString
    
    pro.javatar.pipeline.model.ReleaseInfo.getDockerImageNames by using pro.javatar.pipeline.builder.FlowBuilder.toString
    pro.javatar.pipeline.service.vcs.GitService.getUserName by using pro.javatar.pipeline.util.Logger.debug
    pro.javatar.pipeline.builder.Npm.getNpmType by using pro.javatar.pipeline.service.impl.DockerNpmBuildService.<init>

    hudson.remoting.ProxyException: CpsCallableInvocation{methodName=getNpmType, call=com.cloudbees.groovy.cps.impl.CpsFunction@72f20ef, receiver=Npm{npmVersion='null', npmType='nodejs', libraryFolder='node_modules', distributionFolder='dist'}, arguments=[]}
    Finished: FAILURE

k get deploy org-structure -o json > org-structure.json

Contract
```groovy
@Library('javatar-declarative-pipeline@1.4')
import pro.javatar.pipeline.Flow

node("pipeline-jl-k8s") {

    Flow.of(this).execute()

}
```

