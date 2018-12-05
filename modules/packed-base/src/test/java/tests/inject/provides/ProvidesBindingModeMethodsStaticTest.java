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
package tests.inject.provides;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.invoke.MethodHandles;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import app.packed.inject.BindingMode;
import app.packed.inject.Factory;
import app.packed.inject.Injector;
import app.packed.inject.InjectorConfiguration;
import app.packed.inject.Provides;
import app.packed.inject.TypeLiteral;

/** Tests {@link Provides#bindingMode()} on static methods. */
public class ProvidesBindingModeMethodsStaticTest {

    /** Tests lazy {@link Provides#bindingMode()} on static methods. */
    @Test
    public void bindLazy() {
        MixedMethodsNoInstantiation.test(c -> c.bindLazy(MixedMethodsNoInstantiation.class));
        MixedMethodsNoInstantiation.test(c -> c.bindLazy(Factory.findInjectable(MixedMethodsNoInstantiation.class)));
        MixedMethodsNoInstantiation.test(c -> c.bindLazy(new TypeLiteral<MixedMethodsNoInstantiation>() {}));
    }

    /** Tests prototype {@link Provides#bindingMode()} on static methods. */
    @Test
    public void bindPrototype() {
        MixedMethodsNoInstantiation.test(c -> c.bindPrototype(MixedMethodsNoInstantiation.class));
        MixedMethodsNoInstantiation.test(c -> c.bindPrototype(Factory.findInjectable(MixedMethodsNoInstantiation.class)));
        MixedMethodsNoInstantiation.test(c -> c.bindPrototype(new TypeLiteral<MixedMethodsNoInstantiation>() {}));
    }

    /** Tests default {@link Provides#bindingMode()} on static methods. */
    @Test
    public void bindSingleton() {
        MixedMethodsInstantiable.test(c -> c.bind(new MixedMethodsInstantiable()));
        MixedMethodsInstantiable.test(c -> c.bind(MixedMethodsInstantiable.class));
        MixedMethodsInstantiable.test(c -> c.bind(Factory.findInjectable(MixedMethodsInstantiable.class)));
        MixedMethodsInstantiable.test(c -> c.bind(new TypeLiteral<MixedMethodsInstantiable>() {}));
    }

    /** A helper class that can be instantiated. */
    static class MixedMethodsInstantiable {

        private static Long L;

        private static Integer P;

        private static Short S;

        MixedMethodsInstantiable() {
            assertThat(L).isEqualByComparingTo(1L);
            assertThat(P).isEqualByComparingTo(1);
            assertThat(S).isEqualByComparingTo((short) 1);
        }

        @Provides(bindingMode = BindingMode.LAZY)
        static Long l() {
            return L;
        }

        @Provides(bindingMode = BindingMode.PROTOTYPE)
        static Integer p() {
            return P;
        }

        @Provides(bindingMode = BindingMode.SINGLETON)
        static Short s() {
            return S;
        }

        static void test(Consumer<? super InjectorConfiguration> configurator) {
            L = 1L;
            P = 1;
            S = 1;
            Injector i = Injector.of(c -> {
                c.lookup(MethodHandles.lookup());
                configurator.accept(c);
            });
            assertThat(i.with(MixedMethodsInstantiable.class)).isNotNull();
            L = 2L;
            S = 2;
            P = 2;

            assertThat(i.with(Short.class)).isEqualTo((short) 1);
            assertThat(i.with(Long.class)).isEqualTo(2L);
            assertThat(i.with(Integer.class)).isEqualTo(2);
            L = 3L;
            S = 3;
            P = 3;
            assertThat(i.with(Short.class)).isEqualTo((short) 1);
            assertThat(i.with(Long.class)).isEqualTo(2L);
            assertThat(i.with(Integer.class)).isEqualTo(3);
        }
    }

    /**
     * A helper class that should never be instantiated. Because we can read the value of the fields without an instance of
     * BindStaticNoInstantiation.
     */
    static class MixedMethodsNoInstantiation {

        private static Long L;

        private static Integer P;

        private static Short S;

        public MixedMethodsNoInstantiation() {
            throw new AssertionError("Cannot instantiate");
        }

        @Provides(bindingMode = BindingMode.LAZY)
        static Long l() {
            return L;
        }

        @Provides(bindingMode = BindingMode.PROTOTYPE)
        static Integer p() {
            return P;
        }

        @Provides(bindingMode = BindingMode.SINGLETON)
        static Short s() {
            return S;
        }

        static void test(Consumer<? super InjectorConfiguration> configurator) {
            L = 1L;
            P = 1;
            S = 1;
            Injector i = Injector.of(c -> {
                c.lookup(MethodHandles.lookup());
                configurator.accept(c);
            });

            L = 2L;
            S = 2;
            P = 2;

            assertThat(i.with(Short.class)).isEqualTo((short) 1);
            assertThat(i.with(Long.class)).isEqualTo(2L);
            assertThat(i.with(Integer.class)).isEqualTo(2);
            L = 3L;
            S = 3;
            P = 3;
            assertThat(i.with(Short.class)).isEqualTo((short) 1);
            assertThat(i.with(Long.class)).isEqualTo(2L);
            assertThat(i.with(Integer.class)).isEqualTo(3);
        }
    }
}