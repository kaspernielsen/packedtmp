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
package tck.wirelets;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import app.packed.container.Wirelet;
import internal.app.packed.container.wirelets.CompositeWirelet;

/** Tests {@link Wirelet#combine(Wirelet...)}. */
public class WireletCombineTest {

    @Test
    public void nestedCombineIsFlattened() {
        Wirelet a = Wirelet.named("a");
        Wirelet b = Wirelet.named("b");
        Wirelet c = Wirelet.named("c");
        Wirelet d = Wirelet.named("d");

        Wirelet cd = Wirelet.combine(c, d);

        // composite first
        assertThat(((CompositeWirelet) Wirelet.combine(cd, a, b)).wirelets).containsExactly(c, d, a, b);
        // composite in the middle
        assertThat(((CompositeWirelet) Wirelet.combine(a, cd, b)).wirelets).containsExactly(a, c, d, b);
        // composite last
        assertThat(((CompositeWirelet) Wirelet.combine(a, b, cd)).wirelets).containsExactly(a, b, c, d);
        // two composites
        assertThat(((CompositeWirelet) Wirelet.combine(cd, Wirelet.combine(a, b), cd)).wirelets).containsExactly(c, d, a, b, c, d);
    }
}
