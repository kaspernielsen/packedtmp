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

/**
 *
 */
// Ideen er egentlig at de her configurators ogsaa er artifact sources....

// Saa kan vi nemlig have <? extends ArtifactSource>

// Eneste lidt irriterende er at Vi har en Configurator der tager en configutaro?
@FunctionalInterface
public interface ArtifactConfigurator<T> extends ArtifactSource {

    /**
     * Configure the artifact
     * 
     * @param configurator
     *            the configurator used to configure the artifact
     */
    void configure(T configurator);
}
