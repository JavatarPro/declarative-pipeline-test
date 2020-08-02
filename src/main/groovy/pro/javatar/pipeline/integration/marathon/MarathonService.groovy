/*
 * Copyright (c) 2020 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.integration.marathon

import groovy.json.JsonSlurper
import pro.javatar.pipeline.jenkins.api.JenkinsDslService

/**
 * @author Borys Zora
 * @version 2020-08-02
 */
class MarathonService {

    JenkinsDslService dslService;

    Map<String, MarathonConfig> envConfigs;

    MarathonService(Map<String, MarathonConfig> envConfigs, JenkinsDslService dslService) {
        this.dslService = dslService
        this.envConfigs = envConfigs
    }

    String getCurrentServiceVersion(String env, String service) {
        MarathonConfig config = envConfigs.get(env);
        String url = config.getUrl();
        String resp = dslService.getShellExecutionResponse("curl ${url}/v2/apps/${service}")
        return new JsonSlurper().parseText(resp).app.container.docker.image.split(":")[1]
    }

}
