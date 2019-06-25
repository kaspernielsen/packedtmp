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
import java.util.LinkedHashMap;
import java.util.Optional;

import app.packed.component.ComponentConfiguration;
import app.packed.component.ComponentPath;
import app.packed.config.ConfigSite;
import app.packed.container.AnyBundle;
import app.packed.container.ContainerConfiguration;
import app.packed.util.Nullable;
import packed.internal.config.site.InternalConfigurationSite;
import packed.internal.container.DefaultContainerConfiguration.NameWirelet;

/** An abstract base class for the configuration of a component. */
abstract class AbstractComponentConfiguration {

    /** Any children this component might have, in order of insertion. */
    LinkedHashMap<String, AbstractComponentConfiguration> children;

    DefaultComponentConfiguration current;

    /** The description of the component. */
    @Nullable
    String description;

    /** The name of the component. */
    @Nullable
    String name;

    private State state = State.INITIAL;

    /** Any parent that the component has. */
    @Nullable
    final DefaultContainerConfiguration parent;

    /** The configuration site of the component. */
    private final InternalConfigurationSite site;

    AbstractComponentConfiguration(InternalConfigurationSite site, DefaultContainerConfiguration parent) {
        this.site = requireNonNull(site);
        this.parent = parent;
    }

    protected void checkConfigurable() {}

    public ConfigSite configurationSite() {
        return site;
    }

    @Nullable
    public final String getDescription() {
        return description;
    }

    public final String getName() {
        String n = name;
        if (n == null) {
            lazyInitializeName(State.GET_NAME_INVOKED, null);
        }
        return name;
    }

    private void lazyInitializeName(E e, String name) {
        if (!name.endsWith("?")) {
            if (parent == null || parent.children == null || !parent.children.containsKey(name)) {
                this.name = name;
                return;
            }
            throw new RuntimeException("Name already exist " + name);
        }

        String prefix = name.substring(0, name.length() - 1);
        String newName = prefix;
        int counter = 0;
        for (;;) {
            if (parent.children == null || !parent.children.containsKey(newName)) {
                name = newName;
                return;
            }
            // Maybe now keep track of the counter... In a prefix hashmap, Its probably benchmarking code though
            // But it could also be a host???
            newName = prefix + counter++;
        }
    }

    protected void lazyInitializeName(State reason, String name) {
        if (this.name != null) {
            return;
        }
        E e = E.IMPLICIT;
        if (name != null) {
            e = E.BY_SET;
        }
        String n = name;
        if (this instanceof DefaultContainerConfiguration) {
            Optional<NameWirelet> o = ((DefaultContainerConfiguration) this).wirelets().last(NameWirelet.class);
            if (o.isPresent()) {
                n = o.get().name;
                e = E.BY_WIRE;
            }
        }
        if (n == null) {
            if (parent == null) {
                if (this instanceof DefaultContainerConfiguration) {
                    @Nullable
                    AnyBundle bundle = ((DefaultContainerConfiguration) this).bundle;
                    if (bundle != null) {
                        String nnn = bundle.getClass().getSimpleName();
                        if (nnn.endsWith("Bundle")) {
                            nnn = nnn.substring(0, nnn.length() - 6);
                        }
                        if (nnn.length() > 0) {
                            // checkName, if not just App
                            this.name = nnn;
                            return;
                        }
                    }
                }
                this.name = "App";
                return; // TODO fix
            }
            if (this instanceof DefaultContainerConfiguration) {
                n = ((DefaultContainerConfiguration) this).ccc.defaultPrefix();
            } else {
                n = ((DefaultComponentConfiguration) this).ccd.defaultPrefix();
            }
        }

        lazyInitializeName(e, n);
        this.state = reason;
    }

    AbstractComponentConfiguration setDescription(String description) {
        requireNonNull(description, "description is null");
        checkConfigurable();
        this.description = description;
        return this;
    }

    AbstractComponentConfiguration setName(String name) {
        requireNonNull(name, "name is null");
        checkName(name);
        checkConfigurable();
        if (state == State.INITIAL) {
            lazyInitializeName(State.SET_NAME_INVOKED, name);
            // We only update this.name if wiring sets a name, never naming state
            // So make sure we do it here
            this.state = State.SET_NAME_INVOKED;
            return this;
        } else if (state == State.SET_NAME_INVOKED) {
            throw new IllegalStateException("setName has already been invoked once");
        } else if (state == State.LINK_INVOKED) {
            throw new IllegalStateException("Cannot call this method after containerConfiguration.link has been invoked");
        } else if (state == State.INSTALL_INVOKED) {
            // How this work @Install????
            // Maybe we can have a installFromScan method(), that is implicit called from the end of the configure method
            throw new IllegalStateException("Cannot call this method after installing new components in the container");
        } else /* if (namingState == NamingState.GET_NAME_CALLED) */ {
            throw new IllegalStateException("Cannot call #setName(String) after #getName() has been invoked.");
        }
    }

    public ComponentPath path() {
        // Technically we don't need to do this for root containers. However, its more consistent.
        // TODO freeze current componentName
        lazyInitializeName(State.PATH_INVOKED, null);
        if (parent == null) {
            return ComponentPath.ROOT;
        }
        ArrayList<String> l = new ArrayList<>();
        path0(l);
        return new DefaultComponentPath(l.toArray(e -> new String[e]));
    }

    public void path0(ArrayList<String> addTo) {
        if (parent != null) {
            parent.path0(addTo);
            addTo.add(requireNonNull(name));
        }
    }

    /**
     * Checks the name of the component.
     * 
     * @param name
     *            the name to check
     * @return the name if valid
     */
    private static String checkName(String name) {
        if (name != null) {

        }
        return name;
    }

    enum E {
        BY_SET, BY_WIRE, IMPLICIT
    }

    enum State {

        /** The initial state. */
        INITIAL,

        /** {@link ComponentConfiguration#getName()} or {@link ContainerConfiguration#getName()} has been invoked. */
        GET_NAME_INVOKED,

        /** One of the install component methods has been invoked. */
        INSTALL_INVOKED,

        /** {@link ContainerConfiguration#link(AnyBundle, app.packed.container.Wirelet...)} has been invoked. */
        LINK_INVOKED,

        /** One of the install component methods has been invoked. */
        PATH_INVOKED,

        /** */
        SET_NAME_INVOKED,

        /** */
        FINAL;
    }
}