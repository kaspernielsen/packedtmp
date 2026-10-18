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
package tck.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;

import app.packed.application.App;
import app.packed.assembly.BaseAssembly;
import app.packed.lifecycle.Initialize;
import app.packed.lifecycle.Start;
import app.packed.lifecycle.Stop;

/** Tests that all lifecycle operations are invoked, regardless of natural ordering. */
public class LifecycleStopTest {

    static final List<String> EVENTS = new CopyOnWriteArrayList<>();

    public static class LifecycleBean {

        @Initialize
        public void init() {
            EVENTS.add("init");
        }

        @Initialize(naturalOrder = false)
        public void initReversed() {
            EVENTS.add("initReversed");
        }

        @Start
        public void start() {
            EVENTS.add("start");
        }

        @Start(naturalOrder = false)
        public void startReversed() {
            EVENTS.add("startReversed");
        }

        @Stop(naturalOrder = false)
        public void stopReversed() {
            EVENTS.add("stopReversed");
        }

        @Stop
        public void stop() {
            EVENTS.add("stop");
        }
    }

    @Test
    public void allLifecycleMethodsAreInvoked() {
        EVENTS.clear();
        App.run(new BaseAssembly() {
            @Override
            protected void build() {
                install(LifecycleBean.class);
            }
        });
        assertThat(EVENTS).containsExactly("init", "initReversed", "start", "startReversed", "stopReversed", "stop");
    }
}
