/*
 * Copyright (c) 2021 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.model

import pro.javatar.pipeline.release.ArtifactReleaseInfo
import spock.lang.Specification

/**
 * @author Borys Zora
 * @version 2021-05-15
 */
class ReleaseInfoTest extends Specification {

    void setup() {}

    def "ArtifactReleaseInfo"() {
        ArtifactReleaseInfo releaseInfo = new ReleaseInfo()
                .setServiceName("inventory")
                .setCurrentVersion("1.0.0-SNAPSHOT")
                .setBuildNumber("18")

        given: releaseInfo.toString()
        when: "we check ArtifactReleaseInfo"
        then:
        releaseInfo.currentVersion() == "1.0.0-SNAPSHOT"
        releaseInfo.releaseVersion() == "1.0.0"
        releaseInfo.nextVersion() == "1.0.1-SNAPSHOT"
        releaseInfo.releaseVersionWithBuildSuffix() == "1.0.0.18"
    }

}
