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
package zets.name.spi;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import app.packed.artifact.ArtifactSource;
import app.packed.container.Bundle;
import app.packed.container.Wirelet;

/** An abstract test for testing artifacts. */
public abstract class AbstractArtifactTest {

    /** A bundle with no operations. */
    public static final Bundle EMPTY_BUNDLE = new Bundle() {};

    public static AppTester appOf(ArtifactSource source, Wirelet... wirelets) {
        return new AppTester(source, wirelets);
    }

    public static AppTester appOf(Consumer<? super ContainerConfigurationTester> source, Wirelet... wirelets) {
        return new AppTester(new AbstractConsumableBundle(source) {}, wirelets);
    }

    public static ContainerImageTester imageOf(ArtifactSource source, Wirelet... wirelets) {
        return new ContainerImageTester(source, wirelets);
    }

    protected static abstract class AbstractConsumableBundle extends Bundle {
        final Consumer<? super ContainerConfigurationTester> ca;

        protected AbstractConsumableBundle(Consumer<? super ContainerConfigurationTester> ca) {
            this.ca = requireNonNull(ca);
        }

        @Override
        public void configure() {
            ca.accept(new ContainerConfigurationTester(configuration()));
        }
    }
}
