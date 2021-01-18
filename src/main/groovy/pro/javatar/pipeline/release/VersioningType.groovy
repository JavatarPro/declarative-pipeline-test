/*
 * Copyright (c) 2021 Javatar LLC
 * All rights reserved.
 */
package pro.javatar.pipeline.release

/**
 * @author Borys Zora
 * @version 2021-01-18
 */
enum VersioningType {

    // pom.xml artifact version
    MAVEN,
    // TODO
    GRADLE,
    //
    NPM,
    // { "version": "0.0.1" }
    JSON_FILE,
    // version: 0.0.1
    YAML_FILE,
    // version=0.0.1
    PROPERTY_FILE

}