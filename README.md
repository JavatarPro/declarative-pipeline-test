# declarative-pipeline-test

## unit test for declarative-pipeline

[declarative-pipeline](https://github.com/JavatarPro/declarative-pipeline)

    mvn clean test

## how to start testing

checkout declarative-pipeline

    # you can choose other path
    cd ${HOME}/continuous-delivery
    git clone https://github.com/JavatarPro/declarative-pipeline.git
    git clone https://github.com/JavatarPro/declarative-pipeline-test.git
    
add env variable to your system   
    
    vim ~/.bash_profile
    # add this lines
    export DECLARATIVE_PIPELINE_HOME=${HOME}/continuous-delivery/declarative-pipeline
    export DECLARATIVE_PIPELINE_TEST_HOME=${HOME}/continuous-delivery/declarative-pipeline-test
    
configure development

    ln -s ${DECLARATIVE_PIPELINE_HOME}/src  ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy
    mvn clean package
    
    
after changes in test     
    
    rm  ${DECLARATIVE_PIPELINE_TEST_HOME}/src/main/groovy