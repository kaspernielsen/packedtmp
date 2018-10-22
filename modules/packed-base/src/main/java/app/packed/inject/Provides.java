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

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Fields and Methods are <b>not</b< automatically injected when returned by methods annotated with {@link Provides}.
 * {@link Injector#injectMembers(Object)} can be used to inject members if needed.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Provides {

    /**
     * Returns a description of the service provided by this method.
     *
     * @return a description of the service provided by this method
     */
    String description() default "";

    /**
     * The bind mode of the provided method, the default is to eagerly create a new instance.
     */
    // Make caching mode plays well together if we allow null returns to indicate look in parent container
    BindMode bindMode() default BindMode.EAGER_SINGLETON;

    // Default is return type for methods or field type, on field bla bla.
    // skal maaske baade have real type og exposedType
    // Maaske have en exposedAs type ogsaa
    Class<?> type() default Class.class;

    /**
     * The default value is {@link Qualifier} which indicates that it ignores any annotations
     *
     * @return
     */
    Class<? extends Annotation> wildcardQualifier() default Qualifier.class;

    // Cannot both have wildcardType and wildcardAnnotation
    // Should have List<?> return Type
    // Key.wildcardType(Class<?> type)
    // Key.wildcardAnnotation(Class<? extends Annotation>)
    Class<?> wildcardType() default Class.class;
}
/// **
// * A single instance is created for each container on demand. Concurrent calls will block.
// * <p>
// * Injection will only be available via container injectors, component instances or via a {@link Component#injector()
// * components injector}. Injection via a containers injector or a standalone injector is not supported.
// */
// PER_CONTAINER,
//
/// **
// * A single instance is created for each component on demand. Concurrent calls will block.
// * <p>
// * Injection will only be available for component instances or via a {@link Component#injector() components injector}.
// * Injection via a containers injector or a standalone injector is not supported.
// */
// PER_COMPONENT,

/** The default value for {@link Provides#wildcardQualifier()} */
@interface ProvidesQualifierNone {}
// int priority() default 1;

// Class<? extends Annotation>[] ifAnyMethodAnnotatedWith() default {};
//
/// **
// * The default value is {@link Qualifier} which indicates that it ignores any annotations
// *
// * @return
// */
// Class<? extends Annotation> wildcardAnnotation() default ProvidesQualifierNone.class;
/**
 * Only provide instances for object whose owning type is annotated with a particular annotation
 *
 * @return the annotations that must be present on the declaring class
 */
// What about annotations placed on interfaces implemented or super types??????
// Class<? extends Annotation>[] ifTypeAnnotatedWith() default {};
