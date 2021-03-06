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
package packed.internal.hook;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.IdentityHashMap;
import java.util.function.Supplier;

import app.packed.hook.AnnotatedFieldHook;
import app.packed.hook.AnnotatedMethodHook;
import app.packed.hook.AnnotatedTypeHook;
import app.packed.hook.OnHook;
import app.packed.util.IllegalAccessRuntimeException;
import app.packed.util.InvalidDeclarationException;
import app.packed.util.NativeImage;
import packed.internal.util.StringFormatter;
import packed.internal.util.ThrowableUtil;
import packed.internal.util.TypeUtil;
import packed.internal.util.TypeVariableExtractorUtil;

/**
 * An {@link OnHookAggregatorDescriptor} wraps
 */
final class OnHookAggregatorDescriptor {

    /** A cache of information for aggregator types. */
    private static final ClassValue<OnHookAggregatorDescriptor> CACHE = new ClassValue<>() {

        @SuppressWarnings("unchecked")
        @Override
        protected OnHookAggregatorDescriptor computeValue(Class<?> type) {
            return new OnHookAggregatorDescriptor.Builder((Class<? extends Supplier<?>>) type).build();
        }
    };

    /** The type of aggregator. */
    private final Class<?> aggregatorType;

    /** A map of all methods that takes a {@link AnnotatedFieldHook}. */
    final IdentityHashMap<Class<? extends Annotation>, MethodHandle> annotatedFields;

    /** A map of all methods that takes a {@link AnnotatedMethodHook}. */
    final IdentityHashMap<Class<? extends Annotation>, MethodHandle> annotatedMethods;

    /** A map of all methods that takes a {@link AnnotatedMethodHook}. */
    final IdentityHashMap<Class<? extends Annotation>, MethodHandle> annotatedTypes;

    /** A constructor for creating new aggregator instance. */
    private final MethodHandle constructor;

    /** The type of result the aggregator produces. */
    private final Class<?> resultType;

    /**
     * Creates a new aggregator from the specified builder.
     * 
     * @param builder
     *            the builder to create an aggregator from
     */
    private OnHookAggregatorDescriptor(Builder builder) {
        this.constructor = requireNonNull(builder.constructor);
        this.aggregatorType = builder.aggregatorType;
        this.resultType = builder.resultType;
        this.annotatedMethods = builder.annotatedMethods;
        this.annotatedFields = builder.annotatedFields;
        this.annotatedTypes = builder.annotatedTypes;
    }

    void invokeOnHook(Supplier<?> aggregator, AnnotatedFieldHook<?> hook) {
        if (aggregator.getClass() != aggregatorType) {
            throw new IllegalArgumentException("Must be specify an aggregator of type " + aggregatorType + ", but was " + aggregator.getClass());
        }
        Class<? extends Annotation> an = hook.annotation().annotationType();

        MethodHandle om = annotatedFields.get(an);
        if (om == null) {
            // We will normally have checked for this previously
            System.out.println(an);
            System.out.println(annotatedFields.keySet());
            throw new IllegalStateException("" + an);
        }

        try {
            om.invoke(aggregator, hook);
        } catch (Throwable e) {
            ThrowableUtil.rethrowErrorOrRuntimeException(e);
            throw new RuntimeException(e);
        }
    }

    void invokeOnHook(Supplier<?> aggregator, AnnotatedMethodHook<?> hook) {
        if (aggregator.getClass() != aggregatorType) {
            throw new IllegalArgumentException("Must be specify an aggregator of type " + aggregatorType + ", but was " + aggregator.getClass());
        }
        Class<? extends Annotation> an = hook.annotation().annotationType();

        MethodHandle om = annotatedMethods.get(an);
        if (om == null) {
            System.out.println(an);
            System.out.println(annotatedMethods.keySet());
            throw new IllegalStateException("" + an);
        }

        try {
            om.invoke(aggregator, hook);
        } catch (Throwable e) {
            ThrowableUtil.rethrowErrorOrRuntimeException(e);
            throw new RuntimeException(e);
        }
    }

    void invokeOnHook(Supplier<?> aggregator, AnnotatedTypeHook<?> hook) {
        if (aggregator.getClass() != aggregatorType) {
            throw new IllegalArgumentException("Must be specify an aggregator of type " + aggregatorType + ", but was " + aggregator.getClass());
        }
        Class<? extends Annotation> an = hook.annotation().annotationType();
        System.out.println(an + " " + annotatedTypes);
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new aggregator (supplier) object.
     * 
     * @return a new aggregator object
     */
    Supplier<?> newAggregatorInstance() {
        try {
            return (Supplier<?>) constructor.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the type of result the aggregator produces.
     * 
     * @return the type of result the aggregator produces
     */
    final Class<?> resultType() {
        return resultType;
    }

    public static OnHookAggregatorDescriptor get(Class<? extends Supplier<?>> clazz) {
        return CACHE.get(clazz);
    }

    private static class Builder {

        private final Class<? extends Supplier<?>> aggregatorType;

        final IdentityHashMap<Class<? extends Annotation>, MethodHandle> annotatedFields = new IdentityHashMap<>();

        final IdentityHashMap<Class<? extends Annotation>, MethodHandle> annotatedMethods = new IdentityHashMap<>();

        final IdentityHashMap<Class<? extends Annotation>, MethodHandle> annotatedTypes = new IdentityHashMap<>();

        private MethodHandle constructor;

        final Class<?> resultType;

        @SuppressWarnings({ "rawtypes" })
        private Builder(Class<? extends Supplier<?>> aggregatorType) {
            this.aggregatorType = requireNonNull(aggregatorType);
            resultType = (Class) TypeVariableExtractorUtil.findTypeParameterFromInterface(aggregatorType, Supplier.class, 0);
        }

        private void addHookMethod(Lookup lookup, Method method, OnHook onHook) {
            if (onHook.aggreateWith() != ExtensionHookPerComponentGroup.NoAggregator.class) {
                throw new InvalidDeclarationException("Cannot specify a aggregate class '" + onHook.aggreateWith().getCanonicalName()
                        + "' for a method on aggregator class, method = " + StringFormatter.format(method));
            }
            if (method.getParameterCount() != 1) {
                throw new InvalidDeclarationException(
                        "Methods annotated with @OnHook on hook aggregates must have exactly one parameter, method = " + StringFormatter.format(method));
            }

            Parameter p = method.getParameters()[0];
            Class<?> cl = p.getType();

            if (cl == AnnotatedFieldHook.class) {
                addHookMethod0(lookup, method, p, annotatedFields);
            } else if (cl == AnnotatedMethodHook.class) {
                addHookMethod0(lookup, method, p, annotatedMethods);
            } else if (cl == AnnotatedTypeHook.class) {
                addHookMethod0(lookup, method, p, annotatedTypes);
            } else {
                throw new InvalidDeclarationException("Methods annotated with @OnHook on hook aggregates must have exactly one parameter of type "
                        + AnnotatedFieldHook.class.getSimpleName() + ", " + AnnotatedMethodHook.class.getSimpleName() + ", or"
                        + AnnotatedTypeHook.class.getSimpleName() + ", " + " for method = " + StringFormatter.format(method));
            }
        }

        private void addHookMethod0(MethodHandles.Lookup lookup, Method method, Parameter p,
                IdentityHashMap<Class<? extends Annotation>, MethodHandle> annotations) {
            // if (ComponentClassDescriptor.Builder.METHOD_ANNOTATION_ACTIVATOR.get(annotationType) != type) {
            // throw new IllegalStateException("Annotation @" + annotationType.getSimpleName() + " must be annotated with @"
            // + Activate.class.getSimpleName() + "(" + extensionClass.getSimpleName() + ".class) to be used with this method");
            // }
            ParameterizedType pt = (ParameterizedType) p.getParameterizedType();
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotationType = (Class<? extends Annotation>) pt.getActualTypeArguments()[0];

            if (annotations.containsKey(annotationType)) {
                throw new InvalidDeclarationException("There are multiple methods annotated with @OnHook on "
                        + StringFormatter.format(method.getDeclaringClass()) + " that takes " + p.getParameterizedType());
            }
            // Check that we have not added another previously for the same annotation

            MethodHandle mh;
            try {
                method.setAccessible(true);
                mh = lookup.unreflect(method);
            } catch (IllegalAccessException | InaccessibleObjectException e) {
                throw new IllegalAccessRuntimeException("In order to use the extension " + StringFormatter.format(aggregatorType) + ", the module '"
                        + aggregatorType.getModule().getName() + "' in which the extension is located must be 'open' to 'app.packed.base'", e);
            }

            NativeImage.registerMethod(method);

            annotations.put(annotationType, mh);
        }

        OnHookAggregatorDescriptor build() {
            TypeUtil.checkClassIsInstantiable(aggregatorType);

            Constructor<?> constructor;
            try {
                constructor = aggregatorType.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                        "The extension " + StringFormatter.format(aggregatorType) + " must have a no-argument constructor to be installed.");
            }

            Lookup lookup = MethodHandles.lookup();
            try {
                constructor.setAccessible(true);
                this.constructor = lookup.unreflectConstructor(constructor);
            } catch (IllegalAccessException | InaccessibleObjectException e) {
                throw new IllegalAccessRuntimeException("In order to use the hook aggregate " + StringFormatter.format(aggregatorType) + ", the module '"
                        + aggregatorType.getModule().getName() + "' in which the class is located must be 'open' to 'app.packed.base'", e);
            }

            // Find all methods annotated with @OnHook
            for (Class<?> c = aggregatorType; c != Object.class; c = c.getSuperclass()) {
                for (Method method : c.getDeclaredMethods()) {
                    OnHook oh = method.getAnnotation(OnHook.class);
                    if (oh != null) {
                        addHookMethod(lookup, method, oh);
                    }
                }
            }
            if (annotatedFields.isEmpty() && annotatedMethods.isEmpty() && annotatedTypes.isEmpty()) {
                throw new IllegalArgumentException("Hook aggregator '" + StringFormatter.format(aggregatorType)
                        + "' must define at least one method annotated with @" + OnHook.class.getSimpleName());
            }

            // Register the constructor if we are generating a native image
            NativeImage.registerConstructor(constructor);

            return new OnHookAggregatorDescriptor(this);
        }
    }
}
