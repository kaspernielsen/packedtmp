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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import app.packed.bundle.Bundle;
import app.packed.bundle.WiringOption;
import app.packed.inject.InjectionException;
import app.packed.util.Key;
import app.packed.util.Nullable;
import packed.internal.annotations.AtProvides;
import packed.internal.bundle.AppPackedBundleSupport;
import packed.internal.classscan.ImportExportDescriptor;
import packed.internal.config.site.InternalConfigurationSite;
import packed.internal.inject.InternalDependencyDescriptor;
import packed.internal.inject.ServiceNode;
import packed.internal.inject.ServiceWiringImportOperation;

/**
 * An abstract class for the injector bind methods
 */
class BindInjectorFromBundle {

    @Nullable
    final Bundle bundle;

    /** The configuration site of binding. */
    final InternalConfigurationSite configurationSite;

    /** The configuration of the injector that binding another bundle or injector. */
    final ContainerBuilder injectorConfiguration;

    /** The wiring operations. */
    final List<WiringOption> operations;

    final ContainerBuilder newConfiguration;

    BindInjectorFromBundle(ContainerBuilder injectorConfiguration, InternalConfigurationSite configurationSite, Bundle bundle, List<WiringOption> stages) {
        this.injectorConfiguration = requireNonNull(injectorConfiguration);
        this.configurationSite = requireNonNull(configurationSite);
        this.operations = requireNonNull(stages);
        this.bundle = bundle;
        this.newConfiguration = new ContainerBuilder(configurationSite, bundle);
    }

    /**
     * 
     */
    void processImport() {
        bundle.doConfigure(newConfiguration);
        processImport(newConfiguration.publicNodeList);
    }

    void processExport() {
        for (WiringOption s : operations) {
            if (s instanceof WiringOption) {
                throw new UnsupportedOperationException();
            }
        }
        List<BuildtimeServiceNodeImport<?>> exports = new ArrayList<>();
        if (newConfiguration.box.services().required != null) {
            for (Key<?> k : newConfiguration.box.services().required) {
                if (newConfiguration.box.services().nodes.containsKey(k)) {
                    throw new RuntimeException("OOPS already there " + k);
                }
                ServiceNode<?> node = injectorConfiguration.box.services().nodes.getRecursive(k);
                if (node == null) {
                    throw new RuntimeException("OOPS " + k);
                }
                BuildtimeServiceNodeImport<?> e = new BuildtimeServiceNodeImport<>(newConfiguration, configurationSite.replaceParent(node.configurationSite()), this,
                        node);
                exports.add(e);
                newConfiguration.box.services().nodes.put(e);
            }
        }
    }

    /**
     * @param importableNodes
     *            all nodes that are available for import from bound injector or bundle
     */
    void processImport(List<? extends ServiceNode<?>> importableNodes) {
        HashMap<Key<?>, BuildtimeServiceNodeImport<?>> nodes = new HashMap<>();
        for (ServiceNode<?> node : importableNodes) {
            if (!node.isPrivate()) {
                nodes.put(node.key(),
                        new BuildtimeServiceNodeImport<>(injectorConfiguration, configurationSite.replaceParent(node.configurationSite()), this, node));
            }
        }
        // Process each stage
        for (WiringOption operation : operations) {
            if (operation instanceof WiringOption) {
                AppPackedBundleSupport.invoke().startWireOperation(operation);
                nodes = processImportStage(operation, nodes);
                AppPackedBundleSupport.invoke().finishWireOperation(operation);
            }
        }

        // Add all to the private node map
        for (BuildtimeServiceNodeImport<?> node : nodes.values()) {
            if (!injectorConfiguration.box.services().nodes.putIfAbsent(node)) {
                throw new InjectionException("oops for " + node.key()); // Tried to import a service with a key that was already present
            }
        }
    }

    private HashMap<Key<?>, BuildtimeServiceNodeImport<?>> processImportStage(WiringOption stage, HashMap<Key<?>, BuildtimeServiceNodeImport<?>> nodes) {
        ImportExportDescriptor ied = ImportExportDescriptor.from(AppPackedBundleSupport.invoke().lookupFromWireOperation(stage), stage.getClass());

        for (AtProvides m : ied.provides.members.values()) {
            for (InternalDependencyDescriptor s : m.dependencies) {
                if (!nodes.containsKey(s.key())) {
                    throw new InjectionException("not good man, " + s.key() + " is not in the set of incoming services");
                }
            }
        }

        // Make runtime nodes....

        HashMap<Key<?>, BuildtimeServiceNodeImport<?>> newNodes = new HashMap<>();

        for (Iterator<BuildtimeServiceNodeImport<?>> iterator = nodes.values().iterator(); iterator.hasNext();) {
            BuildtimeServiceNodeImport<?> node = iterator.next();
            Key<?> existing = node.key();

            // invoke the import function on the stage
            if (stage instanceof ServiceWiringImportOperation) {
                ((ServiceWiringImportOperation) stage).onEachService(node);
            }

            if (node.key() == null) {
                iterator.remove();
            } else if (!node.key().equals(existing)) {
                iterator.remove();
                // TODO check if a node is already present
                newNodes.put(node.key(), node); // Should make new, with new configuration site
            }
        }
        // Put all remaining nodes in newNodes;
        newNodes.putAll(nodes);
        return newNodes;
    }

}