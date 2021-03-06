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
package app.packed.lifecycle;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import app.packed.app.App;
import app.packed.artifact.ArtifactSource;
import app.packed.container.Wirelet;
import app.packed.inject.ServiceWirelets;

/**
 * Wirelets that can be used when creating an {@link App} instance. For example, via
 * {@link App#of(ArtifactSource, Wirelet...)} or {@link App#run(ArtifactSource, Wirelet...)}.
 */
// InvalidWireletApplicationException -> Thrown when trying to apply a wirelet in a situation where it cannot be used

// Do we allow them in Image????
// I don't see why not... So
//// Taenker ikke det er App. maaske Lifecycle wirelets....
// Isaer hvis App er en wrapper over en masse ting...
public final class LifecycleWirelets {

    /** No instantiation. */
    private LifecycleWirelets() {}

    /**
     * Creates a {@link StringArgs} from the specified arguments and returns a wirelet that provides it, via
     * {@link ServiceWirelets#provide(Object)}, to the wired container.
     * 
     * @param args
     *            the arguments to inject
     * @return a wirelet that provides the specified arguments to the linked container
     */
    static Wirelet args(String... args) {
        return ServiceWirelets.provide(StringArgs.of(args));
    }

    /**
     * Sets a maximum time for the container to run. When the deadline podpodf the app is shutdown.
     * 
     * @param timeout
     *            the timeout
     * @param unit
     *            the time unit
     * @return this option object
     */
    // These can only be used with a TopContainer with lifecycle...
    // Container will be shutdown normally after the specified timeout

    // Vi vil gerne have en version, vi kan refreshe ogsaa???
    // Maaske vi bare ikke skal supportered det direkte.

    // Teknisk set er det vel en app wirelet
    // Det er vel en der kan bruges hver gang vi har en dynamisk wiring. Og ikke kun apps...
    // Men, f.eks. ogsaa actors...

    public static Wirelet timeToLive(long timeout, TimeUnit unit) {
        // Shuts down container normally
        throw new UnsupportedOperationException();
    }

    public static Wirelet timeToLive(long timeout, TimeUnit unit, Supplier<Throwable> supplier) {
        timeToLive(10, TimeUnit.SECONDS, () -> new CancellationException());
        // Alternativ, kan man wrappe dem i f.eks. WiringOperation.requireExecutionMode();
        return new Wirelet() {

            // @Override
            // protected void process(BundleLink link) {
            // link.mode().checkExecute();
            // }
        };
    }

}
