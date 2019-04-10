package pro.javatar.pipeline.service.orchestration.template

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import pro.javatar.pipeline.Utils
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.service.orchestration.model.OrchestrationRequest
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

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

    @Ignore
    @Unroll
    def "create request for #service where  #scenario"(String service, String env, int cpu, int ram, int port,
                                                       int count, String scenario) {
        given: "given ${scenario} with orchestration request for ${service} in ${env} to create request"
        def request =  getOrchestrationRequest("nomad-request/${service}-orchestration-request.json")
        amendTemplateFolder(request)
        request.setEnv(env)

        when: "provider creates nomad request for service ${service} when ${scenario}"
        String actualRequest = provider.createRequest(request)
        logger.debug("json validation, if json is not valid, JsonOutput.prettyPrint will throw exception")
        JsonOutput.prettyPrint(actualRequest)

        then: "expected that json contains all appropriate variables, passed by orchestration request " +
                "merged with *variable.properties and *request.json"

        def actual = new JsonSlurper().parseText(actualRequest)

        expect:
        actual.Job[0].Tasks[0].Resources.cpu == cpu
        actual.Job[0].Tasks[0].Resources.MemoryMB == ram
        actual.Job.TaskGroups[0].Tasks[0].Resources.Networks[0].DynamicPorts[0].Label == service
        actual.Job.TaskGroups[0].Tasks[0].Config.port_map[0]."${service}" == service


        where:
        service          | env   | cpu  | ram  | port | count| scenario
        "l10n-service"   | "qa"  | 3800 | 4096 | 8080 | 1    | "only main variable & request files"
        "consul"         | "dev" | 1900 | 2048 | 8080 | 1    | "main & env variable & request files"
        "l10n-postgres"  | "qa"  | 3800 | 4096 | 5432 | 1    | "main & service env variable & request files"
        "eureka"         | "qa"  | 3800 | 4096 | 8761 | 1    | "main & service main variable & request files"
        "pricing-mysql"  | "dev" | 1900 | 2048 | 3306 | 1    | "main, env & service env variable & request files"
        "gateway-redis"  | "dev" | 1900 | 2048 | 6379 | 2    | "main, service main, env & service env variable & request files"
    }

    @Unroll
    def "get template file contents for #service where #scenario"(String service, String env, int count, String scenario) {
        given: "given ${scenario} with orchestration request for ${service} in ${env} env files count"
        def request =  getOrchestrationRequest("nomad-request/${service}-orchestration-request.json")
        amendTemplateFolder(request)
        request.setEnv(env)

        when: "get template files for ${service} in ${env} environment"
        def actual = provider.getTemplateFileContents(request)

        then: "expected count ${count} where ${scenario}"

        expect:
        actual.size() == count
        request.env == env
        request.service == service

        where:
        service          | env   | count| scenario
        "l10n-service"   | "qa"  | 1    | "only main request file"
        "consul"         | "dev" | 2    | "main & env request files"
        "l10n-postgres"  | "qa"  | 2    | "main & service env request files"
        "eureka"         | "qa"  | 2    | "main & service main request files"
        "pricing-mysql"  | "dev" | 3    | "main, env & service env request files"
        "gateway-redis"  | "dev" | 4    | "main, service main, env & service env request files"
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
