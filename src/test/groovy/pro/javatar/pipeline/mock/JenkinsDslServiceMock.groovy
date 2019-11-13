/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.mock

import groovy.util.logging.Slf4j
import org.yaml.snakeyaml.Yaml;
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
    String readConfiguration(String file) {
        return new String(getClass().getClassLoader().getResourceAsStream(file).bytes);
    }

    @Override
    Map getJenkinsJobParameters() {
        return [repo: "service"]
    }

    @Override
    def readYaml(String yamlConfig) {
        return new Yaml().load(yamlConfig);
    }

    @Override
    String getShellExecutionResponse(String command) {
        return null
    }

    @Override
    void executeShell(String command) {

    }

    @Override
    def directDsl() {
        return this;
    }

    @Override
    void executeWithinTimeoutInSpecifiedDirectory(Duration timeout, String directory, JenkinsExecutor executor) {
        log.info("timeout: {}, directory: {}", timeout, directory);
    }

}
