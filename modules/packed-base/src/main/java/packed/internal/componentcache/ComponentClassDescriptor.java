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
package packed.internal.componentcache;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

import app.packed.component.ComponentConfiguration;
import app.packed.container.ExtensionActivator;
import app.packed.container.ExtensionHookGroup;
import packed.internal.container.DefaultContainerConfiguration;

/**
 *
 */
// Includere den lookup??? Ja det taenker jeg...

// Ved den alt?????? Alle metoder???
// Man kan ikke installere en component senere vel??
// Som paa magisk vis faar componenten

// Der eksistere end ComponentClassDescriptor per Class+ComponentLookup

public final class ComponentClassDescriptor {

    /** The component type. */
    private final Class<?> componentType;

    private final Instance[] groups;

    /** The simple name of the component type. */
    private volatile String simpleName;

    /**
     * Creates a new descriptor.
     * 
     * @param builder
     *            a builder for this descriptor
     */
    private ComponentClassDescriptor(ComponentClassDescriptor.Builder builder) {
        this.componentType = requireNonNull(builder.componentType);
        this.groups = builder.builders.values().stream().map(e -> e.build()).toArray(i -> new Instance[i]);
    }

    /**
     * Returns the default prefix for the container, if no name is explicitly set.
     * 
     * @return the default prefix for the container, if no name is explicitly set
     */
    public String defaultPrefix() {
        String s = simpleName;
        if (s == null) {
            s = simpleName = componentType.getSimpleName();
        }
        return s;
    }

    public void initialize(DefaultContainerConfiguration container, ComponentConfiguration component) {
        for (Instance c : groups) {
            c.add(container, component);
        }
    }

    /**
     * Returns the type of component.
     * 
     * @return the type of component
     */
    public Class<?> type() {
        return componentType;
    }

    /** A builder object for a component class descriptor. */
    static class Builder {

        static final ClassValue<Class<? extends ExtensionHookGroup<?, ?>>> METHOD_ANNOTATION_ACTIVATOR = new ClassValue<>() {

            @Override
            protected Class<? extends ExtensionHookGroup<?, ?>> computeValue(Class<?> type) {
                ExtensionActivator ae = type.getAnnotation(ExtensionActivator.class);
                return ae == null ? null : ae.value();
            }
        };

        private final IdentityHashMap<Class<? extends ExtensionHookGroup<?, ?>>, Instance.Builder> builders = new IdentityHashMap<>();

        private final ComponentLookup cl;

        /** The component type. */
        private final Class<?> componentType;

        /**
         * @param cl
         * @param componentType
         */
        Builder(ComponentLookup cl, Class<?> componentType) {
            this.cl = requireNonNull(cl);
            this.componentType = requireNonNull(componentType);
        }

        /**
         * Builds and returns a new descriptor.
         * 
         * @return a new descriptor
         */
        ComponentClassDescriptor build() {
            for (Class<?> c = componentType; c != Object.class; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    Annotation[] annotations = field.getAnnotations();
                    for (Annotation a : annotations) {
                        Class<? extends ExtensionHookGroup<?, ?>> cc = METHOD_ANNOTATION_ACTIVATOR.get(a.annotationType());
                        if (cc != null) {
                            builders.computeIfAbsent(cc, m -> new Instance.Builder(componentType, m)).onAnnotatedField(cl, field, a);
                        }
                    }
                }
                for (Method method : c.getDeclaredMethods()) {
                    Annotation[] annotations = method.getAnnotations();
                    for (Annotation a : annotations) {
                        Class<? extends ExtensionHookGroup<?, ?>> cc = METHOD_ANNOTATION_ACTIVATOR.get(a.annotationType());
                        if (cc != null) {
                            builders.computeIfAbsent(cc, m -> new Instance.Builder(componentType, m)).onAnnotatedMethod(cl, method, a);
                        }
                    }
                }
            }
            return new ComponentClassDescriptor(this);
        }
    }
}