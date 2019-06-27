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
package aexp.env;

/**
 *
 */
interface RuntimeEnvironment {

    /** The default runtime environment. */
    public static final RuntimeEnvironment DEFAULT = DefaultRuntimeEnvironment.DEFAULT;

    /**
     * Returns the name of the environment. This method is automatically implemented via {@link Enum#name()}.
     * 
     * @return the name of the environment
     * 
     * @see Enum#name()
     */
    String name();

    default RuntimeEnvironment defaultEnvironment() {
        return (RuntimeEnvironment) ((Enum<?>) this).getDeclaringClass().getEnumConstants()[0];
    }

    default boolean saveStackTraces() {
        return DEFAULT.saveStackTraces();
    }
    // Save StackTraces...
}

enum DefaultRuntimeEnvironment implements RuntimeEnvironment {
    DEFAULT;

    /** {@inheritDoc} */
    @Override
    public boolean saveStackTraces() {
        return true;
    }
}