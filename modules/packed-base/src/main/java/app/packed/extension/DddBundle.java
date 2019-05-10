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
package app.packed.extension;

/**
 *
 */
public abstract class DddBundle extends AnyBundle {

    private static final BootstrapFactory F = new BootStrapFactoryBuilder().build();

    final ContainerConfiguration spec;

    protected DddBundle() {
        this(F.create());
    }

    private DddBundle(ContainerConfiguration specification) {
        super(specification);
        this.spec = specification;
    }
}