package pro.javatar.pipeline.service.orchestration

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.builder.model.Environment
import pro.javatar.pipeline.service.orchestration.model.NomadBO
import spock.lang.Specification

@Slf4j(value = "logger")
class NomadServiceTest extends Specification {

    def setupSpec() throws Exception {
        logger.info("setup mocks")
    }

    def "check nomad service"() {
        given: "given "
        Map<Environment, NomadBO> nomadConfig = new HashMap<>()
        NomadService nomadService = new NomadService(nomadConfig)
        when: "some when"


        then: "expect"
        expect:
        nomadService != null
    }

}
