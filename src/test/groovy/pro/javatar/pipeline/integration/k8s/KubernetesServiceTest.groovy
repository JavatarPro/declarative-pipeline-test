/*
 * Copyright (c) 2021 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.integration.k8s

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.jenkins.api.JenkinsDsl
import pro.javatar.pipeline.service.PipelineDslHolder
import spock.lang.Shared
import spock.lang.Specification
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * @author Borys Zora
 * @version 2021-01-10
 */
@Slf4j
class KubernetesServiceTest extends Specification {

    static final String K8S_DEPLOYMENT_STATUS_RESPONSE = "k8s/k8s-deployment-status.json";

    static final String SERVICE_NAME = "nginx-app-1";

    @Shared
    JenkinsDsl dsl = mock(JenkinsDsl.class);

    @Shared
    KubernetesService service;

    def setupSpec() throws Exception {
        log.info("setup mocks")
        String jsonResponse = getResponseStub(K8S_DEPLOYMENT_STATUS_RESPONSE)
        when(dsl.getShellExecutionResponse(anyString())).thenReturn(jsonResponse);
        service = new KubernetesService(dsl);
    }

    def "IsDeploymentReady"() {
        given: "k8s deployment status response"

        when: "we get deployment status response from k8s"
        // TODO functionality has been moved to K8sDeployVerifier
        // boolean status = service.isDeploymentReady(SERVICE_NAME)

        then: "expected version retrieved correctly"

        expect:
        true
        // status
    }

    String getResponseStub(String file) {
        return new String(getClass().getClassLoader().getResourceAsStream(file).getBytes());
    }
}
