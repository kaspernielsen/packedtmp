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
package app.packed.artifact;

import java.util.function.Supplier;

import app.packed.component.Component;
import app.packed.container.Bundle;

/**
 * An artifact source is used to create an artifact. Currently the following types of artifact sources are supported:
 * 
 * 
 * This is typically either a subclass of {@link Bundle} or a pregenerated {@link ArtifactImage container image}.
 * <p>
 * TODO maybe list all the s
 * <p>
 * This interface is not intended to be implemented outside of this module. Future versions of this class may make use
 * of sealed types if they become available.
 */
// ContainerFactory?? But this maybe implies that you can invoke it multiple times

// Properties
// Repeatable - Non-repeatable..
// Concurrent - Non-current (Bundles may be Repeatable but they will never be Concurrent)
// The only reason we want to allow repeatable bundles. Is So we can create a descriptor
// before we make

// Hmm ArtifactSource??? Only DynamicContainerSource is a bit of unknown.
// ArtifactSource type....
public interface ArtifactSource {

    static ArtifactSource ofRepeatableBundle(Supplier<? extends Bundle> supplier) {
        throw new UnsupportedOperationException();
    }

    // Ideen er egentlig at have en scanner af en slags..
    // Componenter for den pakke, med de annoteringer.. osv.
    // CacheResult = true <- Vi tillader som default ikke
    static ArtifactSource ofComponentSelector(String s) {
        throw new UnsupportedOperationException();
    }

    /**
     * Validates that the source can create a valid. Move it to App, Injector, ...
     * 
     * @param source
     */
    static void validate(ArtifactSource source) {}
}

// Not sure we can link to ContainerImages...
// So ContainerSource is maybe more like an AppSource

class LiveReload /* implements ContainerSource */ {
    // Bliver redeployet hver gang en fil aendrer sig....
    // Det bliver loaded i sit eget module layer...

    // LiveReload af en single App, men ikke hosten..
}

abstract class SoftLink implements Component {
    // I sidste ende er alt jo en bundle....

    // Ideen er egentlig at man kan live replace alle componenter....
    //// Det skal maaske ikke vaere en softlink....
}
