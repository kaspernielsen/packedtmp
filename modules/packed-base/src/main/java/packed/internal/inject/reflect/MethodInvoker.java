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

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;

import app.packed.inject.IllegalAccessRuntimeException;
import packed.internal.util.descriptor.InternalFieldDescriptor;
import packed.internal.util.descriptor.InternalMethodDescriptor;

/**
 *
 */
public class MethodInvoker {

    final InternalMethodDescriptor descriptor;

    final MethodHandle handle;

    MethodInvoker(InternalMethodDescriptor descriptor, Lookup lookup) {
        this.descriptor = descriptor;
        try {
            this.handle = descriptor.unreflect(lookup);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessRuntimeException("method " + descriptor + " is not accessible for lookup object " + lookup, e);
        }
    }

    public InternalMethodDescriptor descriptor() {
        return descriptor;
    }

    public Object invoke(Object... arguments) throws Throwable {
        return handle.invokeWithArguments(arguments);
    }

    String cannotHaveBothAnnotations(Class<? extends Annotation> annotationType1, Class<? extends Annotation> annotationType2) {
        return "Cannot use both @" + annotationType1.getSimpleName() + " and @" + annotationType1.getSimpleName() + " on method: " + descriptor
                + ", to resolve remove one of the annotations.";
    }

    /**
     * Creates an error message for using an annotation on a final field.
     *
     * @param field
     *            the field
     * @param annotationType
     *            the annotation
     * @return the error message
     */
    static String fieldWithAnnotationCannotBeFinal(InternalFieldDescriptor field, Class<? extends Annotation> annotationType) {
        return "Cannot use @" + annotationType.getSimpleName() + " on final field: " + field + ", to resolve remove @" + annotationType.getSimpleName()
                + " or make the field non-final";
    }

    /**
     * Creates an error message for using an annotation on a static field.
     *
     * @param field
     *            the field
     * @param annotationType
     *            the annotation
     * @return the error message
     */
    static String fieldWithAnnotationCannotBeStatic(InternalFieldDescriptor field, Class<? extends Annotation> annotationType) {
        return "Cannot use @" + annotationType.getSimpleName() + " on static field: " + field + ", to resolve remove @" + annotationType.getSimpleName()
                + " or make the field non-static";
    }

    /**
     * Creates an error message for using an annotation on a field that is not final.
     *
     * @param field
     *            the field
     * @param annotationType
     *            the annotation
     * @return the error message
     */
    static String fieldWithAnnotationMustBeFinal(InternalFieldDescriptor field, Class<? extends Annotation> annotationType) {
        return "Fields annotated with @" + annotationType.getSimpleName() + " must be final, field = " + field + ", to resolve remove @"
                + annotationType.getSimpleName() + " or make the field final";
    }
}