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
package tck.injectorconfigurator.of;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.invoke.MethodHandles;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.Test;

import app.packed.inject.Injector;
import app.packed.util.Key;
import support.stubs.Letters.A;
import support.stubs.Letters.B;
import support.stubs.annotation.Left;
import support.stubs.annotation.Right;

/** Test {@link Injector#get(Class)} and {@link Injector#get(Key)}. */
public class InjectorWithTest {

    @Test
    public void get() {
        Injector i = Injector.configure(c -> {
            c.lookup(MethodHandles.lookup());
            c.provide(A.class);
            c.provide(A.class).as(new Key<@Left A>() {});
        });

        assertThat(i.use(A.class)).isInstanceOf(A.class);
        assertThat(i.use(new Key<A>() {})).isInstanceOf(A.class);
        assertThat(i.use(new Key<A>() {})).isSameAs(i.get(A.class).get());

        assertThat(i.use(new Key<@Left A>() {})).isInstanceOf(A.class);

        assertThat(i.use(new Key<A>() {})).isNotSameAs(i.use(new Key<@Left A>() {}));

        AbstractThrowableAssert<?, ? extends Throwable> a = assertThatThrownBy(() -> i.use(B.class));
        a.isExactlyInstanceOf(UnsupportedOperationException.class).hasNoCause();

        a = assertThatThrownBy(() -> i.use(new Key<@Left B>() {}));
        a.isExactlyInstanceOf(UnsupportedOperationException.class).hasNoCause();

        a = assertThatThrownBy(() -> i.use(new Key<@Right A>() {}));
        a.isExactlyInstanceOf(UnsupportedOperationException.class).hasNoCause();
    }
}
