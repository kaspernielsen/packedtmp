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
package packed.internal.annotations;

import java.lang.invoke.MethodHandles.Lookup;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import app.packed.hook.AnnotatedFieldHook;
import app.packed.hook.AnnotatedMethodHook;
import app.packed.hook.OnHook;
import app.packed.inject.Provide;
import app.packed.util.InvalidDeclarationException;
import app.packed.util.Key;
import packed.internal.inject.util.InternalDependencyDescriptor;
import packed.internal.util.ErrorMessageBuilder;
import packed.internal.util.descriptor.InternalFieldDescriptor;
import packed.internal.util.descriptor.InternalMemberDescriptor;
import packed.internal.util.descriptor.InternalMethodDescriptor;

/**
 * Information about fields and methods annotated with {@link Provide}, typically on a single class. Used for both
 * services, components, import and export stages.
 */
public final class AtProvidesGroup {

    /** Whether or not there are any non-static providing fields or methods. */
    public final boolean hasInstanceMembers;

    /** An immutable map of all providing members. */
    public final Map<Key<?>, AtProvides> members;

    /**
     * Creates a new provides group
     * 
     * @param builder
     *            the builder to create the group for
     */
    private AtProvidesGroup(ProvidesHookAggregator builder) {
        this.members = builder.members == null ? Map.of() : Map.copyOf(builder.members);
        this.hasInstanceMembers = builder.hasInstanceMembers;
    }

    /** A builder for an {@link AtProvidesGroup}. */
    public final static class ProvidesHookAggregator implements Supplier<AtProvidesGroup> {

        /** Whether or not there are any non-static providing fields or methods. */
        private boolean hasInstanceMembers;

        /** A set of all keys for every provided service. */
        private final HashMap<Key<?>, AtProvides> members = new HashMap<>();

        /**
         * Creates a new group from this builder.
         * 
         * @return the new group
         */
        @Override
        public AtProvidesGroup get() {
            return new AtProvidesGroup(this);
        }

        @OnHook
        void onFieldProvide(AnnotatedFieldHook<Provide> amh) {
            tryAdd0(amh.lookup(), InternalFieldDescriptor.of(amh.field()), Key.fromField(amh.field().newField()), amh.annotation(), List.of());
        }

        @OnHook
        void onMethodProvide(AnnotatedMethodHook<Provide> amh) {
            InternalMethodDescriptor descriptor = (InternalMethodDescriptor) amh.method();
            tryAdd0(amh.lookup(), descriptor, Key.fromMethodReturnType(descriptor.newMethod()), amh.annotation(),
                    InternalDependencyDescriptor.fromExecutable(descriptor));
        }

        private AtProvides tryAdd0(Lookup lookup, InternalMemberDescriptor descriptor, Key<?> key, Provide provides,
                List<InternalDependencyDescriptor> dependencies) {
            AtProvides ap = new AtProvides(lookup, descriptor, key, provides, dependencies);
            hasInstanceMembers |= !ap.isStaticMember;

            // Check this
            // if (instantionMode != InstantiationMode.PROTOTYPE && hasDependencyOnInjectionSite) {
            // throw new InvalidDeclarationException("Cannot inject InjectionSite into singleton services");
            // }
            if (members.putIfAbsent(key, ap) != null) {
                throw new InvalidDeclarationException(ErrorMessageBuilder.of(descriptor.getDeclaringClass())
                        .cannot("have multiple members providing services with the same key (" + key.toStringSimple() + ").")
                        .toResolve("either remove @Provides on one of the members, or use a unique qualifier for each of the members"));
            }
            return ap;
        }
    }
}
