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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import app.packed.bundle.Bundle;
import app.packed.bundle.BundlingOperation;
import app.packed.container.ComponentConfiguration;
import app.packed.container.Container;
import app.packed.container.ContainerBundle;
import app.packed.container.ContainerConfiguration;
import app.packed.inject.Factory;
import app.packed.util.Nullable;
import app.packed.util.TypeLiteral;
import packed.internal.classscan.ComponentClassDescriptor;
import packed.internal.inject.InjectSupport;
import packed.internal.inject.builder.InjectorBuilder;
import packed.internal.invokers.InternalFunction;
import packed.internal.util.configurationsite.ConfigurationSiteType;
import packed.internal.util.configurationsite.InternalConfigurationSite;

/**
 * A builder of {@link Container containers}. Is both used via {@link ContainerBundle} and
 * {@link ContainerConfiguration}.
 */
public final class ContainerBuilder extends InjectorBuilder implements ContainerConfiguration {

    // Maybe should be able to define a naming strategy, to avoid reuse? Mostly for distributed
    final ConcurrentHashMap<String, AtomicLong> autoGeneratedComponentNames = new ConcurrentHashMap<>();

    /** The name of the container, or null if no name has been set. */
    @Nullable
    private String name;

    /** The root component, or null if no root component has been set yet. */
    @Nullable
    InternalComponentConfiguration<?> root;

    /**
     * Creates a new builder.
     * 
     * @param configurationSite
     *            the configuration site
     */
    public ContainerBuilder(InternalConfigurationSite configurationSite) {
        super(configurationSite);
    }

    public ContainerBuilder(InternalConfigurationSite configurationSite, ContainerBundle bundle) {
        super(configurationSite, bundle);
    }

    @Override
    public Container build() {
        if (root == null) {
            throw new IllegalStateException("Must install at least one component");
        }
        if (root.name == null) {
            root.name = root.descriptor().simpleName;
        }

        // initialize component instance array and set component names.
        root.forEachRecursively(cc -> {
            cc.instances = new Object[1 + (cc.mixins == null ? 0 : cc.mixins.size())];
            // Create a name for all children where no name have been defined
            if (cc.children != null && (cc.childrenExplicitNamed == null || cc.children.size() != cc.childrenExplicitNamed.size())) {
                if (cc.childrenExplicitNamed == null) {
                    cc.childrenExplicitNamed = new HashMap<>(cc.children.size());
                }
                for (InternalComponentConfiguration<?> child : cc.children) {
                    String name = child.descriptor().simpleName;
                    AtomicLong al = autoGeneratedComponentNames.computeIfAbsent(name, ignore -> new AtomicLong());
                    String newName;
                    do {
                        long l = al.getAndIncrement();
                        newName = l == 0 ? name : name + l;
                    } while (cc.childrenExplicitNamed.putIfAbsent(newName, child) != null);
                    child.name = newName;
                }
            }
        });
        InternalContainer container = new InternalContainer(this, super.build());

        return container;
    }

    /** {@inheritDoc} */
    @Override
    public void installContainer(ContainerBundle bundle, BundlingOperation... stages) {

    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public <T> ComponentConfiguration<T> install(Class<T> implementation) {
        return install(Factory.findInjectable(implementation));
    }

    /** {@inheritDoc} */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> ComponentConfiguration<T> install(Factory<T> factory) {
        requireNonNull(factory, "factory is null");
        checkConfigurable();
        freezeLatest();
        InternalFunction<T> func = InjectSupport.toInternalFunction(factory);

        ComponentClassDescriptor cdesc = accessor.getComponentDescriptor(func.getReturnTypeRaw());
        InternalComponentConfiguration<T> icc = new InternalComponentConfiguration<T>(this,
                getConfigurationSite().spawnStack(ConfigurationSiteType.COMPONENT_INSTALL), cdesc, root, func, (List) factory.getDependencies());
        ComponentConfiguration<T> cc = install0(icc);
        scanForProvides(func.getReturnTypeRaw(), icc);
        bindNode(icc).as(factory.getKey());
        return cc;
    }

    /** {@inheritDoc} */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> ComponentConfiguration<T> install(T instance) {
        requireNonNull(instance, "instance is null");
        checkConfigurable();
        freezeLatest();
        ComponentClassDescriptor cdesc = accessor.getComponentDescriptor(instance.getClass());
        InternalComponentConfiguration<T> icc = new InternalComponentConfiguration<T>(this,
                getConfigurationSite().spawnStack(ConfigurationSiteType.COMPONENT_INSTALL), cdesc, root, instance);
        ComponentConfiguration<T> cc = install0(icc);
        scanForProvides(instance.getClass(), icc);
        bindNode(icc).as((Class) instance.getClass());
        return cc;
    }

    /** {@inheritDoc} */
    @Override
    public <T> ComponentConfiguration<T> install(TypeLiteral<T> implementation) {
        return install(Factory.findInjectable(implementation));
    }

    /**
     * Sets the component root iff a root has not already been set.
     *
     * @param configuration
     *            the component configuration
     * @return the specified component configuration
     */
    private <T> InternalComponentConfiguration<T> install0(InternalComponentConfiguration<T> configuration) {
        if (root == null) {
            root = configuration;
        } else {
            if (root.children == null) {
                root.children = new ArrayList<>();
            }
            root.children.add(configuration);
        }

        return configuration;
    }

    /** {@inheritDoc} */
    @Override
    public ContainerBuilder setDescription(String description) {
        super.setDescription(description);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /** Small for utility class for generate a best effort unique name for containers. */
    static class InternalContainerNameGenerator {

        /** Assigns unique IDs, starting with 1 when lazy naming containers. */
        private static final AtomicLong ANONYMOUS_ID = new AtomicLong();

        private static final ClassValue<Supplier<String>> BUNDLE_NAME_SUPPLIER = new ClassValue<>() {
            private final AtomicLong L = new AtomicLong();

            @Override
            protected Supplier<String> computeValue(Class<?> type) {
                String simpleName = type.getSimpleName();
                String s = simpleName.endsWith("Bundle") && simpleName.length() > 6 ? simpleName.substring(simpleName.length() - 6) : simpleName;
                return () -> s + L.incrementAndGet();
            }
        };

        static String fromBundleType(Class<? extends Bundle> cl) {
            return BUNDLE_NAME_SUPPLIER.get(cl).get();
        }

        static String next() {
            return "Container" + ANONYMOUS_ID.incrementAndGet();
        }
    }
}
