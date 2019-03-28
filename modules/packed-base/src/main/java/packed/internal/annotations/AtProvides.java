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

import static java.util.Objects.requireNonNull;

import java.lang.invoke.MethodHandles.Lookup;
import java.util.List;

import app.packed.inject.InstantiationMode;
import app.packed.inject.Provides;
import app.packed.util.Key;
import app.packed.util.Nullable;
import packed.internal.inject.InternalDependency;
import packed.internal.util.descriptor.InternalFieldDescriptor;
import packed.internal.util.descriptor.InternalMemberDescriptor;
import packed.internal.util.descriptor.InternalMethodDescriptor;

/** A descriptor for a member annotated with {@link Provides}. */
public final class AtProvides extends AtDependable {

    /** An (optional) description from {@link Provides#description()}. */
    @Nullable
    public final String description;

    /** The instantiation mode from {@link Provides#instantionMode()}. */
    public final InstantiationMode instantionMode;

    /** Whether or not the member on which the annotation is present is a static member. */
    public final boolean isStaticMember;

    /** The key under which the provided service will be made available. */
    public final Key<?> key;

    /** The annotated member, either an {@link InternalFieldDescriptor} or an {@link InternalMethodDescriptor}. */
    public final InternalMemberDescriptor member;

    AtProvides(Lookup lookup, InternalMemberDescriptor member, Key<?> key, Provides provides, List<InternalDependency> dependencies) {
        super(member.newInvoker(lookup), dependencies);
        this.description = provides.description().length() > 0 ? provides.description() : null;
        this.member = requireNonNull(member);
        this.instantionMode = provides.instantionMode();
        this.isStaticMember = member.isStatic();
        this.key = requireNonNull(key);
    }
}