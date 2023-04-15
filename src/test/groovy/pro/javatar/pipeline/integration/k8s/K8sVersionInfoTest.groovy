/*
 * Copyright (c) 2022 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.integration.k8s

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import pro.javatar.pipeline.domain.Slack
import pro.javatar.pipeline.integration.slack.SlackChannelSender
import pro.javatar.pipeline.jenkins.api.JenkinsDsl
import pro.javatar.pipeline.mock.JenkinsDslServiceMock
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.util.StringUtils
import spock.lang.Shared
import spock.lang.Specification

import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import static pro.javatar.pipeline.Utils.getFileAsString

/**
 * @author Borys Zora
 * @version 2022-09-12
 */
@Slf4j
class K8sVersionInfoTest extends Specification {
    // "image": "docker-dev.javatar.com/absence-tracker:0.0.15.30"
    // path: items[0].spec.template.spec.containers[0].image
    // k get deploy -o json > /Users/javatar/continuous-delivery/declarative-pipeline-test/src/test/resources/k8s/all-deploys.json

    @Shared
    JenkinsDsl dsl = mock(JenkinsDsl.class)

    @Shared
    SlackChannelSender sender = mock(SlackChannelSender.class)

    @Shared
    K8sVersionInfo service

    def setupSpec() throws Exception {
        log.info("setup mocks")
        String jsonResponse = getFileAsString("k8s/all-deploys.json")
        when(dsl.getShellExecutionResponse(anyString())).thenReturn(jsonResponse);
        service = new K8sVersionInfo(dsl, "docker-dev.javatar.com", "docker-prod.javatar.com")
        Slack slack = new Slack()
        slack.webhookUrl = "https://hooks.slack.com/services/ChangeMe"
        slack.enabled = true
        sender = new SlackChannelSender(slack, new JenkinsDslServiceMock())
    }

    def "versions"() {
        given: "K8sVersionInfo service with pre-setup mock of all deployments from dev env"
        when: "retrieve current versions map"
        Map<String, String> versions = service.versionsCurrent()
        then: "only services from the registry must be retrieved"

        sender.send(StringUtils.toJson(versions))

        expect:
        versions.get("absence-tracker") == "docker-dev.javatar.com/absence-tracker:0.0.15.30"
        versions.get("calendar") == "docker-dev.javatar.com/calendar:1.0.7"
        versions.get("customer-support") == "docker-dev.javatar.com/customer-support:0.12"
        versions.get("file") == "docker-dev.javatar.com/file:0.0.20.25"
        versions.get("issue-tracking") == "docker-dev.javatar.com/issue-tracking:0.0.97"
        versions.get("job") == "docker-dev.javatar.com/job:0.0.169.176"
        versions.get("jobs-lab-ui") == "docker-dev.javatar.com/jobs-lab-ui:0.0.355.189"
        versions.get("k8s-gateway") == "docker-dev.javatar.com/k8s-gateway:0.0.74.94"
        versions.get("kafka-topics-initializer") == "docker-dev.javatar.com/kafka-topics-initializer:0.0.8"
        versions.get("mail-plugin") == "docker-dev.javatar.com/mail-plugin:0.0.17"
        versions.get("notification") == "docker-dev.javatar.com/notification:0.1.93.73"
        versions.get("onboarding") == "docker-dev.javatar.com/onboarding:0.0.42.59"
        versions.get("probation") == "docker-dev.javatar.com/probation:0.0.56.60"
        versions.get("profile") == "docker-dev.javatar.com/profile:0.0.20"
        versions.get("org-structure") == "docker-dev.javatar.com/org-structure:0.0.237.297"
        versions.get("recruitment-database") == "docker-dev.javatar.com/recruitment-database:0.0.112.77"
    }

    def "next versions"() {
        String release = getFileAsString("release/dev-versions.json")
        def versions = new JsonSlurper().parseText(release)
        given: "versions from dev env"

        when: "transform to next versions"
        def result = service.versionsNext(versions)

        then: "expected we will switch to release versions from next env"

        expect:
        result.get("slack-plugin") == "docker-prod.javatar.com/slack-plugin:0.0.5"
        result.get("profile") == "docker-prod.javatar.com/profile:0.0.21"
        result.get("job") == "docker-prod.javatar.com/job:0.0.174"
    }

    def "to update for next env"() {
        def dev = new JsonSlurper().parseText(getFileAsString("release/dev-versions.json"))
        def prod = new JsonSlurper().parseText(getFileAsString("release/prod-versions.json"))
        given: "versions from dev and prod env"

        when: "we try to find what version should be updated"
        def prodProposed = service.versionsNext(dev)
        def result = service.toUpdate(prodProposed, prod)

        then: "expected that some of them should be updated and some skipped"

        expect:
        result.get("slack-plugin") == "docker-prod.javatar.com/slack-plugin:0.0.5"
        result.get("profile") == "docker-prod.javatar.com/profile:0.0.21"
        result.get("onboarding") == "docker-prod.javatar.com/onboarding:0.0.43"
    }
}
