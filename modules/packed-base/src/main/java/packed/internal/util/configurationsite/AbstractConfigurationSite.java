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
package packed.internal.util.configurationsite;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.util.Optional;

import app.packed.util.ConfigurationSite;
import app.packed.util.FieldDescriptor;
import app.packed.util.MethodDescriptor;

/**
 *
 */
public abstract class AbstractConfigurationSite implements InternalConfigurationSite {

    private final ConfigurationSiteType operation;

    private final ConfigurationSite parent;

    AbstractConfigurationSite(ConfigurationSite parent, ConfigurationSiteType operation) {
        this.parent = parent;
        this.operation = requireNonNull(operation);
    }

    /** {@inheritDoc} */
    @Override
    public String operation() {
        return operation.name();
    }

    @Override
    public Optional<ConfigurationSite> parent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public ConfigurationSite spawnOnAnnotatedField(ConfigurationSiteType operation, FieldDescriptor field, Annotation annotation) {
        return new AnnotatedFieldConfigurationSite(this, operation, field, annotation);
    }

    public ConfigurationSite spawnOnAnnotatedMethod(ConfigurationSiteType operation, MethodDescriptor method, Annotation annotation) {
        return new AnnotatedMethodConfigurationSite(this, operation, method, annotation);
    }

}