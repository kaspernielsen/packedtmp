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
package app.packed.artifact;

import app.packed.container.ContainerConfiguration;
import app.packed.container.WireletList;
import app.packed.util.Nullable;

/**
 * An artifact instantiation context is created every time an artifact is being instantiated.
 * <p>
 * Describes which phases it is available from
 * <p>
 * The main difference from {@link ArtifactBuildContext} is when using an artifact image
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> Hmmm what about if use them at startup???
 */
// ArtifactInstantiationContext or ContainerInstantionationContext
// Per Artifact or PerContainer???
public interface ArtifactInstantiationContext {

    /**
     * Returns the type of artifact that is being instantiated.
     * 
     * @return the type of artifact that is being instantiated
     */
    Class<?> artifactType();

    @Nullable
    <T> T get(ContainerConfiguration configuration, Class<T> type);

    void put(ContainerConfiguration configuration, Object obj);

    <T> T use(ContainerConfiguration configuration, Class<T> type);

    /**
     * Returns a list of wirelets that used to instantiate. This may include wirelets that are not present at build time if
     * using an image.
     * 
     * @return a list of wirelets that used to instantiate
     */
    WireletList wirelets();
}
