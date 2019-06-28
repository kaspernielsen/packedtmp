/*
 * Copyright (c) 2008 Kasper Nielsen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.packed.container;

import app.packed.app.App;
import app.packed.inject.Injector;

/// Kan vi bruge dem for example med @UseExtension(onlyAllow=OutputType.INJECTOR) @OnStart
/** The type of artifact the build process creates. */
public enum ArtifactType {

    /**
     * The output type is an analyze. This is typically via {@link BundleDescriptor#of(Bundle)} or when analyzing an
     * application for graal.
     */
    // Model??
    ANALYZE,

    /**
     * The output type of the process is an {@link App}. This is typically either via
     * {@link App#of(ContainerSource, Wirelet...)} or {@link App#run(ContainerSource, Wirelet...)}.
     */
    APP /* (app.packed.app.App.class) */,

    /**
     * The output type of the build process is a {@link ContainerImage}. This is typically via
     * {@link ContainerImage#of(ContainerSource, Wirelet...)}.
     */
    CONTAINER_IMAGE,

    /**
     * The output type of the process is an {@link App}. This is typically either via
     * {@link Injector#of(ContainerSource, Wirelet...)} or
     * {@link Injector#configure(java.util.function.Consumer, Wirelet...)}.
     */
    INJECTOR;

    /**
     * Returns whether or not output will result in any kind of instantiation.
     * 
     * @return whether or not output will result in any kind of instantiation
     */
    public boolean isInstantiating() {
        return this == APP || this == INJECTOR;
    }
}