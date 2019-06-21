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
package examples.container;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import app.packed.component.Component;
import app.packed.component.ComponentPath;
import app.packed.component.ComponentStream;
import app.packed.config.ConfigSite;
import app.packed.container.Container;
import app.packed.lifecycle.RunState;
import app.packed.util.Nullable;

/**
 *
 */
public class OldInternalComponent implements Component {

    /** Any children this component might have. */
    final ConcurrentHashMap<String, OldInternalComponent> children = new ConcurrentHashMap<>();

    volatile OldInternalComponentConfiguration<?> configuration;

    /** The configuration site of the component. */
    private final ConfigSite configurationSite;

    /** The container in which this component lives. */
    final Container container;

    /** The description of this component (optional). */
    @Nullable
    private final String description;

    /** The name of the component. The name is unique among other siblings. */
    private final String name;

    /** The parent component, or null for a root component. */
    @Nullable
    final OldInternalComponent parent;

    // /** An immutable set of tags for this component. */
    // private final Set<String> tags;
    volatile Object[] instances;

    OldInternalComponent(Container container, OldInternalComponentConfiguration<?> configuration, OldInternalComponent parent, boolean isRuntime, String name) {
        this.container = requireNonNull(container);
        this.configuration = requireNonNull(configuration);
        this.configurationSite = configuration.configurationSite();
        this.parent = parent;
        this.description = configuration.getDescription();
        this.name = requireNonNull(name);
        // this.tags = configuration.immutableCopyOfTags();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Component> children() {
        return Collections.unmodifiableCollection(children.values());
    }

    /** {@inheritDoc} */
    @Override
    public ConfigSite configurationSite() {
        return configurationSite;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> description() {
        return Optional.ofNullable(description);
    }
    //
    // private Object[] instances() {
    // // Instances are updated like this
    // // component.state = Initialized
    // // component.instances = configuration.instances[];
    // // component.configuration = null;
    //
    // for (;;) {
    // Object[] instances = this.instances;
    // if (instances != null) {
    // return instances;
    // }
    // InternalComponentConfiguration<?> configuration = this.configuration;
    // if (configuration != null) {
    // if (Thread.currentThread() != configuration.initializationThread && getState() == LifecycleState.INITIALIZING) {
    // throw new IllegalStateException("The Component instance has not been instantiated yet, component = " + path());
    // } else {
    // return configuration.instances;
    // }
    // }
    // }
    // }

    Object[] instancesIfAvailable() {
        for (;;) {
            Object[] instances = this.instances;
            if (instances != null) {
                return instances;
            }
            OldInternalComponentConfiguration<?> configuration = this.configuration;
            if (configuration != null) {
                if (Thread.currentThread() != configuration.initializationThread && getState() == RunState.INITIALIZING) {
                    return null;
                } else {
                    return configuration.instances;
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String name() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public ComponentPath path() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public ComponentStream stream() {
        throw new UnsupportedOperationException();
        // return new InternalComponentStream(Stream.concat(Stream.of(this),
        // children.values().stream().flatMap(OldInternalComponent::components)));
    }
    //
    // /** {@inheritDoc} */
    // @Override
    // public Set<String> tags() {
    // return tags;
    // }

    public RunState getState() {
        return RunState.INITIALIZING;
    }
}
//
/// ** {@inheritDoc} */
// @Override
// public Object instance() {
// // TODO we need to check the state of the component O think
// // If the component was terminated without ever being initialized we should return any instance
// return instances()[0];
// }