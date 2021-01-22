/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline

import groovy.util.logging.Slf4j
import pro.javatar.pipeline.mock.JenkinsDslServiceMock
import pro.javatar.pipeline.mock.PipelineDslHolderMock
import pro.javatar.pipeline.model.ReleaseInfo
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.stage.BuildAndUnitTestStage
import pro.javatar.pipeline.stage.StageAware
import spock.lang.Ignore
import spock.lang.Specification

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pro.javatar.pipeline.service.PipelineDslHolder.dsl;
/**
 * @author Borys Zora
 * @version 2019-11-03
 */
@Slf4j("logger")
class FlowTest extends Specification {

    Flow flow;

    void setup() {
        PipelineDslHolder.createDsl(new PipelineDslHolderMock())
        flow = new Flow(new ReleaseInfo(), new JenkinsDslServiceMock())
    }

    def "test addStage"() {
        given: "empty flow object"

        when: "add stage to flow and retrieve all stages"
        StageAware stage = new BuildAndUnitTestStage(null, null);
        flow.addStage(stage)
        List<StageAware> stages = flow.getStages();
        then: "expected stage exists"

        expect:
        stages.size() == 1;
        stages.get(0) == stage
    }

    def "test execute"() {
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

    @Ignore
    def "test executeStage"() {
        //given:

        //when:
        // TODO implement stimulus
        //then:
        // TODO implement assertions
    }
}
