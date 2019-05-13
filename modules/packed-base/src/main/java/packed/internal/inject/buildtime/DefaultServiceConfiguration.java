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

import app.packed.inject.InstantiationMode;
import app.packed.inject.ServiceConfiguration;
import app.packed.util.Key;
import app.packed.util.Nullable;

/**
 *
 */
public class DefaultServiceConfiguration<T> extends AbstractFreezableNode implements ServiceConfiguration<T> {

    /** {@inheritDoc} */
    @Override
    protected void onFreeze() {
        node.onFreeze();
    }

    final BuildtimeServiceNode<T> node;

    /**
     * @param node
     */
    public DefaultServiceConfiguration(BuildtimeServiceNode<T> node) {
        super(node.configurationSite);
        this.node = requireNonNull(node);
    }

    /** {@inheritDoc} */
    @Override
    public ServiceConfiguration<T> as(Key<? super T> key) {
        checkConfigurable();
        node.as(key);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public String getDescription() {
        checkConfigurable();
        return node.description;
    }

    /** {@inheritDoc} */
    @Override
    public @Nullable Key<?> getKey() {
        return node.getKey();
    }

    /** {@inheritDoc} */
    @Override
    public InstantiationMode instantiationMode() {
        return node.instantiationMode();
    }

    /** {@inheritDoc} */
    @Override
    public ServiceConfiguration<T> lazy() {
        checkConfigurable();
        ((BuildtimeServiceNodeDefault<T>) node).lazy();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ServiceConfiguration<T> prototype() {
        checkConfigurable();
        ((BuildtimeServiceNodeDefault<T>) node).prototype();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ServiceConfiguration<T> setDescription(@Nullable String description) {
        checkConfigurable();
        node.description = description;
        return this;
    }

}