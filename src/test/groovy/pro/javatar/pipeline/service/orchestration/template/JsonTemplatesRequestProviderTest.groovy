package pro.javatar.pipeline.service.orchestration.template

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import pro.javatar.pipeline.Utils
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.service.orchestration.model.OrchestrationRequest
import spock.lang.Specification
import spock.lang.Unroll

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
        given: "given orchestration request for service that does not contain specific configuration"
        OrchestrationRequest request =
                getOrchestrationRequest("nomad-request/localization-service-orchestration-request.json")
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

    @Unroll
    def "get merged variables for #service int #env env"(String service, String env, int cpu, int ram, int count) {
        given: "given orchestration request for ${service} in ${env} env to verify merge of variables"
        def request =  getOrchestrationRequest("nomad-request/${service}-orchestration-request.json")
        request.setEnv(env)

        when: "merged variables for ${service} in ${env} environment"
        def actual = provider.getMergedVariables(request)
        assertThat(actual.size(), is())

        then: "expected cpu: ${cpu}, ram: ${ram}, variable map size: ${count}"

        expect:
        actual.cpu == cpu
        actual.ram == ram
        actual.size() == count
        request.env == env
        request.service == service

        where:
        service                | env   | cpu  | ram  | count
        "localization-postgres"| "dev" | 3800 | 4096 | 2
        "localization-service" | "dev" | 3800 | 4096 | 2
        "eureka-service"       | "dev" | 3800 | 4096 | 2
    }

    OrchestrationRequest getOrchestrationRequest(String file) {
        return Utils.readFileAsObject(file, OrchestrationRequest.class)
    }
}
