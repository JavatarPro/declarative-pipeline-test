/*
 * Copyright (c) 2022 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.integration.slack

import com.cloudbees.groovy.cps.NonCPS
import pro.javatar.pipeline.domain.Slack
import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.util.Logger
import pro.javatar.pipeline.util.RestClient

/**
 * @author Borys Zora
 * @version 2022-09-10
 */
class SlackChannelSender implements Serializable {

    Slack slack
    JenkinsDslService dsl

    SlackChannelSender(Slack slack,
                       JenkinsDslService dsl) {
        this.slack = slack
        this.dsl = dsl
    }

    // curl -X POST -H 'Content-type: application/json' --data '{"text":"Hello, World!"}' ${webhookUrl}
    @NonCPS
    void send(String msg) {
        if (slack == null || !slack.enabled) {
            Logger.info("Slack is disabled: message: ${msg}")
        }
        new RestClient(dsl)
                .withBody(msg)
                .contentType(RestClient.HttpMediaType.JSON)
                .post(slack.webhookUrl)
    }
}
