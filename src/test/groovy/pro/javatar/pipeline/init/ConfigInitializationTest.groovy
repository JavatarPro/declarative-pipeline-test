/*
 * Copyright (c) 2022 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.init

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.domain.BuildType
import pro.javatar.pipeline.domain.CommandType
import pro.javatar.pipeline.domain.Config
import pro.javatar.pipeline.domain.ReleaseType
import pro.javatar.pipeline.jenkins.api.JenkinsDsl
import pro.javatar.pipeline.mock.JenkinsDslServiceMock
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.model.DockerOrchestrationServiceType
import pro.javatar.pipeline.model.PipelineStagesSuit
import pro.javatar.pipeline.util.LogLevel
import spock.lang.Shared
import spock.lang.Specification

import static pro.javatar.pipeline.service.PipelineDslHolder.createDsl

/**
 * @author Borys Zora
 * @version 2022-09-10
 */
@Slf4j("logger")
class ConfigInitializationTest extends Specification {

    @Shared
    JenkinsDsl dsl;

    def setupSpec() throws Exception {
        logger.info("setup mocks")
        dsl = createDsl(new PipelineDslHolderMock())
        dsl = new JenkinsDslServiceMock()
    }

    void testCreateEffectiveConfig() {
        def configs = ["init/k8s-pipeline.yml", "init/k8s-pipeline-overrides.yml"]
        given: "pipeline configs:\n ${configs}"

        when: "conversion creating createEffectiveConfig"
        Config config = ConfigInitialization.createEffectiveConfig(dsl, configs)

        then: "expected all configuration is correct"

        expect: "pipeline config is correct"
        config.pipeline.suit == PipelineStagesSuit.LIBRARY
        config.pipeline.service == "job"
        config.pipeline.orchestration == DockerOrchestrationServiceType.K8S
        config.pipeline.build.get(0) == BuildType.MAVEN
        config.pipeline.build.get(1) == BuildType.DOCKER
        config.pipeline.release.get(0) == ReleaseType.DOCKER

        and: "vcs config is correct"
        config.vcs.url == "ssh://git@gitlab.javatar.com:1022/ats/job.git"
        config.vcs.cred == "javatar-jenkins-gitlab-ats-ssh"

        and: "vcs config is correct"
        config.maven.params == "-Dmaven.wagon.http.ssl.insecure=true"
        config.maven.jenkins_tool_mvn == "maven_384"
        config.maven.jenkins_tool_jdk == "JDK18"

        and: "docker config is correct"
        config.docker[0].name == "dev"
        config.docker[0].cred == "jenkins-docker-dev-k8s-nexus-cred"
        config.docker[0].url == "docker-dev.javatar.com"
        config.docker[1].name == "prod"
        config.docker[1].cred == "jenkins-docker-prod-k8s-nexus-cred"
        config.docker[1].url == "docker-prod.javatar.com"

        and: "auto-test config is correct"
        config.autoTest.commands[0].name == "ats system tests"
        config.autoTest.commands[0].job == "ats/system-test"
        config.autoTest.commands[0].type == CommandType.JENKINS_JOB

        and: "log-level config is correct"
        config.log_level == LogLevel.WARN

        and: "version config is correct"
        config.version.file == "pom.xml"
        config.version.pattern == "major.minor.build"
    }
}
