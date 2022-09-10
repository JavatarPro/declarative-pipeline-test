/*
 * Copyright (c) 2022 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.init

import pro.javatar.pipeline.Flow
import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.util.Logger

/**
 * @author Borys Zora
 * @version 2022-09-10
 */
class FlowBuilder {

    static Flow build(def dsl, String... configFiles) {
        return build(PipelineDslHolder.createDsl(dsl), configFiles.toList());
    }

    static build(JenkinsDslService dsl, List<String> configFiles) {
        Logger.debug("build Flow from dsl and configFiles: ${configFiles}")
        ConfigYamlConverter.toConfig()
//        dslService = jenkinsDslService;
//        this.configFiles = configFiles;
    }
}
