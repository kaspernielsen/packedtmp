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

/**
 *
 */
public class AtMainGroup {

    final AtDependable main;

    /**
     * Creates a new provides group
     * 
     * @param builder
     *            the builder to create the group for
     */
    private AtMainGroup(Builder builder) {
        this.main = builder.main;
    }

    /** A builder for an {@link AtMainGroup}. */
    public final static class Builder {
        AtDependable main;
    }
}