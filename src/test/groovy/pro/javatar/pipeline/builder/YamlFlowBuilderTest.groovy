/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.builder

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.Flow
import pro.javatar.pipeline.YamlFlowBuilder
import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.mock.JenkinsDslServiceMock
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.service.PipelineDslHolder
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Borys Zora
 * @version 2019-11-10
 */
@Slf4j
class YamlFlowBuilderTest extends Specification {

    public static final String GRADLE_NOMAD_CONFIG_FILE = "continuous-delivery/nomad-gradle-declarative-pipeline.yml"

    @Shared
    JenkinsDslService dslService;

    def setupSpec() throws Exception {
        log.info("setup mocks")
        PipelineDslHolder.dsl = new PipelineDslHolderMock();
        dslService = new JenkinsDslServiceMock();
    }

    def "test build"() {
        YamlFlowBuilder builder = new YamlFlowBuilder(GRADLE_NOMAD_CONFIG_FILE, dslService);
        given: "gradle configuration in " + GRADLE_NOMAD_CONFIG_FILE

        when: "build flow is completed"
        Flow flow = builder.build();

        then: "expected configuration"

        expect:
        1==1;

        when: "execute flow"

        then:
        1==1;
    }

}
