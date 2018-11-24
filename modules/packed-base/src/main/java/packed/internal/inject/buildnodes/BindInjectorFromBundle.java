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
package packed.internal.inject.buildnodes;

import static java.util.Objects.requireNonNull;

import java.util.List;

import app.packed.bundle.InjectorBundle;
import app.packed.inject.AbstractInjectorStage;
import packed.internal.util.configurationsite.InternalConfigurationSite;

/**
 *
 */
public final class BindInjectorFromBundle extends BindInjector {

    private final InjectorBundle bundle;

    private final List<AbstractInjectorStage> filters;

    public BindInjectorFromBundle(InternalInjectorConfiguration injectorConfiguration, InternalConfigurationSite configurationSite, InjectorBundle bundle,
            AbstractInjectorStage[] filters) {
        super(injectorConfiguration, configurationSite);
        this.bundle = requireNonNull(bundle, "bundle is null");
        this.filters = List.of(filters);
    }

    public InjectorBundle bundle() {
        return bundle;
    }

    public List<AbstractInjectorStage> getFilters() {
        return filters;
    }
}