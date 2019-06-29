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
package packed.internal.container;

import static java.util.Objects.requireNonNull;

import app.packed.config.ConfigSite;
import app.packed.container.ArtifactType;
import app.packed.container.BuildContext;
import app.packed.container.ContainerSource;
import app.packed.container.WireletList;

/** The default implementation of {@link BuildContext} */
final class PackedBuildContext implements BuildContext {

    /** The artifact type. */
    private final ArtifactType artifactType;

    /** The configuration of the artifacts root container. */
    private final PackedContainerConfiguration configuration;

    /**
     * Creates a new context object.
     * 
     * @param configuration
     *            the configuration of the artifacts root container
     * @param artifactType
     *            the type of artifact we are building
     */
    PackedBuildContext(PackedContainerConfiguration configuration, ArtifactType artifactType) {
        this.configuration = requireNonNull(configuration);
        this.artifactType = requireNonNull(artifactType);
    }

    /** {@inheritDoc} */
    @Override
    public ArtifactType artifactType() {
        return artifactType;
    }

    /** {@inheritDoc} */
    @Override
    public ConfigSite configSite() {
        return configuration.configSite();
    }

    /** {@inheritDoc} */
    @Override
    public ContainerSource source() {
        return configuration.source.source;
    }

    /** {@inheritDoc} */
    @Override
    public WireletList wirelets() {
        return configuration.wirelets;
    }
}