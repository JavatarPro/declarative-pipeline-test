/*
 * Copyright (c) 2022 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.integration.slack

import com.cloudbees.groovy.cps.NonCPS
import pro.javatar.pipeline.domain.Slack
import pro.javatar.pipeline.jenkins.api.JenkinsDsl
import pro.javatar.pipeline.util.Logger
import pro.javatar.pipeline.util.RestClient
import pro.javatar.pipeline.util.StringUtils

/**
 * @author Borys Zora
 * @version 2022-09-10
 */
class SlackChannelSender implements Serializable {

    private Slack slack
    private JenkinsDsl dsl

    SlackChannelSender(Slack slack,
                       JenkinsDsl dsl) {
        this.slack = slack
        this.dsl = dsl
    }

    // curl -X POST -H 'Content-type: application/json' --data '{"text":"Hello, World!"}' ${webhookUrl}
    @NonCPS
    void send(String msg) {
        if (slack == null || !slack.enabled) {
            Logger.info("Slack is disabled: message: ${msg}")
        }
        String message = StringUtils.toJson(["text": msg])
        new RestClient(dsl)
                .withBody(message)
                .contentType(RestClient.HttpMediaType.JSON)
                .post(slack.webhookUrl)
                .execute()
    }
}
