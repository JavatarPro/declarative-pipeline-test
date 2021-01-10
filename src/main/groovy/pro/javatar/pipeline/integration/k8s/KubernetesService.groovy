/*
 * Copyright (c) 2020 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.integration.k8s

import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.service.orchestration.DockerOrchestrationService
import pro.javatar.pipeline.service.orchestration.model.DeploymentRequestBO
import pro.javatar.pipeline.service.orchestration.model.DeploymentResponseBO
import pro.javatar.pipeline.util.Logger

/**
 * @author Borys Zora
 * @version 2020-08-02
 */
class KubernetesService implements DockerOrchestrationService {

    JenkinsDslService dslService;

    KubernetesService(JenkinsDslService dslService) {
        this.dslService = dslService
    }

    @Override
    def setup() {
        return null
    }

    @Override
    DeploymentResponseBO dockerDeployContainer(DeploymentRequestBO req) {
        String image = "${req.getImageName()}:${req.getImageVersion()}"
        String name = req.service
        String kubectlCommand = "kubectl create deployment ${name} --image=${image}"
        String resp = dslService.getShellExecutionResponse(kubectlCommand)
        Logger.info("execute command: ${kubectlCommand}\nresp: ${resp}\nrequest: ${req.toString()}")
        return null
    }

}
