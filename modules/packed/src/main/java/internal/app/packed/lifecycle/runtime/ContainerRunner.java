/*
 * Copyright (c) 2026 Kasper Nielsen.
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
package internal.app.packed.lifecycle.runtime;

import java.util.ArrayList;

import app.packed.lifecycle.RunState;
import org.jspecify.annotations.Nullable;
import internal.app.packed.application.ApplicationSetup;
import internal.app.packed.container.ContainerSetup;
import internal.app.packed.extension.ExtensionContext;
import internal.app.packed.lifecycle.InvokableLifecycleOperationHandle;
import internal.app.packed.lifecycle.LifecycleOperationHandle.StartOperationHandle;
import internal.app.packed.lifecycle.LifecycleOperationHandle.StopOperationHandle;

/**
 *
 */
public class ContainerRunner {

    final ContainerSetup container;

    /** The runtime component node we are building. */
    private ExtensionContext pool;

    /** If the application is stateful, the applications runtime. */
    @Nullable
    public final RegionalManagedLifetime runtime;

    public ContainerRunner(ApplicationSetup application) {
        this.runtime = new RegionalManagedLifetime(this);
        this.container = application.rootContainer();
        // application.goal.isLaunchable() && application.driver.lifetimeKind() == OldLifetimeKind.MANAGED ? new
        // PackedManagedLifetime(this) : null;
    }

    /**
     * @return
     */
    public ExtensionContext pool() {
        return pool;
    }

    public void run(RunState state) {
        this.pool = container.lifetime.newRuntimePool();
        runtime.launch(state);
    }

    void shutdown() {
        // Stop operations that run before dependency order are followed by those that run in dependency order
        ArrayList<InvokableLifecycleOperationHandle<StopOperationHandle>> stoppers = new ArrayList<>(container.lifetime.stoppersPre);
        stoppers.addAll(container.lifetime.stoppersPost);
        new StopRunner(stoppers, pool, runtime).start();
    }

    void start() {
        // Start operations in dependency order are followed by those that run in reverse order
        ArrayList<InvokableLifecycleOperationHandle<StartOperationHandle>> starters = new ArrayList<>(container.lifetime.startersPre);
        starters.addAll(container.lifetime.startersPost);
        new StartRunner(starters, pool, runtime).start();
    }
}
