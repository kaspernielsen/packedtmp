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
package packed.internal.graph;

import java.util.function.Consumer;

import app.packed.bundle.InjectorBundle;
import app.packed.inject.Injector;
import app.packed.inject.InjectorConfiguration;
import packed.internal.inject.buildnodes.InternalInjectorConfiguration;

/**
 *
 */
public class DependencyGraph<T extends Injector> {

    InternalInjectorConfiguration rootConfiguration;
    InjectorBundle rootBundle;

    // Fra ContainerBundle.
    // Fra Consumer<ContainerConfiguration>
    // Fra InjectorBundle
    // Fra Consumer<InjectorConfiguration>

    // DP<T> and then build

    public static Injector create(InjectorBundle bundle) {
        throw new UnsupportedOperationException();
    }

    public static Injector create(Consumer<? super InjectorConfiguration> configurator) {
        // InternalInjectorConfiguration c = new
        // InternalInjectorConfiguration(InternalConfigurationSite.ofStack(ConfigurationSiteType.INJECTOR_OF), null);
        // configurator.accept(c);
        // return c.builder.build();
        throw new UnsupportedOperationException();
    }

}