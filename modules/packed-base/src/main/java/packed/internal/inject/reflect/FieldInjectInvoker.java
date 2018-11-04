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
package packed.internal.inject.reflect;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import app.packed.inject.Dependency;
import app.packed.inject.Inject;
import app.packed.inject.InjectionException;
import app.packed.util.Nullable;
import packed.internal.inject.JavaXInjectSupport;
import packed.internal.util.descriptor.InternalFieldDescriptor;

/** This class represents a field annotated with the Inject annotation. */
public final class FieldInjectInvoker extends FieldInvoker {

    /** The field represented as a dependency */
    private final Dependency dependency;

    /**
     * @param descriptor
     * @param lookup
     */
    public FieldInjectInvoker(InternalFieldDescriptor descriptor, Lookup lookup) {
        super(descriptor, lookup);
        this.dependency = Dependency.of(descriptor);
    }

    public void injectInstance(Object instance, Object value) {
        setField(instance, dependency.wrapIfOptional(value));
    }

    /**
     * Returns the annotated field as a dependency.
     *
     * @return the annotated field as a dependency
     */
    public Dependency getDependency() {
        return dependency;
    }

    // For now this is a separate method, when we also want to support components fields..
    @Nullable
    private static FieldInjectInvoker tryCreate(Field field, Lookup lookup) {
        // First check for @Inject
        if (JavaXInjectSupport.isInjectAnnotationPresent(field)) {
            InternalFieldDescriptor descriptor = InternalFieldDescriptor.of(field);
            if (Modifier.isStatic(field.getModifiers())) {
                throw new InjectionException(fieldWithAnnotationCannotBeStatic(descriptor, Inject.class));
            } else if (Modifier.isFinal(field.getModifiers())) {
                throw new InjectionException(fieldWithAnnotationCannotBeFinal(descriptor, Inject.class));
            }
            return new FieldInjectInvoker(descriptor, lookup);
        }
        return null;
    }

    static Collection<FieldInjectInvoker> findInjectableFields(Class<?> clazz, Lookup lookup) {
        ArrayList<FieldInjectInvoker> result = null;
        for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                // Extract valid annotations
                // First check for @Inject
                FieldInjectInvoker i = tryCreate(field, lookup);
                if (i != null) {
                    if (result == null) {
                        result = new ArrayList<>(2);
                    }
                    result.add(i);
                }
            }
        }
        return result == null ? List.of() : List.copyOf(result);
    }
}