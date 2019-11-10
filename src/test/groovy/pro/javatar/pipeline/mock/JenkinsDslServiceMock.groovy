/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.mock

import groovy.util.logging.Slf4j;
import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.jenkins.api.JenkinsExecutor
import pro.javatar.pipeline.stage.StageAware

import java.time.Duration

/**
 * @author Borys Zora
 * @version 2019-11-03
 */
@Slf4j
class JenkinsDslServiceMock implements JenkinsDslService {

    @Override
    void executeStage(StageAware stage) {
        stage.execute();
    }

    @Override
    void executeWithinTimeoutInSpecifiedDirectory(Duration timeout, String directory, JenkinsExecutor executor) {
        log.info("timeout: {}, directory: {}", timeout, directory);
    }

}
