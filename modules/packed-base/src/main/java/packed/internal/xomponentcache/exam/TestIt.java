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
package packed.internal.xomponentcache.exam;

import app.packed.app.App;
import app.packed.container.Bundle;

/**
 *
 */
public class TestIt extends Bundle {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        install(new Dddd());
        link(new SomeOtherBundle());
        System.out.println(extensions());
    }

    public static void main(String[] args) {
        App a = App.of(new TestIt());
        System.out.println("------------------------------ " + a.name());
        App.of(new TestIt());
    }

    static class SomeOtherBundle extends Bundle {

    }
}