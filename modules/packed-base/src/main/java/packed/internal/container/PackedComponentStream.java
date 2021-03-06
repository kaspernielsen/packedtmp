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
package packed.internal.container;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import app.packed.component.Component;
import app.packed.component.ComponentStream;
import packed.internal.util.AbstractDelegatingStream;

/** The default implementation of {@link ComponentStream}. */
final class PackedComponentStream extends AbstractDelegatingStream<Component> implements ComponentStream {

    /**
     * Creates a new internal component stream.
     *
     * @param stream
     *            the stream of all components that we wrap.
     */
    PackedComponentStream(Stream<Component> stream) {
        super(stream);
    }

    /** {@inheritDoc} */
    @Override
    public ComponentStream dropWhile(Predicate<? super Component> predicate) {
        return with(stream.dropWhile(predicate));
    }

    /** {@inheritDoc} */
    @Override
    public ComponentStream filter(Predicate<? super Component> predicate) {
        return with(stream.filter(predicate));
    }

    /** {@inheritDoc} */
    @Override
    public ComponentStream limit(long maxSize) {
        return with(stream.limit(maxSize));
    }

    /** {@inheritDoc} */
    @Override
    public ComponentStream peek(Consumer<? super Component> action) {
        return with(stream.peek(action));
    }

    /** {@inheritDoc} */
    @Override
    public ComponentStream skip(long n) {
        return with(stream.skip(n));
    }

    /** {@inheritDoc} */
    @Override
    public ComponentStream sorted(Comparator<? super Component> comparator) {
        return with(stream.sorted(comparator));
    }

    /** {@inheritDoc} */
    @Override
    public ComponentStream takeWhile(Predicate<? super Component> predicate) {
        return with(stream.takeWhile(predicate));
    }

    /** {@inheritDoc} */
    @Override
    protected PackedComponentStream with(Stream<Component> s) {
        return new PackedComponentStream(s);
    }
}

// @Override
// public Stream<Object> instances() {
// return map(e -> e.instance());
// }
//
// @SuppressWarnings("unchecked")
// @Override
// public <T> Stream<T> instancesOfType(Class<T> type) {
// return (Stream<T>) map(e -> {
// InternalComponent ic = (InternalComponent) e;
// Object[] instances = ic.instancesIfAvailable();
// if (instances != null) {
// Object instance = instances[0];
// if (type.isInstance(instance)) {
// return instance;
// }
// }
// return null;
// }).filter(e -> e != null);
// }
//
// /** {@inheritDoc} */
// @SuppressWarnings("unchecked")
// @Override
// public <T> void forEachInstanceOf(Class<T> type, BiConsumer<? super Component, ? super T> consumer) {
// requireNonNull(type, "type is null");
// requireNonNull(consumer, "consumer is null");
// forEach(component -> {
// InternalComponent ic = (InternalComponent) component;
// Object[] instances = ic.instancesIfAvailable();
// if (instances != null) {
// Object instance = instances[0];
// if (type.isInstance(instance)) {
// consumer.accept(component, (T) instance);
// }
// }
// });
// }
//
// /** {@inheritDoc} */
// @SuppressWarnings("unchecked")
// @Override
// public <T> void forEachInstanceOf(Class<T> type, Consumer<? super T> consumer) {
// requireNonNull(type, "type is null");
// requireNonNull(consumer, "consumer is null");
// forEach(component -> {
// InternalComponent ic = (InternalComponent) component;
// Object[] instances = ic.instancesIfAvailable();
// if (instances != null) {
// Object instance = instances[0];
// if (type.isInstance(instance)) {
// consumer.accept((T) instance);
// }
// }
// });
// }