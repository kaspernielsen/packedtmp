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
package packed.internal.inject.buildtime;

import static java.util.Objects.requireNonNull;

import java.util.List;

import app.packed.inject.ProvidesHelper;
import app.packed.inject.InstantiationMode;
import app.packed.inject.ServiceConfiguration;
import app.packed.util.Nullable;
import packed.internal.config.site.InternalConfigurationSite;
import packed.internal.inject.ServiceNode;
import packed.internal.inject.runtime.RuntimeServiceNode;
import packed.internal.inject.runtime.RuntimeServiceNodeDelegate;

/**
 * A build node that is created when a service is exposed.
 */
public final class BuildtimeServiceNodeExported<T> extends BuildtimeServiceNode<T> {

    /** The node that is exposed. */
    final ServiceNode<T> exposureOf;

    /**
     * @param configuration
     *            the injector configuration this node is being added to
     * @param configurationSite
     *            the configuration site of the exposure
     */
    public BuildtimeServiceNodeExported(ContainerBuilder configuration, InternalConfigurationSite configurationSite, ServiceNode<T> exposureOf) {
        super(configuration, configurationSite, List.of());
        this.exposureOf = requireNonNull(exposureOf);
    }

    @Override
    @Nullable
    BuildtimeServiceNode<?> declaringNode() {
        // Skal vi ikke returnere exposureOf?? istedet for .declaringNode
        return (exposureOf instanceof BuildtimeServiceNode) ? ((BuildtimeServiceNode<?>) exposureOf).declaringNode() : null;
    }

    /** {@inheritDoc} */
    @Override
    public InstantiationMode instantiationMode() {
        return exposureOf.instantiationMode();
    }

    /** {@inheritDoc} */
    @Override
    public T getInstance(ProvidesHelper site) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsInjectionSite() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsResolving() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    RuntimeServiceNode<T> newRuntimeNode() {
        return new RuntimeServiceNodeDelegate<>(this, exposureOf);
    }

    /** {@inheritDoc} */
    @Override
    public ServiceConfiguration<T> lazy() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public ServiceConfiguration<T> prototype() {
        throw new UnsupportedOperationException();
    }
}