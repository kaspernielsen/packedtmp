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
package tck.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import app.packed.application.App;
import app.packed.assembly.BaseAssembly;
import app.packed.service.CircularDependencyException;

/** Tests that circular service dependencies are detected at build time. */
public class CircularDependencyTest {

    public static class A {
        public A(B b) {}
    }

    public static class B {
        public B(A a) {}
    }

    public static class Leaf {}

    public static class NeedsLeaf {
        public NeedsLeaf(Leaf leaf) {}
    }

    @Test
    public void cycleIsDetected() {
        assertThatThrownBy(() -> App.run(new BaseAssembly() {
            @Override
            protected void build() {
                install(A.class).provide();
                install(B.class).provide();
            }
        })).isInstanceOf(CircularDependencyException.class).hasMessageContaining("Circular dependencies between 2 services");
    }

    @Test
    public void noFalsePositive() {
        assertThatCode(() -> App.run(new BaseAssembly() {
            @Override
            protected void build() {
                install(Leaf.class).provide();
                install(NeedsLeaf.class).provide();
            }
        })).doesNotThrowAnyException();
    }
}
