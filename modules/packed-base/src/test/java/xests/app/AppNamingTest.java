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
package xests.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import app.packed.app.App;
import app.packed.container.Bundle;
import app.packed.container.Wirelet;

/** Tests {@link App#name()}. */
public class AppNamingTest {

    /** Tests that the default name of an application is 'App'. */
    @Test
    public void basics() {
        // Tests that default name is 'App'
        assertThat(App.of(new Bundle() {}).name()).isEqualTo("App");

        /** Tests that we can set the name via an Wirelet. */
        assertThat(App.of(new Bundle() {}, Wirelet.name("Boo")).name()).isEqualTo("Boo");

        class SetNameToFooBundle extends Bundle {
            @Override
            public void configure() {
                assertThat(getName()).isNull();
                setName("Foo");
                assertThat(getName()).isEqualTo("Foo");
            }
        }

        // Tests that we can a default name via AnyBundle#setName()
        assertThat(App.of(new SetNameToFooBundle()).name()).isEqualTo("Foo");

        // Tests that we can override a set name with a wirelet
        assertThat(App.of(new SetNameToFooBundle() {}, Wirelet.name("Boo")).name()).isEqualTo("Boo");

        // Tests that override takes the last wirelet
        assertThat(App.of(new SetNameToFooBundle() {}, Wirelet.name("Boo"), Wirelet.name("Goo")).name()).isEqualTo("Goo");
    }

    public void testQuestionMarkNames() {

    }
}
