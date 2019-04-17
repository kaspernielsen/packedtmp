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
package packed.internal.box;

/** The type of box. */

// Spoersmaalet er om vi kun skal have en, og om den skal hedde box type....
// F.eks. Hvis vi vi laver en descriptor for et container trae.
// Saa wire vi jo stadig child containers.

// Vi maa have en AppContext.... og en AppType....
public enum BoxType {

    /** A box targeting a Injector created from a bundle. */
    INJECTOR_VIA_BUNDLE,

    /** A box targeting a Injector created using a configurator. */
    INJECTOR_VIA_CONFIGURATOR;

    public boolean privateServices() {
        return this != INJECTOR_VIA_CONFIGURATOR;
    }

    public boolean unresolvedServicesAllowed() {
        return this == INJECTOR_VIA_BUNDLE;
    }

    // HOST
    // INJECTOR - via InjectorConfigurator
    // INJECTOR - via Bundle
    // INJECTOR - via existing injector
    // BundleContract - of
    // BundleDescriptor - of
    // App - via AppConfigurator
    // App - via Bundle

    // App - via existing app

    // En app kan godt have forskellige. F.eks. En Injector fra en InjectorConfigurator der wire en Injector fra en bundle
}