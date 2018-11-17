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

import app.packed.inject.BindingMode;
import app.packed.inject.InjectionSite;
import app.packed.inject.Key;
import app.packed.inject.Provides;
import app.packed.util.ConfigurationSite;
import packed.internal.inject.CommonKeys;
import packed.internal.inject.InternalDependency;
import packed.internal.inject.runtimenodes.RuntimeNode;
import packed.internal.inject.runtimenodes.RuntimeNodeProvidesMethod;
import packed.internal.invokers.AccessibleExecutable;
import packed.internal.util.descriptor.AtProvides;

/** A build node for a method annotated with {@link Provides}. */
public final class BuildNodeProvidesMethod<T> extends BuildNode<T> {

    /** The binding mode of this node. */
    private final BindingMode bindingMode;

    /** The node corresponding to the object or class where this method is located. */
    private final BuildNode<?> owner;

    /** The method annotated with {@link Provides}. */
    private final AccessibleExecutable<AtProvides> providesMethod;

    /**
     * @param owner
     *            the instance or factory node on which the provides method is located.
     * @param providesMethod
     *            the annotated method
     * @param original
     *            the class, instance or factory that was used to register owning node
     */
    public BuildNodeProvidesMethod(BuildNode<?> owner, ConfigurationSite source, AccessibleExecutable<AtProvides> providesMethod) {
        super(null, null, null);// source, providesMethod.getMethod().toDependencyList());
        this.owner = requireNonNull(owner);
        this.providesMethod = requireNonNull(providesMethod);
        this.bindingMode = providesMethod.metadata().getBindingMode();
        setDescription(providesMethod.metadata().getDescription());
    }

    /** {@inheritDoc} */
    @Override
    public BuildNode<?> declaringNode() {
        return owner;
    }

    /** {@inheritDoc} */
    @Override
    public BindingMode getBindingMode() {
        return bindingMode;
    }

    /** {@inheritDoc} */
    @Override
    public T getInstance(InjectionSite site) {
        // TODO cache in ProvidesXYZ
        List<InternalDependency> dependencies = providesMethod.metadata().getDependencies();
        Object[] params = new Object[dependencies.size()];
        Object ownerInstance = owner.getInstance(null);// Must be a factory or an instance, which ignore injection site
        System.out.println(ownerInstance);
        for (int i = 0; i < params.length; i++) {
            if (dependencies.get(i).getKey().equals(Key.of(InjectionSite.class))) {

            }
            params[i] = resolvedDependencies[i].getInstance(null);
        }
        try {
            throw new UnsupportedOperationException();
            // return (T) providesMethod.invoke(ownerInstance, params);
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsInjectionSite() {
        // ServiceRequest kan bruges i forbindelse med prototypes.
        // Singletons kan never have a ServiceRequest in its parameters
        // TODO we could cache this
        List<InternalDependency> dependencies = providesMethod.metadata().getDependencies();
        for (int i = 0; i < dependencies.size(); i++) {
            Key<?> key = dependencies.get(i).getKey();
            if (key.equals(CommonKeys.INJECTION_SITE_KEY) || resolvedDependencies[i].needsInjectionSite()) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean needsResolving() {
        return owner.needsResolving() || !dependencies.isEmpty();
    }

    @Override
    RuntimeNode<T> newRuntimeNode() {
        // if bindingmode==instance
        // return

        return new RuntimeNodeProvidesMethod<>(this);
    }
}