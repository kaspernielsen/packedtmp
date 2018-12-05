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
package packed.internal.inject.builder;

import app.packed.bundle.Bundle;
import app.packed.bundle.InjectorBundle;
import app.packed.inject.ServiceConfiguration;
import packed.internal.bundle.BundleDescriptorBuilder;
import packed.internal.bundle.BundleSupport;
import packed.internal.util.configurationsite.ConfigurationSiteType;
import packed.internal.util.configurationsite.InternalConfigurationSite;

/**
 *
 */
public class InternalBundleDescriptor {

    /**
     * @param bundle
     * @return
     */
    public static BundleDescriptorBuilder of(Bundle bundle) {
        InternalConfigurationSite ics = InternalConfigurationSite.ofStack(ConfigurationSiteType.BUNDLE_DESCRIPTOR_OF);
        InjectorBuilder conf = new InjectorBuilder(ics, bundle);

        BundleSupport.configure((InjectorBundle) bundle, conf, false);

        DependencyGraph injectorBuilder = new DependencyGraph(conf);
        injectorBuilder.analyze(conf);

        //////////////// Create the builder
        BundleDescriptorBuilder builder = new BundleDescriptorBuilder();
        for (AbstractBuildNode<?> n : conf.publicNodeList) {
            if (n instanceof BuildNodeExposed) {
                builder.addExposed((ServiceConfiguration<?>) n);
            }
        }
        if (conf.requiredServicesOptionally != null) {
            builder.addOptionalServices(conf.requiredServicesOptionally);
        }
        if (conf.requiredServicesMandatory != null) {
            builder.addRequiredServices(conf.requiredServicesMandatory);
        }
        return builder;
    }
}