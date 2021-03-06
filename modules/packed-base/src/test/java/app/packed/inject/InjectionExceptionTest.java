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
package app.packed.inject;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import support.stubs.Throwables.Exception1;

/** Tests {@link InjectionException}. */
public class InjectionExceptionTest {

    /** Tests the various constructors.  */
    @Test
    public void test() {
        assertThat(new InjectionException("foo")).hasNoCause();
        assertThat(new InjectionException("foo")).hasMessage("foo");
        assertThat(new InjectionException("foobar", Exception1.INSTANCE)).hasCause(Exception1.INSTANCE);
        assertThat(new InjectionException("foobar", Exception1.INSTANCE)).hasMessage("foobar");
    }
}
