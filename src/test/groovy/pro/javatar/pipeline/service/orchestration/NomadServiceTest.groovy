package pro.javatar.pipeline.service.orchestration

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.Utils
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.service.orchestration.model.DeploymentRequestBO
import pro.javatar.pipeline.service.orchestration.model.DeploymentResponseBO
import pro.javatar.pipeline.service.orchestration.model.NomadBO
import pro.javatar.pipeline.service.orchestration.model.OrchestrationRequest
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@Slf4j(value = "logger")
class NomadServiceTest extends Specification {

    NomadService nomadService

    def setupSpec() throws Exception {
        logger.info("setup mocks")
        PipelineDslHolder.dsl = new PipelineDslHolderMock()
    }

    void setup() {
        nomadService = getNomadServiceWithDefaultJsonTemplateProvider()
    }

    @Ignore
    @Unroll
    def "deploy docker container of #service using nomad where #scenario"(String service, String env, int cpu,
                                                                          int ram, int port, int count, String scenario) {
        DeploymentRequestBO request = getDeploymentRequestBO(service, env)
        logger.info("request: ${request}")
        given: "given nomad service and deployment request"

        when: "we call dockerDeployContainer with given request"
        DeploymentResponseBO response = nomadService.dockerDeployContainer(request)
        logger.info("response: ${response}")

        then: "expect response is positive"

        expect:
        nomadService != null

        where:
        service          | env   | cpu  | ram  | port | count| scenario
        "l10n-service"   | "qa"  | 3800 | 4096 | 8080 | 1    | "only main variables file"
        "consul"         | "dev" | 1900 | 2048 | 8080 | 1    | "main & env variables files"
        "l10n-postgres"  | "qa"  | 3800 | 4096 | 5432 | 1    | "main & service env variables files"
        "eureka"         | "qa"  | 3800 | 4096 | 8761 | 1    | "main & service main variables files"
        "pricing-mysql"  | "dev" | 1900 | 2048 | 3306 | 1    | "main, env & service env variables files"
        "gateway-redis"  | "dev" | 1900 | 2048 | 6379 | 2    | "main, service main, env & service env variables files"
    }


    def "toOrchestrationRequest converter"(String service, String env, String dockerImage) {
        DeploymentRequestBO request = getDeploymentRequestBO(service, env)
        given: "deployment request"

        when: "nomad service conversion happens"
        OrchestrationRequest actual = nomadService.toOrchestrationRequest(request)

        then: "expected orchestration request filled in correct way"
        expect:
        actual.env == request.environment.value
        actual.dockerRegistry == request.dockerRegistry.registry
        actual.dockerImage == dockerImage
        actual.templateFolder == "nomad-templates"
        actual.templateFiles.size() == 0
        actual.service == service
        actual.buildNumber == request.buildNumber
        actual.templateVariables.size() == 5

        where:
        service          | env   | dockerImage
        "l10n-service"   | "qa"  | "l10n-service:1.0.0"
        "consul"         | "dev" | "consul:1.0.0"
        "l10n-postgres"  | "qa"  | "l10n-postgres:1.0.0"
        "eureka"         | "qa"  | "eureka:1.0.0"
        "pricing-mysql"  | "dev" | "pricing-mysql:1.0.0"
        "gateway-redis"  | "dev" | "gateway-redis:1.0.0"
    }

    NomadService getNomadServiceWithDefaultJsonTemplateProvider() {
        return new NomadService(getNomadConfig())
    }

    Map<String, NomadBO> getNomadConfig() {
        Map<String, NomadBO> nomadConfig = new HashMap<>()
        // TODO read from nomad-config.json
        NomadBO nomadBO = Utils.readFileAsObject("nomad-request/nomad-bo.json", NomadBO.class)
        nomadConfig.put("dev", nomadBO)
        nomadConfig.put("qa", nomadBO)
        nomadConfig.put("uat", nomadBO)
        return nomadConfig
    }

    DeploymentRequestBO getDeploymentRequestBO(String service, String env) {
        DeploymentRequestBO request =
                Utils.readFileAsObject("deployment-request/deployment-request-bo.json", DeploymentRequestBO.class)
        return request.withImageName(service)
                .withService(service)
                .withEnvironment(env)
    }

}
