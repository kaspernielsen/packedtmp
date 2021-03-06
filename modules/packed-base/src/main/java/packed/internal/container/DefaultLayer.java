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

import java.util.Set;

import app.packed.container.Bundle;
import app.packed.container.ContainerLayer;
import app.packed.container.Wirelet;

/** The default implementation of Layer. */
class DefaultLayer implements ContainerLayer {

    private final PackedContainerConfiguration container;

    /** All the dependencies of this layer. */
    private final Set<ContainerLayer> dependencies;

    /** The name of the layer. */
    private final String name;

    DefaultLayer(PackedContainerConfiguration container, String name, ContainerLayer... dependencies) {
        this.container = requireNonNull(container);
        this.name = requireNonNull(name, "name is null");
        requireNonNull(dependencies, "dependencies is null");
        for (ContainerLayer l : dependencies) {
            requireNonNull(l, "layer is null");
            if (!(l instanceof DefaultLayer)) {
                throw new IllegalArgumentException("Only Layer instances created by this runtime is allowed, was type " + l.getClass());
            }
        }
        this.dependencies = Set.of(dependencies);
    }

    /** {@inheritDoc} */
    @Override
    public Set<ContainerLayer> dependencies() {
        return dependencies;
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Bundle> T link(T child, Wirelet... wirelets) {
        System.out.println(container);
        return child;
    }

    /** {@inheritDoc} */
    @Override
    public String name() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Bundle> T linkPrivate(T child, Wirelet... wirelets) {
        System.out.println(container);
        return child;
    }
}
