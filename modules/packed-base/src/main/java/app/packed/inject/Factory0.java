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

import java.util.Optional;
import java.util.function.Supplier;

import app.packed.util.InvalidDeclarationException;

/**
 * A {@link Factory} type that uses a {@link Supplier} to create new instances.
 * <p>
 * Is typically used like this:
 *
 * <pre> {@code
 * Factory<Long> f = new Factory0<>(System::currentTimeMillis) {};}</pre>
 * <p>
 * Note that we create a new class inheriting from Factory0 is order to capture information about the type variable (in
 * this case {@code Long}).
 * <p>
 * As an alternative to extending this class, one of the static factory methods can be used:
 *
 * <pre> {@code
 * Factory<Long> f = Factory0.of(System::currentTimeMillis, Long.class);}</pre>
 *
 * The factory created in the last example is functionally equivalent to the factory created in the first example.
 *
 * <p>
 * The supplier must only return null for optional dependencies.
 * 
 * <p>
 * Passing a {@code null} argument to any method or constructor in this class will cause a {@link NullPointerException}
 * to be thrown.
 * 
 * @param <R>
 *            the type of objects this factory constructs
 * @see Factory1
 * @see Factory2
 */
public abstract class Factory0<R> extends Factory<R> {

    /**
     * Creates a new factory, that uses the specified supplier to create new instances.
     *
     * @param supplier
     *            the supplier to use for creating new instances
     * @throws InvalidDeclarationException
     *             if the type variable R could not be determined. Or if R does not represent a valid key, for example,
     *             {@link Optional}
     */
    protected Factory0(Supplier<? extends R> supplier) {
        super(supplier);
    }
}
//
/// ***
// * Returns a factory that uses the specified supplier to create new instances.**
// *
// * @param <T>
// * the type of objects the factory will create*
// * @param supplier
// * the supplier to use for creating new instances*
// * @param type
// * the type of objects the supplier creates*@return a factory that uses the specified supplier to create new
// * instances
// * @return the new factory
// */
//// Don't know if we want to keep them... They are problematic on Factory0, but used for importing nodes from a generic
//// injector
// public static <T> Factory<T> of(Supplier<? extends T> supplier, Class<T> type) {
// return of(supplier, TypeLiteral.of(type));
// }
//
/// ***
// *
// * Returns a factory that uses the specified supplier to create new instances.**
// *
// * @param <T>
// * the type of objects the factory will create*
// * @param supplier
// * the supplier to use for creating new instances*
// * @param type
// * the type of objects the supplier creates*@return a factory that uses the specified supplier to create new
// * instances
// * @return the new factory
// */
// public static <T> Factory<T> of(Supplier<? extends T> supplier, TypeLiteral<T> type) {
// return new Factory<>(new InternalFactory0<>(supplier, type));
// }