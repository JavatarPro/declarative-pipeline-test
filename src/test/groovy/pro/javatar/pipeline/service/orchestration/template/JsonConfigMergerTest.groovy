package pro.javatar.pipeline.service.orchestration.template

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import pro.javatar.pipeline.Utils
import pro.javatar.pipeline.service.PipelineDslHolder
import spock.lang.Specification

@Slf4j("logger")
class JsonConfigMergerTest extends Specification {

    JsonConfigMerger merger

    def setupSpec() throws Exception {
        logger.info("setup mocks")
    }

    void setup() {
        merger = new JsonConfigMerger()
    }

    def "json merge null"() {
        List<String> jsons = null
        given: "null jsons"

        when: "merge jsons"
        String actualJson = merger.merge(jsons)

        then: "actual json must be null"

        expect:
        actualJson == null
    }

    def "merge one json"() {
        List<String> jsons = new ArrayList<>(1)
        jsons.add(Utils.getFileAsString("json-merge/test-1.json"))

        given: "one json in list"

        when: "merge jsons"
        String actualJson = merger.merge(jsons)
        logger.info("actualJson: \n{}", JsonOutput.prettyPrint(actualJson))
        def actual = new JsonSlurper().parseText(actualJson)

        then: "actual json must be the same"

        expect:
        actual.firstName == "Borys"
        actual.size() == 1
    }

    def "very simple 2 jsons merge"() {
        List<String> jsons = new ArrayList<>(2)
        jsons.add(Utils.getFileAsString("json-merge/test-1.json"))
        jsons.add(Utils.getFileAsString("json-merge/test-2.json"))
        given: "list with 2 jsons for simple merge"

        when: "merge jsons"
        String actualJson = merger.merge(jsons)
        logger.info("actualJson: \n{}", JsonOutput.prettyPrint(actualJson))
        def actual = new JsonSlurper().parseText(actualJson)

        then: "actual json contains fistName & lastName"

        expect:
        actual.firstName == "Borys"
        actual.lastName == "Zora"
    }

    def "2 jsons as list merge"() {
        List<String> jsons = new ArrayList<>(2)
        jsons.add(Utils.getFileAsString("json-merge/test-3.json"))
        jsons.add(Utils.getFileAsString("json-merge/test-4.json"))
        given: "list with 2 jsons for list merge"

        when: "merge jsons"
        String actualJson = merger.merge(jsons)
        logger.info("actualJson: \n{}", JsonOutput.prettyPrint(actualJson))
        def actual = new JsonSlurper().parseText(actualJson)

        then: "actual json contains fistName & lastName"

        expect:
        actual.contains("Serhii")
        actual.contains("Borys")
        actual.contains("Andrii")
        actual.contains("Alina")
        actual.contains("Ivan")
        actual.contains("Dmytro")
    }

}
