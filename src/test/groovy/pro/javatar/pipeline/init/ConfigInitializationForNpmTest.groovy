/*
 * Copyright (c) 2022 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.init

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.domain.BuildType
import pro.javatar.pipeline.domain.Config
import pro.javatar.pipeline.domain.ReleaseType
import pro.javatar.pipeline.jenkins.api.JenkinsDsl
import pro.javatar.pipeline.mock.JenkinsDslServiceMock
import pro.javatar.pipeline.model.DockerOrchestrationServiceType
import pro.javatar.pipeline.model.PipelineStagesSuit
import pro.javatar.pipeline.util.LogLevel
import spock.lang.Shared
import spock.lang.Specification

import static pro.javatar.pipeline.service.PipelineDslHolder.createDsl

/**
 * @author Borys Zora
 * @version 2023-04-15
 */
@Slf4j("logger")
class ConfigInitializationForNpmTest extends Specification {

    @Shared
    JenkinsDsl dsl;

    def setupSpec() throws Exception {
        logger.info("setup mocks")
        dsl = createDsl(new JenkinsDslServiceMock())
    }

    void testCreateEffectiveConfig() {
        def configs = ["init/k8s-pipeline-npm.yml"]
        given: "pipeline configs:\n ${configs}"

        when: "conversion creating createEffectiveConfig"
        Config config = ConfigInitialization.createEffectiveConfig(dsl, configs)

        then: "expected all configuration is correct"

        expect: "pipeline config is correct"
        config.pipeline.suit == PipelineStagesSuit.SERVICE_SIMPLE
        config.pipeline.service == "jobs-lab-landing"
        config.pipeline.orchestration == DockerOrchestrationServiceType.K8S
        config.pipeline.build.get(0) == BuildType.NPM
        config.pipeline.build.get(1) == BuildType.DOCKER
        config.pipeline.release.get(0) == ReleaseType.VCS
        config.pipeline.release.get(1) == ReleaseType.DOCKER

        and: "vcs config is correct"
        config.vcs.url == "ssh://git@gitlab.javatar.com:1022/common/jobs-lab-landing.git"
        config.vcs.cred == "javatar-jenkins-gitlab-ssh"

        and: "docker config is correct"
        config.docker[0].name == "dev"
        config.docker[0].cred == "jenkins-docker-dev-k8s-nexus-cred"
        config.docker[0].url == "docker-dev.javatar.com"
        config.docker[1].name == "prod"
        config.docker[1].cred == "jenkins-docker-prod-k8s-nexus-cred"
        config.docker[1].url == "docker-prod.javatar.com"

        and: "vcs config is correct"
        config.npm.version == "v19.9.0"
        config.npm.type == "nodejs"

        and: "log-level config is correct"
        config.log_level == LogLevel.INFO

        and: "version config is correct"
        config.version.file == "package.json"
        config.version.pattern == "major.minor.patch.build"
    }
}
