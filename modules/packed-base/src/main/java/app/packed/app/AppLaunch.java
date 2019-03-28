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
package app.packed.app;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import app.packed.bundle.Bundle;
import app.packed.bundle.WiringOperation;

/**
 *
 */
// No Result...
// Can be run once..
// Could also
// Listener Manager????
// Can only be run once, unless

// Guest... Being added to a running App/Host/...
public class AppLaunch implements Runnable {

    public boolean isRepeatable() {
        return false;
        // Can we run it again, must a property on the bundle....
    }

    /**
     *
     * @throws UnsupportedOperationException
     *             if the bundle is not runnable (has at least one component). Maybe throw this from
     *             {@link #of(Bundle, WiringOperation...)}
     */
    @Override
    public void run() {

    }

    /**
     * Runs the application asynchronously. Equivalent to invoking {@code CompletableFuture.runAsync(app)}.
     * 
     * @return a new CompletableFuture that is asynchronously completed after application has run
     */
    public CompletableFuture<Void> runAsync() {
        return CompletableFuture.runAsync(this);
    }

    public CompletableFuture<Void> runAsync(Executor executor) {
        return CompletableFuture.runAsync(this, executor);
    }

    public AppLaunch setTimeToLive(long timeout, TimeUnit unit) {
        // Container will be shutdown normally after the specified timeout
        return this;
    }

    public AppLaunch setTimeToLive(long timeout, TimeUnit unit, Supplier<Throwable> supplier) {
        setTimeToLive(10, TimeUnit.SECONDS, () -> new CancellationException());
        return this; // Will be shutdown using this
    }
    // setTimeToLive(

    public static AppLaunch of(Bundle bundle, String[] args, WiringOperation... operations) {
        return null;
    }

    /**
     * Creates new application from the specified bundle and an optional array of wiring operations.
     * 
     * @param bundle
     *            the bundle to create an application for
     * @param operations
     *            an optional array of wiring operation
     * @return the new application
     */
    public static AppLaunch of(Bundle bundle, WiringOperation... operations) {
        return null;
    }
}