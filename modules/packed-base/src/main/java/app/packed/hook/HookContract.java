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
package app.packed.hook;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 *
 */
public final class HookContract {

    Set<Class<? extends Annotation>> capturingFieldHooks;

    public Set<Class<? extends Annotation>> exposedFieldHooks() {
        throw new UnsupportedOperationException();
    }

    public Set<Class<? extends Annotation>> capturingFieldHooks() {
        return capturingFieldHooks;
    }

    public final class Builder {}
    // captures
    // exposes

    // expose hooks, capture hooks

    // Another key feature is hooks.
}