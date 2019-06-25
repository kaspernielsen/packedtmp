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
package packed.internal.componentcache.exam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import app.packed.component.ComponentConfiguration;
import app.packed.container.ExtensionHookGroup;
import app.packed.util.MethodDescriptor;
import packed.internal.componentcache.exam.Dddd.MyExtension;
import packed.internal.componentcache.exam.Dddd.OnX;

/**
 *
 */
public class OnXConfigurator extends ExtensionHookGroup<MyExtension, OnXConfigurator.Builder> {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        onAnnotatedMethodDescription(OnX.class, (a, b) -> {
            a.list.add(b);
        });
        // MethodBuilder

        // if has Context argument
        //// Function<Context> ->

        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public Builder newBuilder(Class<?> componentType) {
        return new Builder();
    }

    static class Builder implements Supplier<BiConsumer<ComponentConfiguration, MyExtension>> {

        List<MethodDescriptor> list = new ArrayList<>();

        /** {@inheritDoc} */
        @Override
        public BiConsumer<ComponentConfiguration, MyExtension> get() {
            List<MethodDescriptor> l = List.copyOf(list);
            return (c, e) -> e.methods(c, l);
        }
    }

}