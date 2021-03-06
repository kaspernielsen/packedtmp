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
import packed.internal.inject.runtime.AbstractRuntimeServiceNode;
import packed.internal.inject.runtime.RuntimeDelegateServiceNode;

/** A build node that imports a service from another injector. */
public class BuildServiceNodeImported<T> extends BuildServiceNode<T> {

    /** The node to import. */
    final ServiceNode<T> importFrom;

    /** The bind injector source. */
    final InjectorImporter injectorImporter;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    BuildServiceNodeImported(InjectorBuilder injectorConfiguration, InternalConfigSite configSite, InjectorImporter injectorToImportFrom,
            ServiceNode<T> importFrom) {
        super(injectorConfiguration, configSite, List.of());
        this.importFrom = requireNonNull(importFrom);
        this.injectorImporter = requireNonNull(injectorToImportFrom);
        this.as((Key) importFrom.key());
        description = importFrom.description().orElse(null);
    }

    @Override
    @Nullable
    BuildServiceNode<?> declaringNode() {
        return (importFrom instanceof BuildServiceNode) ? ((BuildServiceNode<?>) importFrom).declaringNode() : null;
    }

    /** {@inheritDoc} */
    @Override
    public T getInstance(ProvideHelper site) {
        return importFrom.getInstance(site);
    }

    /** {@inheritDoc} */
    @Override
    public InstantiationMode instantiationMode() {
        return importFrom.instantiationMode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsInjectionSite() {
        return importFrom.needsInjectionSite();
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsResolving() {
        return importFrom.needsResolving();
    }

    /** {@inheritDoc} */
    @Override
    AbstractRuntimeServiceNode<T> newRuntimeNode() {
        return new RuntimeDelegateServiceNode<T>(this, importFrom);
    }
}
