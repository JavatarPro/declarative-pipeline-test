package pro.javatar.pipeline.mock

import groovy.util.logging.Slf4j;

@Slf4j("logger")
class PipelineDslHolderMock {

    def echo(def message) {
        logger.info(message)
    }

}

