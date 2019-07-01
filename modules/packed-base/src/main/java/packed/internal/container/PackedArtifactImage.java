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

import java.util.Optional;

import app.packed.component.ComponentPath;
import app.packed.config.ConfigSite;
import app.packed.container.ArtifactImage;
import app.packed.container.ContainerBundle;
import app.packed.container.Wirelet;
import app.packed.container.WireletList;
import app.packed.inject.Injector;

/** The default implementation of {@link ArtifactImage}. */
public final class PackedArtifactImage implements ArtifactImage {

    /** The configuration of the future artifact's root container. */
    private final PackedContainerConfiguration containerConfiguration;

    /**
     * Creates a new image.
     * 
     * @param containerConfiguration
     *            the configuration of the container we wrap
     */
    public PackedArtifactImage(PackedContainerConfiguration containerConfiguration) {
        this.containerConfiguration = requireNonNull(containerConfiguration);
    }

    /** {@inheritDoc} */
    @Override
    public ConfigSite configSite() {
        return containerConfiguration.configSite();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> description() {
        return Optional.ofNullable(containerConfiguration.getDescription());
    }

    /** {@inheritDoc} */
    @Override
    public String name() {
        // If we set naming wirelets we need to run through them
        return containerConfiguration.getName();
    }

    public PackedApp newApp(Wirelet... wirelets) {
        WireletList.of(wirelets);
        return new PackedApp(containerConfiguration.doInstantiate());
    }

    public ArtifactImage newImage(Wirelet... wirelets) {
        throw new UnsupportedOperationException();
    }

    public Injector newInjector(Wirelet... wirelets) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public ComponentPath path() {
        return containerConfiguration.path();
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends ContainerBundle> sourceType() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public ArtifactImage with(Wirelet... wirelets) {
        // We need to check that they can be used at image instantion time.
        throw new UnsupportedOperationException();
    }

    interface InjectorFactory {
        // Tager disse to objekter, laver en injector fra bundlen.
        // Og outputter String
        String spawn(long str1, int str2);

        Injector spawn(String httpRequest, String httpResponse);
    }

    interface UserDefinedSpawner {
        // App spawn(Host h, String httpRequest, String httpResponse);
    }
}