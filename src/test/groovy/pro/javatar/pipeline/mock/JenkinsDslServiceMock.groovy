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
        return "";
    }

    @Override
    String getShellExecutionResponse(String command, String defaultMessage) {
        log.info("sh " + command + " || echo {}");
    }

    @Override
    void executeShell(String command) {
        log.info("sh " + command);
    }

    @Override
    void executeSecureShell(String command, String credentialsId, String userVariable, String passwordVariable) {
        log.info("sh " + command);
    }

    @Override
    void addToPath(String toolName, String variable) {
        log.info("addToPath: toolName: {}, variable: {}" + toolName, variable);
    }

    @Override
    def directDsl() {
        return this;
    }

    @Override
    void executeWithinTimeoutInSpecifiedDirectory(Duration timeout, String directory, JenkinsExecutor executor) {
        log.info("timeout: {}, directory: {}", timeout, directory);
    }

    @Override
    def getEnv(String variable) {
        return "null"
    }

    @Override
    boolean fileExists(String file) {
        return false
    }

    @Override
    void echo(String message) {
        log.info("echo " + message);
    }

    @Override
    void writeFile(String path, String content) {

    }

    @Override
    String buildNumber() {
        return "18"
    }
}
