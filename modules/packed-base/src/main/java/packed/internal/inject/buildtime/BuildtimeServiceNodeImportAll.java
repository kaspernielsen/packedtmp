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

import app.packed.inject.InstantiationMode;
import app.packed.inject.ProvideHelper;
import app.packed.util.Key;
import app.packed.util.Nullable;
import packed.internal.config.site.InternalConfigSite;
import packed.internal.inject.ServiceNode;
import packed.internal.inject.runtime.RuntimeServiceNode;
import packed.internal.inject.runtime.RuntimeServiceNodeDelegate;

/** A build node that imports a service from another injector. */
public class BuildtimeServiceNodeImportAll<T> extends BuildtimeServiceNode<T> {

    /** The node to import. */
    final ServiceNode<T> other;

    /** The bind injector source. */
    final ImportAllFromInjector injectorToImportFrom;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    BuildtimeServiceNodeImportAll(InjectorBuilder injectorConfiguration, InternalConfigSite configSite, ImportAllFromInjector injectorToImportFrom,
            ServiceNode<T> node) {
        super(injectorConfiguration, configSite, List.of());
        this.other = requireNonNull(node);
        this.injectorToImportFrom = requireNonNull(injectorToImportFrom);
        this.as((Key) node.key());
        description = node.description().orElse(null);
        // this.tags().addAll(node.tags());
    }

    /** {@inheritDoc} */
    @Override
    public InstantiationMode instantiationMode() {
        return other.instantiationMode();
    }

    @Override
    @Nullable
    BuildtimeServiceNode<?> declaringNode() {
        return (other instanceof BuildtimeServiceNode) ? ((BuildtimeServiceNode<?>) other).declaringNode() : null;
    }

    /** {@inheritDoc} */
    @Override
    public T getInstance(ProvideHelper site) {
        return other.getInstance(site);
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsInjectionSite() {
        return other.needsInjectionSite();
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsResolving() {
        return other.needsResolving();
    }

    /** {@inheritDoc} */
    @Override
    RuntimeServiceNode<T> newRuntimeNode() {
        return new RuntimeServiceNodeDelegate<T>(this, other);
    }
}