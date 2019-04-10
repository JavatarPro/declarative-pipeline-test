package pro.javatar.pipeline.service.orchestration.template

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import pro.javatar.pipeline.Utils
import spock.lang.Specification

class JsonConfigMergerTest extends Specification {

    JsonConfigMerger merger = new JsonConfigMerger()

    def merge() {
        List<String> jsons = new ArrayList<>(2)
        jsons.add(Utils.getFileAsString("json-merge/test-1.json"))
        jsons.add(Utils.getFileAsString("json-merge/test-2.json"))
        given: "list with 2 json for simple merge"

        when: "merge jsons"
        String actualJson = merger.merge(jsons)
        JsonOutput.prettyPrint(actualJson)

        then: "actual json contains fistName & lastName"
        def actual = new JsonSlurper().parseText(actualJson)

        expect:
        actual.firstName == "Borys"
        actual.lastName == "Zora"
    }

}
