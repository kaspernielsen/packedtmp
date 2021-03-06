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
package packed.internal.invoke;

import static java.util.Objects.requireNonNull;
import static packed.internal.util.StringFormatter.format;

import java.lang.invoke.MethodHandles.Lookup;

import app.packed.inject.Factory;
import app.packed.inject.InjectionException;
import app.packed.util.Nullable;
import app.packed.util.TypeLiteral;

/**
 * The internal version of the {@link Factory} class.
 * <p>
 * Instances of this this class are <b>never</b> exposed to users.
 */
// Ideen er vel at lave hvad vi ville med MethodHandles her iestedet for...

// checker aldrig for null.
// Men checker den korrekte type???
public abstract class FunctionHandle<T> extends Object {

    //////// TYPES (Raw)
    // ExactType... -> Instance, Constructor
    // LowerBoundType, Field, Method
    // PromisedType -> Fac0,Fac1,Fac2,

    /// TypeLiteral<- Always the promised, key must be assignable via raw type
    ///////////////

    // TypeLiteral
    // actual type

    // Correctness
    // Instance -> Lowerbound correct, upper correct
    // Executable -> Lower bound maybe correct (if exposedType=return type), upper correct if final return type
    // Rest, unknown all
    // Bindable -> has no effect..

    // static {
    // Dependency.of(String.class);// Initializes InternalApis for InternalFactory
    // }

    // Ideen er her. at for f.eks. Factory.of(XImpl, X) saa skal der stadig scannes paa Ximpl og ikke paa X
    final Class<?> actualType;

    /** The dependencies for this factory. */

    private final Class<? super T> type;

    /** The type of objects this factory creates. */
    public final TypeLiteral<T> typeLiteral;

    public FunctionHandle(TypeLiteral<T> typeLiteralOrKey) {
        this(typeLiteralOrKey, typeLiteralOrKey.rawType());
    }

    public FunctionHandle(TypeLiteral<T> typeLiteralOrKey, Class<?> actualType) {
        requireNonNull(typeLiteralOrKey, "typeLiteralOrKey is null");
        this.typeLiteral = typeLiteralOrKey;
        this.type = typeLiteral.rawType();
        this.actualType = requireNonNull(actualType);
    }

    protected T checkLowerbound(T instance) {
        if (!type.isInstance(instance)) {
            throw new InjectionException("Expected factory to produce an instance of " + format(type) + " but was " + instance.getClass());
        }
        return instance;
    }

    /**
     * Returns the type of objects this operation returns on invocation.
     *
     * @return the type of objects this operation returns on invocation
     */
    public final TypeLiteral<T> getReturnType() {
        return typeLiteral;
    }

    /**
     * Returns the raw type of objects this operation returns on invocation.
     *
     * @return the raw type of objects this operation returns on invocation
     */
    public final Class<? super T> getReturnTypeRaw() {
        return type;
    }

    /**
     * Instantiates a new object using the specified parameters
     * 
     * @param params
     *            the parameters to use
     * @return the new instance
     */
    @Nullable
    public abstract T invoke(Object[] params);

    public FunctionHandle<T> withLookup(Lookup lookup) {
        throw new UnsupportedOperationException("This method is only supported by factories that were created from a field, constructor or method");
    }
}
