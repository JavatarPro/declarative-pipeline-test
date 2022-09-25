/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.jenkins.api.JenkinsDsl
import pro.javatar.pipeline.mock.JenkinsDslServiceMock
import pro.javatar.pipeline.model.ReleaseInfo
import pro.javatar.pipeline.stage.BuildAndUnitTestStage
import pro.javatar.pipeline.stage.StageAware
import spock.lang.Specification

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when
import static pro.javatar.pipeline.Utils.getFileAsString

/**
 * @author Borys Zora
 * @version 2019-11-03
 */
@Slf4j("logger")
class FlowTest extends Specification {

    def "test addStage"() {
        Flow flow = new Flow(new ReleaseInfo(), new JenkinsDslServiceMock())
        given: "empty flow object"

        when: "add stage to flow and retrieve all stages"
        StageAware stage = new BuildAndUnitTestStage();
        flow.addStage(stage)
        List<StageAware> stages = flow.getStageNames()
        then: "expected stage exists"

        expect:
        stages.size() == 1;
        stages.get(0) == stage.name()
    }

    def "test execute"() {
        Flow flow = new Flow(new ReleaseInfo(), new JenkinsDslServiceMock())
        given: "empty flow object"

        when: "add mock sages to flow object and execute it"
        StageAware stage1 = mock(StageAware.class);
        StageAware stage2 = mock(StageAware.class);
        when(stage2.shouldSkip()).thenReturn(true);
        StageAware stage3 = mock(StageAware.class);
        flow.addStage(stage1)
                .addStage(stage2)
                .addStage(stage3)
                .execute()
        then: "expected each stage was executed"

        expect:
        verify(stage1, times(1)).execute()
        verify(stage2, times(0)).execute()
        verify(stage3, times(1)).execute()
    }

    def "test of configuration for suit analyse-service-versions"() {
        JenkinsDsl dsl = new JenkinsDslServiceMock()
                .addResponse("kubectl get deploy -o json",  getFileAsString("k8s/all-deploys.json"))
        String config = "init/k8s-pipeline-analyse-suit.yml"
        Flow flow = Flow.of(dsl, config)
        given: "analyse-service-versions suit in config:\n${config}"

        when: "flow is executed"
        flow.execute()

        then: "expected parsing of versions succeeded and stages contains checked items below"

        expect:
        flow.stageNames[0] == "Version Info"
    }
}
