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
package packed.internal.inject.buildtime;

import app.packed.bundle.Bundle;
import app.packed.bundle.BundleDescriptor;
import packed.internal.config.site.ConfigurationSiteType;
import packed.internal.config.site.InternalConfigurationSite;
import packed.internal.inject.ServiceNode;

/**
 *
 */
public class InternalBundleDescriptor {

    public static BundleDescriptor.Builder of(Bundle bundle) {
        InternalConfigurationSite ics = InternalConfigurationSite.ofStack(ConfigurationSiteType.BUNDLE_DESCRIPTOR_OF);
        ContainerBuilder conf = new ContainerBuilder(ics, bundle);

        bundle.doConfigure(conf);

        // BundleSupport.invoke().configureInjectorBundle((InjectorBundle) bundle, conf, false);

        DependencyGraph injectorBuilder = new DependencyGraph(conf);
        injectorBuilder.analyze(conf);

        //////////////// Create the builder
        BundleDescriptor.Builder builder = new BundleDescriptor.Builder(bundle.getClass());
        builder.setBundleDescription(conf.getDescription());// Nahh, this is the runtime description

        for (ServiceNode<?> n : conf.box.services().nodes) {
            if (n instanceof BuildtimeServiceNode) {
                builder.addServiceDescriptor(((BuildtimeServiceNode<?>) n).toDescriptor());
            }
        }

        for (BuildtimeServiceNode<?> n : conf.publicNodeList) {
            if (n instanceof BuildtimeServiceNodeExported) {
                builder.contract().services().addProvides(n.getKey());
            }
        }

        conf.box.buildContract(builder.contract());
        return builder;
    }
}