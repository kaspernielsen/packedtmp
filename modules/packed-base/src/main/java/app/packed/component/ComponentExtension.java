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

import app.packed.container.Extension;
import app.packed.inject.Factory;
import packed.internal.container.DefaultContainerConfiguration;

/**
 * An extension that provides basic functionality for installing components.
 */
public final class ComponentExtension extends Extension<ComponentExtension> {

    /** Creates a new component extension. */
    ComponentExtension() {}

    // install
    // noget med Main, Entry points....
    // Man kan f.eks. disable et Main.... EntryPointExtension....

    // @Main skal jo pege et paa en eller anden extension...

    private DefaultContainerConfiguration configuration0() {
        return (DefaultContainerConfiguration) configuration();
    }

    // Selvfoelelig er det hele komponenter... Ogsaa scoped
    // Vi skal ikke til at have flere scans...

    public ComponentConfiguration install(Class<?> implementation) {
        return configuration0().install(implementation);
    }

    // @Scoped
    // @Install()

    // Why export
    // Need to export

    public ComponentConfiguration install(Factory<?> factory) {
        return configuration0().install(factory);
    }

    public ComponentConfiguration install(Object instance) {
        return configuration0().install(instance);
    }

    public ComponentConfiguration installHelper(Class<?> implementation) {
        return configuration0().installHelper(implementation);
    }

    // Alternative to ComponentScan
    public void scanForInstall(Class<?>... classesInPackages) {}

    public void scan(String... packages) {}
}
