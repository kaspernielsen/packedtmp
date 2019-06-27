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
package zets.name;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import app.packed.container.AnyBundle;
import app.packed.container.ContainerSource;
import app.packed.container.Wirelet;
import zets.name.spi.AbstractBaseTest;
import zets.name.spi.ContainerConfigurationTester;

/** Tests that a proper name is generated if the name of a container is not explicitly set. */
public class NameDefaultsTest extends AbstractBaseTest {

    /** Tests the */
    @Test
    public void bundles() {
        check(f -> new AbstractTesterBundle(f) {}, "Container"); // Anonymous class
        check(f -> new S(f), "S");
        check(f -> new Bundle(f), "Bundle");
        check(f -> new HelloWorld(f), "HelloWorld");
        check(f -> new HelloWorldBundle(f), "HelloWorld");
    }

    private static void check(Function<Consumer<? super ContainerConfigurationTester>, ContainerSource> cs, String defaultName) {
        appOf(cs.apply(c -> {})).nameIs(defaultName);
        appOf(cs.apply(c -> {})).nameIs(defaultName);
        // We can override default name
        appOf(cs.apply(c -> c.getNameIs("Boo")), Wirelet.name("Boo")).nameIs("Boo");

        // Images
        imageOf(cs.apply(c -> {})).nameIs(defaultName);
        imageOf(cs.apply(c -> {})).nameIs(defaultName);
        imageOf(cs.apply(c -> {})).newApp().nameIs(defaultName);

        // We can override default name from images
        imageOf(cs.apply(c -> c.getNameIs("Boo")), Wirelet.name("Boo")).nameIs("Boo");
        imageOf(cs.apply(c -> c.getNameIs("Boo")), Wirelet.name("Boo")).newApp().nameIs("Boo");

        // As a child
        appOf(new AbstractTesterBundle(c -> {
            c.link((AnyBundle) cs.apply(cc -> {
                cc.pathIs("/" + defaultName);
            }));
        }) {}).nameIs("Container");

        // As multiple children
        appOf(new AbstractTesterBundle(c -> {
            c.link((AnyBundle) cs.apply(cc -> {
                cc.pathIs("/" + defaultName);
            }));
            c.link((AnyBundle) cs.apply(cc -> {
                cc.pathIs("/" + defaultName + "1");
            }));
        }) {}).nameIs("Container");

        // As two level nested
        appOf(new AbstractTesterBundle(c -> {
            c.link((AnyBundle) cs.apply(cc -> {
                cc.link((AnyBundle) cs.apply(ccc -> {
                    ccc.pathIs("/" + defaultName + "/" + defaultName);
                }));
            }));
        }) {}).nameIs("Container");

        // As 3 level nested
        appOf(new AbstractTesterBundle(c -> {
            c.link((AnyBundle) cs.apply(cc -> {
                cc.link((AnyBundle) cs.apply(ccc -> {
                    ccc.link((AnyBundle) cs.apply(cccc -> {
                        cccc.pathIs("/" + defaultName + "/" + defaultName + "/" + defaultName);
                    }));
                }));
            }));
        }) {}).nameIs("Container");
    }

    /** We normally remove the suffix 'Bundle', so make sure Bundle works */
    private class Bundle extends AbstractTesterBundle {
        Bundle(Consumer<? super ContainerConfigurationTester> ca) {
            super(ca);
        }
    }

    private class HelloWorld extends AbstractTesterBundle {
        HelloWorld(Consumer<? super ContainerConfigurationTester> ca) {
            super(ca);
        }
    }

    private class HelloWorldBundle extends AbstractTesterBundle {
        HelloWorldBundle(Consumer<? super ContainerConfigurationTester> ca) {
            super(ca);
        }
    }

    private class S extends AbstractTesterBundle {
        S(Consumer<? super ContainerConfigurationTester> ca) {
            super(ca);
        }
    }
}