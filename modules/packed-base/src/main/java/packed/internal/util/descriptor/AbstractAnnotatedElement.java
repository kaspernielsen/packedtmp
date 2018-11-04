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
package packed.internal.util.descriptor;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import app.packed.inject.InjectionException;
import packed.internal.inject.JavaXInjectSupport;
import packed.internal.util.AnnotationUtil;

/** The default abstract implementation of a {@link AnnotatedElement}. */
public abstract class AbstractAnnotatedElement implements AnnotatedElement {

    /** The annotations present on the element */
    // TODO fix modifier
    public final Annotation[] annotations;

    /** The annotated element. */
    private final AnnotatedElement element;

    // TODO fix declared vs non-declared annotation
    // https://djitz.com/neu-mscs/java-reflection-notes-getdeclaredannotations-vs-getannotations-method/
    // Think we need to store both for Class
    // And then just ditch annotationTypes....
    // TODO also check for Repeatable annotations
    // HMMM not sure it isAnnotationPresent works for repeatable annotations?
    // We only store the List instance
    // TODO test with some inherited annotations

    /**
     * Creates a new AbstractAnnotatedElement from an {@link AnnotatedElement}.
     *
     * @param element
     *            the annotated element
     */
    public AbstractAnnotatedElement(AnnotatedElement element) {
        this.element = element;
        this.annotations = element.getAnnotations();
    }

    /**
     * Returns the type of element, is typically used for error messages.
     *
     * @return the type of element
     */
    public abstract String descriptorTypeName();

    /**
     * Attempts to find a qualifier annotation on this element.
     *
     * @return a optional qualifier
     * @throws InjectionException
     *             if more than one qualifier are present on the element
     */
    public final Optional<Annotation> findQualifiedAnnotation() {
        Annotation qualifier = null;
        for (Annotation a : annotations) {
            Class<? extends Annotation> annotationType = a.annotationType();
            if (JavaXInjectSupport.isQualifierAnnotationPresent(annotationType)) {
                AnnotationUtil.validateRuntimeRetentionPolicy(a.annotationType());
                if (qualifier != null) {
                    List<Class<? extends Annotation>> annotations = List.of(element.getAnnotations()).stream().map(Annotation::annotationType)
                            .filter(JavaXInjectSupport::isQualifierAnnotationPresent).collect(Collectors.toList());
                    throw new InjectionException("Multiple qualifiers found on element '" + element + "', qualifiers = " + annotations);
                }
                qualifier = a;
            }
        }
        return Optional.ofNullable(qualifier);
    }

    /**
     * Invokes the specified action for each annotation present on this element.
     *
     * @param action
     *            the action to invoke
     * @throws NullPointerException
     *             if the specified action is null
     * @see AnnotatedElement#getAnnotations()
     */
    public final void forEachAnnotation(Consumer<? super Annotation> action) {
        requireNonNull(action, "action is null");
        for (Annotation a : annotations) {
            action.accept(a);
        }
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public final <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i].annotationType() == annotationClass) {
                return (T) annotations[i];
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public final Annotation[] getAnnotations() {
        return element.getAnnotations();// Safe to use for outside
    }

    /** {@inheritDoc} */
    @Override
    public final <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        return element.getAnnotationsByType(annotationClass);
    }

    /** {@inheritDoc} */
    @Override
    public final <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return element.getDeclaredAnnotation(annotationClass);
    }

    /** {@inheritDoc} */
    @Override
    public final Annotation[] getDeclaredAnnotations() {
        return element.getDeclaredAnnotations();
    }

    /** {@inheritDoc} */
    @Override
    public final <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        return element.getDeclaredAnnotationsByType(annotationClass);
    }

    /**
     * Returns whether or not there are any annotations present on the element.
     *
     * @return <code>true</code> if at least one annotation is present, <code>false</code> otherwise
     */
    public final boolean isAnnotated() {
        return annotations.length > 0;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        requireNonNull(annotationClass, "annotationClass is null");
        for (Annotation a : annotations) {
            if (a.annotationType() == annotationClass) {
                return true;
            }
        }
        return false;
    }
}