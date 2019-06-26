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
package packed.internal.container;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import app.packed.container.AnyBundle;
import app.packed.container.ContainerImage;
import app.packed.container.ContainerSource;
import app.packed.container.Wirelet;
import app.packed.container.WireletList;
import app.packed.inject.Injector;
import packed.internal.inject.AbstractInjector;

/** The default implementation of {@link ContainerImage}. */
public class DefaultContainerImage implements ContainerImage {

    final DefaultContainerConfiguration dcc;

    DefaultContainerImage(DefaultContainerConfiguration dcc) {
        this.dcc = requireNonNull(dcc);
    }

    /** {@inheritDoc} */
    @Override
    public String name() {
        return dcc.getName();
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends AnyBundle> source() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public ContainerImage with(Wirelet... wirelets) {
        // We need to check that they can be used at image instantion time.
        throw new UnsupportedOperationException();
    }

    public DefaultApp newApp(Wirelet... wirelets) {
        WireletList.of(wirelets);
        return new DefaultApp(dcc.buildFromImage());
    }

    public AbstractInjector newInjector(Wirelet... wirelets) {
        throw new UnsupportedOperationException();
    }
}

class DefaultContainerImageExpe {

    // Ideen er lidt at vi kan lade vaere at lave bundled foerend det skal bruges...
    //// Men ved ikke lige hvor meget vi sparer
    static DefaultContainerImageExpe of(Supplier<ContainerSource> source, Wirelet... wirelets) {
        throw new UnsupportedOperationException();
    }

    interface InjectorFactory {
        // Tager disse to objekter, laver en injector fra bundlen.
        // Og outputter String
        String spawn(long str1, int str2);

        Injector spawn(String httpRequest, String httpResponse);
    }

    interface UserDefinedSpawner {
        // App spawn(Host h, String httpRequest, String httpResponse);
    }
}
// Skal vi have forskellige injectorBuiler og AppBuilder???
// Syntes jeg egentlig ikke. Det kunne vaere fedt hvis man kunne bruge det overalt.
// F.eks. BundleContract.of(ImageBundle)
// Saa kan vi jo evt. registrerer om man kan lave en Injector eller App ud af den...

// Questions
// ? Wirelets: Yes, No
// ? Different Environments???
// Ideen er du kan bygge et image... Og saa blive ved med at instantiere det
// Alt er ligesom paa plads... Det er kun dine input parameter der er anderledes.....
// I love it.....

// Minder ufattelig meget om et bundle. Men har ingen mutable operations....
// Bundle -> Container Image -> Container|BundleDescriptor|NativeImage