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
package packed.internal.inject.factory;

import java.util.List;

import app.packed.inject.TypeLiteral;
import app.packed.util.Nullable;

/**
 *
 */

// Taenker vi extender InternalFactoryOfExecutable. I foerste omgang har vi kun
public class InternalFactoryBindable<T> extends InternalFactory<T> {

    public InternalFactoryBindable(TypeLiteral<T> typeLiteral) {
        super(typeLiteral, List.of());
    }

    /** {@inheritDoc} */
    @Override
    public Class<T> getLowerBound() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public T instantiate(Object[] params) {
        return null;
    }

}