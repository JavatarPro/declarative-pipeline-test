package pro.javatar.pipeline.service.orchestration.template

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import pro.javatar.pipeline.Utils
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.service.orchestration.model.OrchestrationRequest
import spock.lang.Specification

import static org.hamcrest.MatcherAssert.assertThat

@Slf4j(value = "logger")
class JsonTemplatesRequestProviderTest extends Specification {

    JsonTemplatesRequestProvider provider

    def setupSpec() throws Exception {
        logger.info("setup mocks")
        PipelineDslHolder.dsl = new PipelineDslHolderMock()
    }

    void setup() {
        provider = new JsonTemplatesRequestProvider()
    }

    def createRequestMainTemplate() {
        String orchestrationRequest = "nomad-request/orchestration-request.json"
        given: "given orchestration request for service that does not contain specific configuration"
        OrchestrationRequest request = Utils.readFileAsObject(orchestrationRequest, OrchestrationRequest.class)
        String templateFolder = Utils.getFullFileName("nomad-templates")
        request.setTemplateFolder(templateFolder)

        when: "provider creates nomad request service that does not contain specific configuration overrides"
        String actualRequest = provider.createRequest(request)
        logger.info("actualRequest: \n${actualRequest}")

        then: "expected that json contains all appropriate variables, passed from pipeline " +
                "merged with variable.properties"
        def actual = new JsonSlurper().parseText(actualRequest)
        assertThat(actual.Job[0].Tasks[0].Resources.cpu, is(750))
    }

}
