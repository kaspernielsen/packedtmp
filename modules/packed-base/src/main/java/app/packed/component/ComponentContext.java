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
package app.packed.component;

/**
 *
 */
public interface ComponentContext {
    // install(); <- installs child
    // spawn();
    // spawn(100);

    // Error handling top->down and then as a static bundle method as last resort.
    // The bundle XX.... defines a non-static error handler method. But it was never installed

    // S spawn();
    // CompletableFuture<S> spawnAsync();

    // NU ER VI TILBAGE MED EN COMPONENT KAN HAVE EN ROLLE... (eller flere???)

    // Bundle.setDefaultRole <- On Runtime.
    // F.eks. Actor .withRole(Actor)

    // Role -> Pool [5-25 instance, timeout 1 minute]

    // Install

    // setMaxInstances();

    // Role-> PrototypeOptionas. Its a prototype of

    // I think there are extra settings on prototype...
    // Such as caching...
}