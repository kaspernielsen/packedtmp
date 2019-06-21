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

import java.lang.invoke.MethodHandles.Lookup;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.packed.component.Component;
import app.packed.component.ComponentStream;
import app.packed.inject.Injector;
import app.packed.inject.ServiceDescriptor;
import app.packed.util.Key;
import app.packed.util.Nullable;

/** The default implementation of Container. */
final class InternalContainer extends AbstractComponent implements Component {

    /** All the components of this container. */
    final Map<String, AbstractComponent> children = new HashMap<>();

    private final Injector injector;

    /** {@inheritDoc} */
    @Override
    public Collection<Component> children() {
        return Collections.unmodifiableCollection(children.values());
    }

    public InternalContainer(@Nullable AbstractComponent parent, DefaultContainerConfiguration configuration, Injector injector) {
        super(parent, configuration);
        this.injector = requireNonNull(injector);
        if (configuration.children != null) {
            for (AbstractComponentConfiguration acc : configuration.children.values()) {
                if (acc instanceof DefaultComponentConfiguration) {
                    InternalComponent ic = new InternalComponent(this, (DefaultComponentConfiguration) acc);
                    children.put(ic.name(), ic);
                } else {
                    DefaultContainerConfiguration dcc = (packed.internal.container.DefaultContainerConfiguration) acc;
                    InternalContainer ic = new InternalContainer(this, dcc, injector);
                    children.put(ic.name(), ic);
                }
            }
        }
    }

    @Override
    public ComponentStream stream() {
        return new InternalComponentStream(Stream.concat(Stream.of(this), children.values().stream().flatMap(AbstractComponent::stream)));
        //
        // Stream.Builder<Component> builder = Stream.builder();
        // builder.accept(new ComponentWrapper());
        // for (AbstractComponent ic : components.values()) {
        // builder.accept(ic);
        // }
        // return new InternalComponentStream(builder.build());
    }

    public <T> Optional<T> get(Class<T> key) {
        return injector.get(key);
    }

    public <T> Optional<T> get(Key<T> key) {
        return injector.get(key);
    }

    public <T> Optional<ServiceDescriptor> getDescriptor(Class<T> serviceType) {
        return injector.getDescriptor(serviceType);
    }

    public <T> Optional<ServiceDescriptor> getDescriptor(Key<T> key) {
        return injector.getDescriptor(key);
    }

    public boolean hasService(Class<?> key) {
        return injector.hasService(key);
    }

    public boolean hasService(Key<?> key) {
        return injector.hasService(key);
    }

    public <T> T injectMembers(T instance, Lookup lookup) {
        return injector.injectMembers(instance, lookup);
    }

    public Stream<ServiceDescriptor> services() {
        return injector.services();
    }

    public <T> T use(Class<T> key) {
        return injector.use(key);
    }

    public <T> T use(Key<T> key) {
        return injector.use(key);
    }

    public Component findComponent(CharSequence path) {
        requireNonNull(path, "path is null");
        if (path.length() == 0) {
            throw new IllegalArgumentException("Cannot specify an empty (\"\") path");
        }
        if (path.charAt(0) == '/') {
            if (path().toString().equals("/")) {
                return this;
            }
        }
        Component c = children.get(path);
        if (c == null) {
            String p = path.toString();
            String[] splits = p.split("/");
            if (splits.length > 1) {
                AbstractComponent ac = children.get(splits[0]);
                if (ac instanceof InternalContainer) {
                    return ((InternalContainer) ac).findComponent(splits[1]);
                }
            }
        }
        return c;
    }

    /**
     * @param path
     */
    public Component useComponent(CharSequence path) {
        Component c = findComponent(path);
        if (c == null) {
            // Maybe try an match with some fuzzy logic, if children is a resonable size)
            List<?> list = stream().map(e -> e.path()).collect(Collectors.toList());
            throw new IllegalArgumentException("Could not find component with path: " + path + " avilable components:" + list);
        }
        return c;
    }
}
