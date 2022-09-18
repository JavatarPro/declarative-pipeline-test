/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.mock

import groovy.util.logging.Slf4j
import org.yaml.snakeyaml.Yaml;
import pro.javatar.pipeline.jenkins.api.JenkinsDsl
import pro.javatar.pipeline.jenkins.api.JenkinsExecutor
import pro.javatar.pipeline.stage.StageAware

import java.time.Duration

/**
 * @author Borys Zora
 * @version 2019-11-03
 */
@Slf4j
class JenkinsDslServiceMock implements JenkinsDsl {

    Map<String, Queue<Object>> responses = new HashMap<>()

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
        return [repo: "service", service: "job", system: "ats"]
    }

    @Override
    def readYaml(String yamlConfig) {
        return new Yaml().load(yamlConfig);
    }

    @Override
    String getShellExecutionResponse(String command) {
        String resp = getResponse(command, "")
        log.info("command: {} will respond with: {}", command, resp)
        return resp;
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

    JenkinsDslServiceMock addResponse(String command, def response) {
        Queue<String> queue = responses.get(command)
        if (queue == null) {
            queue = new LinkedList<>()
            responses.put(command, queue)
        }
        queue.add(response)
        return this
    }

    def getResponse(String command, String defaultResponse) {
        def queue = responses.get(command)
        if (queue == null) {
            return defaultResponse
        }
        return queue.poll()
    }
}
