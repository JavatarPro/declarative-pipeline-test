/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.builder

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.Flow
import pro.javatar.pipeline.config.AutoTestConfig
import pro.javatar.pipeline.config.Config
import pro.javatar.pipeline.config.GradleConfig
import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.mock.JenkinsDslServiceMock
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.service.PipelineDslHolder
import spock.lang.Shared
import spock.lang.Specification

import java.time.Duration

/**
 * @author Borys Zora
 * @version 2019-11-10
 */
@Slf4j
class YamlFlowBuilderTest /*extends Specification*/ {

    public static final String GRADLE_NOMAD_CONFIG_FILE = "continuous-delivery/nomad-gradle-declarative-pipeline.yml"
    public static final String DOCKER_ONLY_CONFIG_FILE = "continuous-delivery/docker-only-pipeline.yml"

    @Shared
    JenkinsDslService dslService;

    def setupSpec() throws Exception {
        log.info("setup mocks")
        dslService = PipelineDslHolder.createDsl(new PipelineDslHolderMock())
        dslService = new JenkinsDslServiceMock();
    }

    def "flow configuration and docker execution with docker-only setup" () {
        List<String> configFiles = [DOCKER_ONLY_CONFIG_FILE]
        YamlFlowBuilder builder = new YamlFlowBuilder(dslService, configFiles)
        given: "docker only configuration in " + configFiles

        when: "build flow is completed"
        Config config = builder.getEffectiveConfig(configFiles.get(0));
        Flow flow = builder.build();

        then: "expected configuration is correct"
    }

    def "flow configuration and execution for gradle with nomad"() {
        List<String> configFiles = [GRADLE_NOMAD_CONFIG_FILE]
        YamlFlowBuilder builder = new YamlFlowBuilder(dslService, configFiles);
        given: "gradle configuration in " + configFiles

        when: "build flow is completed"
        Config config = builder.getEffectiveConfig(configFiles.get(0));
        Flow flow = builder.build();

        then: "expected configuration is correct"
        GradleConfig gradleConfig = config.gradleConfig();

        expect: "gradle config is correct"
        gradleConfig.gradleTool() == "gradle_5.6.3"
        gradleConfig.javaTool() == "JDK11"
        gradleConfig.additionalBuildParameters() == ""
        gradleConfig.versionFile() == "gradle.properties"
        gradleConfig.repositoryUrl() == "http://127.0.0.1:8080/repository/maven-releases/"
        gradleConfig.repositoryId() == "nexus"
        gradleConfig.repositoryCredentialsId() == "jenkins-nexus-maven-upload"

        and: "auto test config is correct"
        AutoTestConfig autoTestConfig = config.autoTestConfig();

        autoTestConfig.enabled();
        autoTestConfig.timeout() == Duration.parse("PT7M");
        autoTestConfig.initialDelay() == Duration.parse("PT30S")
        autoTestConfig.jobName() == "system-tests"
    }

    def "build with new type of config"() {
        List<String> configs = ["config/pipeline.yml", "config/infra.yml"]
        YamlFlowBuilder builder = new YamlFlowBuilder(dslService, configs)
        given: "multiple configurations"

        when: "build flow is completed"
        Config config = builder.getEffectiveConfig2(configs)
//        Flow flow = builder.build2()

        then: "expected configuration is correct"
    }
}
