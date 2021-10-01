/*
 * Copyright (c) 2021 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.model

import pro.javatar.pipeline.util.LogLevel

/**
 * @author Borys Zora
 * @version 2021-06-12
 */
class Model {}

class Config implements Serializable {
    Pipeline pipeline
    List<Docker> docker
    Vcs vcs
    Maven maven
    LogLevel log_level
}

class Docker implements Serializable {
    String name
    String cred
    String url
}

class Pipeline implements Serializable {
    PipelineStagesSuit suit
    String service
    BuildServiceType build
    DockerOrchestrationServiceType orchestration
}

class Vcs implements Serializable { // version control system
    String url
    String cred
}

class Maven implements Serializable {
    String repo_id
    String repo_url
    String jenkins_tool_mvn
    String jenkins_tool_jdk
    String build_cmd = "mvn clean package"
    String integration_test_cmd = "mvn -B verify -DskipITs=false"
    String params
}
