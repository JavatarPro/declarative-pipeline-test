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

    mv ${DECLARATIVE_PIPELINE_HOME}/src ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy
    # or just
    cp -R ${DECLARATIVE_PIPELINE_HOME}/src ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy
    cp -R ${DECLARATIVE_PIPELINE_HOME}/src/ ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy/
    # now you can debug using Intelij Idea
    cp -r ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy/ ${DECLARATIVE_PIPELINE_HOME}/src/
    # commit & push
    rm -r ${DECLARATIVE_PIPELINE_HOME}/src
    # or just ignore it as source in Intelij Idea

   
## add new features to declarative-pipeline

- add new feature
- commit changes to ${DECLARATIVE_PIPELINE_TEST_HOME}
- commit changes in ${DECLARATIVE_PIPELINE_HOME}
- create pull request on both
