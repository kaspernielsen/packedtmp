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
package app.packed.component;

import java.util.Collection;
import java.util.Optional;

import app.packed.config.ConfigSite;

/**
 * A component is the basic entity in Packed. Much like everything is a is one of the defining features of Unix, and its
 * derivatives. In packed everything is a component.
 */
public interface Component {

    /**
     * Returns an immutable view of all of this component's children.
     *
     * @return an immutable view of all of this component's children
     */
    Collection<Component> children();

    /**
     * Returns a component stream consisting of this component and all of its descendants in any order.
     *
     * @return a component stream consisting of this component and all of its descendants in any order
     */
    ComponentStream components();

    /**
     * Returns the configuration site of this component.
     * 
     * @return the configuration site of this component
     */
    ConfigSite configurationSite();

    /**
     * Returns the description of this component. Or an empty optional if no description was set when configuring the
     * component.
     *
     * @return the description of this component. Or an empty optional if no description was set when configuring the
     *         component
     *
     * @see ComponentConfiguration#setDescription(String)
     */
    Optional<String> description();

    default <T> Optional<T> get(Feature<T, ?> feature) {
        throw new UnsupportedOperationException();
    }

    default <T> T use(Feature<T, ?> feature) {
        throw new UnsupportedOperationException();
    }

    default Collection<?> features() {
        // Features vs en selvstaendig komponent....
        //// Altsaa det ser jo dumt ud hvis vi har
        //// /Foo
        //// /Foo/Service<String>
        //// /Foo/AnotherComponent

        ///// Dvs ogsaa scheduled jobs bliver lagt paa som meta data, som en feature

        // Ideen er f.eks. at kunne returnere alle services en component exposer, men ikke give adgang til det...
        // How does it relate to AttributeMap?
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the name of this component.
     * <p>
     * If no name is explicitly set by the user when configuring the component. The runtime will automatically generate a
     * unique name (among other components with the same parent).
     *
     * @return the name of this component
     *
     * @see ComponentConfiguration#setName(String)
     */
    String name();

    /**
     * Returns the path of this component.
     *
     * @return the path of this component
     */
    ComponentPath path();
}
