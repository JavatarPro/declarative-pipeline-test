/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.jenkins.dsl;

import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.jenkins.api.JenkinsExecutor
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.stage.StageAware
import pro.javatar.pipeline.util.StringUtils

import java.time.Duration

import static pro.javatar.pipeline.service.PipelineDslHolder.dsl

/**
 * @author Borys Zora
 * @version 2019-11-03
 */
class JenkinsDslServiceImpl implements JenkinsDslService {

    private def dsl;

    JenkinsDslServiceImpl(def dsl) {
        this.dsl = dsl
        PipelineDslHolder.dsl = dsl
    }

    @Override
    void executeStage(StageAware stage) {
        dsl.stage(stage.getName()) {
            stage.execute();
        }
    }

    @Override
    String readConfiguration(String configFile) {
        String config = dsl.readTrusted configFile;
        return config;
    }

    @Override
    Map getJenkinsJobParameters() {
        return dsl.params
    }

    @Override
    def readYaml(String yamlConfig) {
        def config = dsl.readYaml text: yamlConfig
        return config
    }

    @Override
    String getShellExecutionResponse(String command) {
        String result = dsl.sh returnStdout: true, script: command
        return result
    }

    // https://stackoverflow.com/questions/22009364/is-there-a-try-catch-command-in-bash
    @Override
    String getShellExecutionResponse(String command, String defaultMessage) {
        String fallbackCommand = "${command} || echo ${defaultMessage}"
        String result = dsl.sh returnStdout: true, script: fallbackCommand
        return result
    }

    @Override
    void executeShell(String command) {
        dsl.sh command;
    }

    @Override
    void executeSecureShell(String command, String credentialsId, String userVariable, String passwordVariable) {
        dsl.withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentialsId,
                              usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            String effectiveCommand = command;
            if (StringUtils.isNotBlank(userVariable)) {
                effectiveCommand = effectiveCommand.replace(userVariable, (String) dsl.env.USERNAME)
            }
            if (StringUtils.isNotBlank(passwordVariable)) {
                effectiveCommand = effectiveCommand.replace(passwordVariable, (String) dsl.env.PASSWORD)
            }
            dsl.sh effectiveCommand
        }
    }

    @Override
    void addToPath(String toolName, String variable) {
        def tool="${dsl.tool toolName}";
        dsl.env."${variable}"= tool;
        dsl.env.PATH=String.format("%s/bin:%s", tool, dsl.env.PATH);
    }

    @Override
    void executeWithinTimeoutInSpecifiedDirectory(Duration timeout, String directory, JenkinsExecutor executor) {
        dsl.timeout(time: timeout.toMinutes(), unit: 'MINUTES') { // TODO refactor
            dsl.dir(directory) {
                executor.execute();
            }
        }
    }

    @Override
    def directDsl() {
        return dsl
    }

}
