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
package support.stubs;

import java.lang.invoke.MethodHandles;

import org.junit.jupiter.api.Test;

import app.packed.bundle.Bundle;
import app.packed.bundle.BundleDescriptor;
import app.packed.inject.Injector;

/**
 *
 */
public class TestBundlex extends Bundle {

    @Test
    public void main() {
        BundleDescriptor bd = BundleDescriptor.of(TestBundlex.class);

        System.out.println(bd);

        Injector i = Injector.of(TestBundlex.class);
        i.services().forEach(e -> System.out.println(e));
    }

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        lookup(MethodHandles.lookup());
        bind(A.class);
        bind(this);
        expose(A.class);
    }

    public static class A {
        public A() {}
    }
}
