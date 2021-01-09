/*
 * Copyright (c) 2020 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.integration.k8s

import pro.javatar.pipeline.service.orchestration.DockerOrchestrationService
import pro.javatar.pipeline.service.orchestration.model.DeploymentRequestBO
import pro.javatar.pipeline.service.orchestration.model.DeploymentResponseBO

/**
 * @author Borys Zora
 * @version 2020-08-02
 */
class KubernetesService implements DockerOrchestrationService {

    @Override
    def setup() {
        return null
    }

    @Override
    DeploymentResponseBO dockerDeployContainer(DeploymentRequestBO deploymentRequest) {
        return null
    }

}
