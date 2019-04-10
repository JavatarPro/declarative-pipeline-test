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
                getOrchestrationRequest("nomad-request/l10n-service-orchestration-request.json")
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
    def "merge variables for #service where #scenario"(String service, String env, int cpu, int ram, int port, int count,
                                             String scenario) {
        given: "given ${scenario} with orchestration request for ${service} in ${env} env to verify merge of variables"
        def request =  getOrchestrationRequest("nomad-request/${service}-orchestration-request.json")
        amendTemplateFolder(request)
        request.setEnv(env)

        when: "merged variables for ${service} in ${env} environment"
        def actual = provider.getMergedVariables(request)

        then: "expected overrides in order: main <- env <- main service <- env service"

        expect:
        actual.cpu == "${cpu}"
        actual.ram == "${ram}"
        actual.count == "${count}"
        request.env == env
        request.service == service

        where:
        service          | env   | cpu  | ram  | port | count| scenario
        "l10n-service"   | "qa"  | 3800 | 4096 | 8080 | 1    | "only main variables file"
        "consul"         | "dev" | 1900 | 2048 | 8080 | 1    | "main & env variables files"
        "l10n-postgres"  | "qa"  | 3800 | 4096 | 5432 | 1    | "main & service env variables files"
        "eureka"         | "qa"  | 3800 | 4096 | 8761 | 1    | "main & service main variables files"
        "pricing-mysql"  | "dev" | 1900 | 2048 | 3306 | 1    | "main, env & service env variables files"
        "gateway-redis"  | "dev" | 1900 | 2048 | 6379 | 2    | "main, service main, env & service env variables files"
    }

    void amendTemplateFolder(OrchestrationRequest request) {
        String templateFolder = Utils.getFullFileName(request.getTemplateFolder())
        request.setTemplateFolder(templateFolder)
    }

    OrchestrationRequest getOrchestrationRequest(String file) {
        return Utils.readFileAsObject(file, OrchestrationRequest.class)
    }
}
