/*
 * Copyright (c) 2022 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.init

import pro.javatar.pipeline.jenkins.api.JenkinsDslService
import pro.javatar.pipeline.service.PipelineDslHolder
import pro.javatar.pipeline.util.Logger

/**
 * @author Borys Zora
 * @version 2022-09-10
 */
class FlowBuilder {

    FlowBuilder(def dsl, String... configFiles) {
        this(PipelineDslHolder.createDsl(dsl), configFiles.toList())
    }

    FlowBuilder(def dsl, List<String> configFiles) {
        this(PipelineDslHolder.createDsl(dsl), configFiles)
    }

    protected FlowBuilder(JenkinsDslService dsl, List<String> configFiles) {
//        dslService = jenkinsDslService;
//        this.configFiles = configFiles;
        Logger.debug("FlowBuilder#constructor: configFiles: ${configFiles}")
    }
}
