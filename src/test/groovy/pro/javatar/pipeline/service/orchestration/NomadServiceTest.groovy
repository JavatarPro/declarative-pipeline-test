package pro.javatar.pipeline.service.orchestration

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.Utils
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.service.orchestration.model.DeploymentRequestBO
import pro.javatar.pipeline.service.orchestration.model.DeploymentResponseBO
import pro.javatar.pipeline.service.orchestration.model.NomadBO
import spock.lang.Ignore
import spock.lang.Specification

@Slf4j(value = "logger")
class NomadServiceTest extends Specification {

    def setupSpec() throws Exception {
        logger.info("setup mocks")
        PipelineDslHolder.dsl = new PipelineDslHolderMock()
    }

    @Ignore
    def "check nomad service"() {
        given: "given "
        Map<String, NomadBO> nomadConfig = new HashMap<>()
        NomadBO nomadBO = Utils.readFileAsObject("nomad-request/nomad-bo.json", NomadBO.class)
        nomadConfig.put("dev", nomadBO)
        NomadService nomadService = new NomadService(nomadConfig)

        when: "some when"
        nomadService.setup()

        DeploymentRequestBO deploymentRequestBO =
                Utils.readFileAsObject("nomad-request/deployment-request-bo.json", DeploymentRequestBO.class)
        DeploymentResponseBO response = nomadService.dockerDeployContainer(deploymentRequestBO)

        then: "expect"
        expect:
        nomadService != null
    }

}
