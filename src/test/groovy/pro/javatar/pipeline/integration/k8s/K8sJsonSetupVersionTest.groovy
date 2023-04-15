/*
 * Copyright (c) 2021 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.integration.k8s

import spock.lang.Specification

import static org.junit.Assert.assertTrue

/**
 * @author Borys Zora
 * @version 2021-04-11
 */
class K8sJsonSetupVersionTest extends Specification {

    static final String K8S_CONTAINER_NEW_VERSION = "0.0.19";
    static final String K8S_CONTAINER_VERSION_GIVEN = "k8s/org-structure-given.json";
    static final String K8S_CONTAINER_VERSION_EXPECTED = "k8s/org-structure-expected.json";

    def "SetupVersion"() {
        String givenDeployJsonConfig = getFileContent(K8S_CONTAINER_VERSION_GIVEN)
        given: "givenDeployJsonConfig with 0.018 version"

        when: "we setup new version: " + K8S_CONTAINER_NEW_VERSION
        def service = new K8sJsonSetupVersion(givenDeployJsonConfig)
        String actualJson = service.setupVersion(K8S_CONTAINER_NEW_VERSION)
                .replaceAll("\\s+","")

        then: "expected that it has been applied to response"
        String expectedJson = getFileContent(K8S_CONTAINER_VERSION_EXPECTED)
                .replaceAll("\\s+","")

        assertTrue(actualJson == expectedJson)
    }

    String getFileContent(String file) {
        return new String(getClass().getClassLoader().getResourceAsStream(file).getBytes());
    }
}
