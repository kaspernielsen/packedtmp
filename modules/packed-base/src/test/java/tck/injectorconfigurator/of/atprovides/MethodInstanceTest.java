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
package tck.injectorconfigurator.of.atprovides;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.Test;

import app.packed.inject.Factory;
import app.packed.inject.Injector;
import app.packed.inject.InjectorConfigurator;
import app.packed.inject.InstantiationMode;
import app.packed.inject.Provides;
import app.packed.util.InvalidDeclarationException;
import app.packed.util.TypeLiteral;

/** Tests {@link Provides#instantionMode()}. */
public class MethodInstanceTest {

    /** Tests default {@link Provides#instantionMode()} on instance methods. */
    @Test
    public void provide() {
        MixedMethods.test(c -> c.provide(new MixedMethods()));
        MixedMethods.test(c -> c.provide(MixedMethods.class));
        MixedMethods.test(c -> c.provide(Factory.findInjectable(MixedMethods.class)));
        MixedMethods.test(c -> c.provide(new TypeLiteral<MixedMethods>() {}));
    }

    /** Tests lazy {@link Provides#instantionMode()} on instance methods. */
    @Test
    public void provideLazy() {
        MixedMethods.test(c -> c.provide(MixedMethods.class).lazy());
        MixedMethods.test(c -> c.provide(Factory.findInjectable(MixedMethods.class)).lazy());
        MixedMethods.test(c -> c.provide(new TypeLiteral<MixedMethods>() {}).lazy());

        // Correct support FOR LAZY->LAZY and LAZY->PROTOTYPE is not implemented yet.
        // As we instantiate the parent no matter what. We just dont test it here
    }

    /** Can never bind prototypes that have non-static provided fields. */
    @Test
    public void providePrototype() {
        AbstractThrowableAssert<?, ?> a = assertThatThrownBy(() -> Injector.of(c -> {
            c.lookup(MethodHandles.lookup());
            c.provide(new AtomicBoolean());
            c.provide(SingletonMethod.class).prototype();
        }));
        a.isExactlyInstanceOf(InvalidDeclarationException.class).hasNoCause();
        // TODO check message

        a = assertThatThrownBy(() -> Injector.of(c -> {
            c.lookup(MethodHandles.lookup());
            c.provide(new AtomicBoolean());
            c.provide(LazyMethod.class).prototype();
        }));
        a.isExactlyInstanceOf(InvalidDeclarationException.class).hasNoCause();
        // TODO check message

        a = assertThatThrownBy(() -> Injector.of(c -> {
            c.lookup(MethodHandles.lookup());
            c.provide(new AtomicBoolean());
            c.provide(PrototypeMethod.class).prototype();
        }));
        a.isExactlyInstanceOf(InvalidDeclarationException.class).hasNoCause();
        // TODO check message
    }

    static class LazyMethod {

        Short s = 1;

        LazyMethod(AtomicBoolean b) {
            b.set(true);
        }

        @Provides(instantionMode = InstantiationMode.LAZY)
        public Short s() {
            return s;
        }
    }

    static class MixedMethods {

        Long l = 1L;

        Integer p = 1;

        Short s = 1;

        @Provides(instantionMode = InstantiationMode.LAZY)
        Long l() {
            return l;
        }

        @Provides(instantionMode = InstantiationMode.PROTOTYPE)
        Integer p() {
            return p;
        }

        @Provides(instantionMode = InstantiationMode.SINGLETON)
        Short s() {
            return s;
        }

        static void test(Consumer<? super InjectorConfigurator> configurator) {
            Injector i = Injector.of(c -> {
                c.lookup(MethodHandles.lookup());
                configurator.accept(c);
            });
            MixedMethods f = i.with(MixedMethods.class);
            f.l = 2L;
            f.s = 2;
            f.p = 2;

            assertThat(i.with(Short.class)).isEqualTo((short) 1);
            assertThat(i.with(Long.class)).isEqualTo(2L);
            assertThat(i.with(Integer.class)).isEqualTo(2);
            f.l = 3L;
            f.s = 3;
            f.p = 3;
            assertThat(i.with(Short.class)).isEqualTo((short) 1);
            assertThat(i.with(Long.class)).isEqualTo(2L);
            assertThat(i.with(Integer.class)).isEqualTo(3);
        }
    }

    static class PrototypeMethod {

        Short s = 1;

        PrototypeMethod(AtomicBoolean b) {
            b.set(true);
        }

        @Provides(instantionMode = InstantiationMode.PROTOTYPE)
        public Short s() {
            return s;
        }
    }

    static class SingletonMethod {

        Short s = 1;

        SingletonMethod(AtomicBoolean b) {
            b.set(true);
        }

        @Provides(instantionMode = InstantiationMode.SINGLETON)
        public Short s() {
            return s;
        }
    }
}