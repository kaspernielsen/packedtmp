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
package packed.internal.invokable;

import static java.util.Objects.requireNonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;

import app.packed.inject.Factory;
import app.packed.inject.InjectionException;
import app.packed.util.ConstructorDescriptor;
import app.packed.util.IllegalAccessRuntimeException;
import app.packed.util.Nullable;
import app.packed.util.TypeLiteral;
import packed.internal.util.ThrowableUtil;

/** The backing class of {@link Factory}. */
public final class ConstructorOperation<T> extends InternalFunction<T> {

    /** A factory with an executable as a target. */
    private final ConstructorDescriptor<T> descriptor;

    /**
     * A method handle that can be used for invoking the constructor, is initially null, for example, for
     * {@link Factory#findInjectable(Class)}.
     */
    @Nullable
    private final MethodHandle methodHandle;

    public ConstructorOperation(TypeLiteral<T> key, ConstructorDescriptor<T> descriptor, MethodHandle methodHandle) {
        super(key);
        this.descriptor = requireNonNull(descriptor, "constructor is null");
        this.methodHandle = methodHandle;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public T invoke(Object[] params) {
        requireNonNull(methodHandle, "internal error");
        try {
            return (T) methodHandle.invokeWithArguments(params);
        } catch (Throwable e) {
            ThrowableUtil.rethrowErrorOrRuntimeException(e);
            throw new InjectionException("Failed to inject constructor " + descriptor, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return descriptor.toString();
    }

    /**
     * Returns a new internal factory that uses the specified lookup object when invoking the constructor.
     * 
     * @param lookup
     *            the lookup object to use
     * @return a new internal factory that uses the specified lookup object
     * @throws IllegalAccessRuntimeException
     *             if the specified lookup object does not give access to the underlying constructor
     */
    @Override
    public InternalFunction<T> withLookup(Lookup lookup) {
        MethodHandle handle;
        try {
            handle = descriptor.unreflect(lookup);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessRuntimeException("No access to the constructor " + descriptor + " using the specified lookup", e);
        }
        return new ConstructorOperation<>(getReturnType(), descriptor, handle);
    }
}