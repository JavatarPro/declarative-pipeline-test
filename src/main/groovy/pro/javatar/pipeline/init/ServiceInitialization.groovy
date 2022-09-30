/*
 * Copyright (c) 2022 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.init

import pro.javatar.pipeline.domain.BuildType
import pro.javatar.pipeline.domain.Config
import pro.javatar.pipeline.domain.Vcs
import pro.javatar.pipeline.integration.docker.DockerOnlyBuildService
import pro.javatar.pipeline.integration.k8s.K8sVersionInfo
import pro.javatar.pipeline.integration.k8s.KubernetesService
import pro.javatar.pipeline.integration.nomad.NomadService
import pro.javatar.pipeline.integration.slack.SlackChannelSender
import pro.javatar.pipeline.jenkins.api.JenkinsDsl
import pro.javatar.pipeline.model.DockerOrchestrationServiceType
import pro.javatar.pipeline.model.ReleaseInfo
import pro.javatar.pipeline.service.BuildService
import pro.javatar.pipeline.service.ReleaseService
import pro.javatar.pipeline.service.impl.BackEndReleaseServiceV2
import pro.javatar.pipeline.service.impl.DockerBuildService
import pro.javatar.pipeline.service.impl.DockerDeploymentService
import pro.javatar.pipeline.service.impl.GradleBuildService
import pro.javatar.pipeline.service.impl.MavenBuildService
import pro.javatar.pipeline.service.orchestration.DockerOrchestrationService
import pro.javatar.pipeline.service.orchestration.DockerService
import pro.javatar.pipeline.service.vcs.RevisionControlService
import pro.javatar.pipeline.util.Logger

import static pro.javatar.pipeline.service.ContextHolder.add
import static pro.javatar.pipeline.service.ContextHolder.get
import static pro.javatar.pipeline.service.ContextHolder.link

/**
 * @author Borys Zora
 * @version 2022-09-11
 */
class ServiceInitialization implements Serializable {

    static void createServices(JenkinsDsl dsl, Config config, ReleaseInfo info) {
        Logger.info("ServiceInitialization:createServices started")

        // PipelineStagesSuit.ANALYSE_SERVICE_VERSIONS
        add(new K8sVersionInfo(dsl, config.docker[0].url, config.docker[1].url))
        add(new SlackChannelSender(config.slack, dsl))

        // PipelineStagesSuit.SERVICE_SIMPLE for BE
        setupRevisionControl(config.vcs)
        add(new KubernetesService(dsl))
        setupOrchestrationService(config)
        add(new DockerService(config.docker))
        add(new MavenBuildService(config.maven))
        setupBuildService(config)
        add(new DockerBuildService(get(BuildService.class), get(DockerService.class)))
        add(new DockerDeploymentService(info, get(DockerService.class)))
        addReleaseService()

        // PipelineStagesSuit.LIBRARY for BE
        Logger.info("ServiceInitialization:createServices completed")
    }

    static void setupRevisionControl(Vcs vcs) {
        def rc = new RevisionControlBuilder().build(vcs)
        add(rc)
        add(RevisionControlService.class, rc)
    }

    static void addReleaseService() {
        add(new BackEndReleaseServiceV2(get(BuildService.class),
                get(RevisionControlService.class), get(DockerService.class)))
        link(ReleaseService.class, BackEndReleaseServiceV2.class)
    }

    // TODO refactor to multiple build stages instead of inheritance
    static void setupBuildService(Config config) {
        if (config.pipeline.build.isEmpty()) return
        if (config.pipeline.build.get(0) == BuildType.MAVEN) {
            link(BuildService.class, MavenBuildService.class)
            return
        }
        if (config.pipeline.build.get(0) == BuildType.GRADLE) {
            link(BuildService.class, GradleBuildService.class)
            return
        }
        link(BuildService.class, DockerOnlyBuildService.class)
    }

    static void setupOrchestrationService(Config config) {
        if (config.pipeline.orchestration == null) return
        if (config.pipeline.orchestration == DockerOrchestrationServiceType.K8S) {
            link(DockerOrchestrationService.class, KubernetesService.class)
        } else if (config.pipeline.orchestration == DockerOrchestrationServiceType.NOMAD) {
            link(DockerOrchestrationService.class, NomadService.class)
        } else {
            Logger.info("ServiceInitialization#setupOrchestrationService has no effect")
        }
    }
}
