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
package app.packed.inject;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Member;
import java.util.Optional;

import app.packed.container.Component;
import app.packed.util.Nullable;
import app.packed.util.VariableDescriptor;

/**
 * An implementation of injection site used, when requesting a service directly through an injector, for example, via
 * {@link Injector#with(Class)}.
 */
class InjectionSiteForDependency implements InjectionSite {

    /** An optional component, in case the request is via a component's private injector. */
    @Nullable
    private final Component component;

    /** The key of the service that was requested */
    private final Dependency dependency;

    /** The injector from where the service was requested. */
    private final Injector injector;

    InjectionSiteForDependency(Injector injector, Dependency dependency, @Nullable Component component) {
        this.injector = requireNonNull(injector, "injector is null");
        this.dependency = requireNonNull(dependency, "dependency is null");
        this.component = component;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Component> getComponent() {
        return Optional.ofNullable(component);
    }

    /** {@inheritDoc} */
    @Override
    public int getIndex() {
        return dependency.getIndex();
    }

    /** {@inheritDoc} */
    @Override
    public Injector getInjector() {
        return injector;
    }

    /** {@inheritDoc} */
    @Override
    public Key<?> getKey() {
        return dependency.getKey();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Member> getMember() {
        return dependency.getMember();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<VariableDescriptor> getVariable() {
        return dependency.getVariable();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOptional() {
        return dependency.isOptional();
    }

    // public static void main(String[] args) {
    // new Factory1<InjectionSite, Logger>(
    // site -> site.getComponent().isPresent() ? Logger.getLogger(site.getComponent().get().getPath().toString()) :
    // Logger.getAnonymousLogger()) {};
    // }
    //
    // @Provides
    // public static Logger provideLogger(InjectionSite site) {
    // if (site.getComponent().isPresent()) {
    // return Logger.getLogger(site.getComponent().get().getPath().toString());
    // } else {
    // return Logger.getAnonymousLogger();
    // }
    // }
}