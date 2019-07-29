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
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import app.packed.artifact.ArtifactInstantiationContext;
import app.packed.component.ComponentConfiguration;
import app.packed.container.ContainerConfiguration;
import app.packed.container.Extension;
import app.packed.container.ExtensionHookProcessor;
import packed.internal.container.PackedContainerConfiguration;
import packed.internal.util.ThrowableUtil;

/**
 * We have a group for a collection of hooks/annotations. A component can have multiple groups.
 */
public final class GroupDescriptor {

    @SuppressWarnings("rawtypes")
    private final BiConsumer build;

    /** The type of extension. */
    private final Class<? extends Extension> extensionType;

    final List<MethodConsumer<?>> methodConsumers;

    private GroupDescriptor(Builder b) {
        this.extensionType = requireNonNull(b.conf.extensionClass);
        this.build = requireNonNull(b.b.onBuild());
        this.methodConsumers = List.copyOf(b.consumers);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    void add(PackedContainerConfiguration container, ComponentConfiguration component) {
        Extension extension = container.use((Class) extensionType);
        build.accept(component, extension);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static class Builder {

        final ExtensionHookProcessor<?> b;

        /** The component type */
        final Class<?> componentType;

        final HookGroup conf;

        private ArrayList<MethodConsumer<?>> consumers = new ArrayList<>();

        Builder(Class<?> componentType, Class<? extends ExtensionHookProcessor<?>> cc) {
            this.componentType = requireNonNull(componentType);
            this.conf = HookGroup.FOR_CLASS.get(cc);
            this.b = conf.instantiate();
        }

        GroupDescriptor build() {
            return new GroupDescriptor(this);
        }

        void onAnnotatedField(ComponentLookup lookup, Field field, Annotation annotation) {
            conf.invokeHookOnAnnotatedField(b, new PackedAnnotatedFieldHook(lookup.lookup(), field, annotation));
        }

        void onAnnotatedMethod(ComponentLookup lookup, Method method, Annotation annotation) {
            conf.invokeHookOnAnnotatedMethod(annotation.annotationType(), b, new PackedAnnotatedMethodHook(lookup.lookup(), method, annotation, consumers));
        }
    }
}

class MethodConsumer<S> {
    final BiConsumer<S, Runnable> consumer;
    final Class<S> key;
    final MethodHandle mh;

    /**
     * @param key
     * @param consumer
     */
    public MethodConsumer(Class<S> key, BiConsumer<S, Runnable> consumer, MethodHandle mh) {
        this.key = requireNonNull(key);
        this.consumer = requireNonNull(consumer, "consumer is null");
        this.mh = requireNonNull(mh);

    }

    void prepare(ContainerConfiguration cc, ArtifactInstantiationContext ic) {
        S s = ic.use(cc, key);
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    mh.invoke();
                } catch (Throwable e) {
                    ThrowableUtil.rethrowErrorOrRuntimeException(e);
                    throw new RuntimeException(e);
                }
            }
        };
        consumer.accept(s, r);
    }
}
