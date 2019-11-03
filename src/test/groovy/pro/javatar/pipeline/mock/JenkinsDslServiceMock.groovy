/*
 * Copyright (c) 2019 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.mock;

import pro.javatar.pipeline.service.JenkinsDslService
import pro.javatar.pipeline.stage.StageAware

/**
 * @author Borys Zora
 * @version 2019-11-03
 */
class JenkinsDslServiceMock implements JenkinsDslService {

    @Override
    void executeStage(StageAware stage) {
        stage.execute();
    }

}
