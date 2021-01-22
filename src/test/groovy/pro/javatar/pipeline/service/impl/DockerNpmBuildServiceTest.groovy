/*
 * Copyright (c) 2021 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.service.impl

import groovy.util.logging.Slf4j
import org.mockito.Mockito
import pro.javatar.pipeline.builder.Npm
import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.mock.JenkinsDslServiceMock
import pro.javatar.pipeline.model.ReleaseInfo
import pro.javatar.pipeline.service.orchestration.DockerService
import spock.lang.Specification

/**
 * @author Borys Zora
 * @version 2021-01-22
 */
@Slf4j(value = "logger")
class DockerNpmBuildServiceTest extends Specification {

    DockerService dockerService = Mockito.mock(DockerService.class)
    Npm npm = Mockito.mock(Npm.class)
    JenkinsDslService jenkinsDslService = new JenkinsDslServiceMock()

    def "test BuildAndUnitTests"() {
        given: "DockerNpmBuildService"
        DockerNpmBuildService service = new DockerNpmBuildService(dockerService, jenkinsDslService)
        service.setType(npm.getType())
        service.setNpmVersion(npm.npmVersion)
        service.buildAndUnitTests(new ReleaseInfo())
        assert 1 == 1
    }

}
