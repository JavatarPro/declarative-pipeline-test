/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.integration.marathon

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.service.PipelineDslHolder
import spock.lang.Shared
import spock.lang.Specification

import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * @author Borys Zora
 * @version 2019-11-21
 */
@Slf4j
class MarathonServiceTest extends Specification {

    static final String MARATHON_GET_BY_ID_RESPONSE = "marathon/marathon-get-application-info-by-id.json";

    static final String ENV = "dev";

    static final String SERVICE_NAME = "simple-java-service-dev";

    @Shared
    MarathonService marathonService;

    @Shared
    JenkinsDslService dslService = mock(JenkinsDslService.class);

    def setupSpec() throws Exception {
        log.info("setup mocks")
        PipelineDslHolder.dsl = new PipelineDslHolderMock();
        String jsonResponse = getResponseStub(MARATHON_GET_BY_ID_RESPONSE)
        when(dslService.getShellExecutionResponse(anyString())).thenReturn(jsonResponse);
        Map<String, MarathonConfig> envConfigs = new HashMap<>();
        MarathonConfig config = mock(MarathonConfig.class);
        envConfigs.put(ENV, config)
        marathonService = new MarathonService(envConfigs, dslService);
    }

    def "test version"() {
        given: "marathon response by id"

        when: "we get response from marathon"
        String version = marathonService.getCurrentServiceVersion(ENV, SERVICE_NAME)

        then: "expected version retrieved correctly"

        expect:
        version == "0.0.1.25"
    }

    String getResponseStub(String file) {
        return new String(getClass().getClassLoader().getResourceAsStream(file).getBytes());
    }

}
