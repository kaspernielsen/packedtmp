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
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import app.packed.component.ComponentConfiguration;
import app.packed.container.Extension;
import app.packed.container.ExtensionHookGroup;
import app.packed.util.MethodDescriptor;
import packed.internal.componentcache.ExtensionHookGroupConfiguration.OnMethodDescription;
import packed.internal.container.DefaultContainerConfiguration;
import packed.internal.inject.buildtime.OldDefaultComponentConfiguration;

/**
 *
 */
public final class Instance {

    @SuppressWarnings("rawtypes")
    private final BiConsumer build;

    /** The type of extension. */
    private final Class<? extends Extension<?>> extensionType;

    private Instance(Builder b) {
        this.extensionType = requireNonNull(b.conf.extensionClass);
        build = requireNonNull(b.b.get());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    void add(DefaultContainerConfiguration container, OldDefaultComponentConfiguration component) {
        Extension extension = container.use((Class) extensionType);
        build.accept(component, extension);
    }

    static class Builder {

        final Supplier<BiConsumer<ComponentConfiguration, ?>> b;

        /** The component type */
        final Class<?> componentType;

        final ExtensionHookGroupConfiguration conf;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Builder(Class<?> componentType, Class<? extends ExtensionHookGroup<?, ?>> cc) {
            this.componentType = requireNonNull(componentType);
            this.conf = ExtensionHookGroupConfiguration.FOR_CLASS.get(cc);
            this.b = (Supplier) conf.egc.newBuilder(componentType);
        }

        Instance build() {
            return new Instance(this);
        }

        void onAnnotatedField(ComponentLookup lookup, Field field, Annotation annotation) {

        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        void onAnnotatedMethod(ComponentLookup lookup, Method method, Annotation annotation) {
            for (Object o : conf.list) {
                if (o instanceof ExtensionHookGroupConfiguration.OnMethodDescription) {
                    ExtensionHookGroupConfiguration.OnMethodDescription omd = (OnMethodDescription) o;
                    if (omd.annotationType == annotation.annotationType()) {
                        ((BiConsumer) omd.consumer).accept(b, MethodDescriptor.of(method));
                    }
                }
            }
            // conf.forAnnotatedMethods();
            // MethodHandle mh = lookup.acquireMethodHandle(componentType, method);
        }

    }
}
