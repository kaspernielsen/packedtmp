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
package app.packed.container;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/** An immutable list of wirelets. */
public final class WireletList extends Wirelet implements Iterable<Wirelet> {

    /** An empty wirelet list. */
    private static final WireletList EMPTY = new WireletList();

    /** The wirelest we are wrapping. */
    private final Wirelet[] wirelets;

    private WireletList(Wirelet... wirelets) {
        Wirelet[] tmp = new Wirelet[wirelets.length];
        for (int i = 0; i < wirelets.length; i++) {
            tmp[i] = Objects.requireNonNull(wirelets[i]);
        }
        // If wirelet instanceof WireletList -> Extract
        this.wirelets = tmp;
    }

    /**
     * Consumes the last wirelet of a certain type.
     * 
     * @param <T>
     *            the type of wirelet to consume
     * @param wireletType
     *            the type of wirelet to consume
     * @param consumer
     *            the consumer of the wirelet
     */
    @SuppressWarnings("unchecked")
    public <T extends Wirelet> void consumeLast(Class<T> wireletType, Consumer<? super T> consumer) {
        for (int i = wirelets.length - 1; i >= 0; i--) {
            Wirelet w = wirelets[i];
            if (wireletType.isAssignableFrom(w.getClass())) {
                consumer.accept((T) w);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Wirelet> void forEach(Class<T> wireletType, Consumer<? super T> action) {
        requireNonNull(wireletType, "wireletType is null");
        requireNonNull(action, "action is null");
        for (Wirelet w : wirelets) {
            if (wireletType.isAssignableFrom(w.getClass())) {
                action.accept((T) w);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void forEach(Consumer<? super Wirelet> action) {
        requireNonNull(action, "action is null");
        for (Wirelet w : wirelets) {
            action.accept(w);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Wirelet> iterator() {
        return List.of(wirelets).iterator();
    }

    @SuppressWarnings("unchecked")
    public <T extends Wirelet> Optional<T> last(Class<T> wireletType) {
        for (int i = wirelets.length - 1; i >= 0; i--) {
            Wirelet w = wirelets[i];
            if (wireletType.isAssignableFrom(w.getClass())) {
                return (Optional<T>) Optional.of(w);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns an immutable {@link List} representation of all of the wirelets in this list.
     * 
     * @return a immutable list representation of all of the wirelets in this list
     */
    public List<Wirelet> toList() {
        return List.of(wirelets);
    }

    /**
     * Returns a list of all wirelets of the specified type
     * 
     * @param <T>
     *            the type of wirelets to return a list for
     * @param wireletType
     *            the type of wirelets to return a list for
     * @return a list of all wirelets of the specified type
     */
    @SuppressWarnings("unchecked")
    public <T extends Wirelet> List<T> toList(Class<T> wireletType) {
        requireNonNull(wireletType, "wireletType is null");
        for (int i = 0; i < wirelets.length; i++) {
            Wirelet w = wirelets[i];
            if (wireletType.isAssignableFrom(w.getClass())) {
                for (int j = i; i < wirelets.length; j++) {
                    System.out.println(j);
                }
                // only found one...
                return (List<T>) List.of(w);
            }
        }
        return List.of();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (wirelets.length > 0) {
            sb.append(wirelets[0]);
            for (int i = 1; i < wirelets.length; i++) {
                sb.append(", ").append(wirelets[i]);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a ampty wirelet list.
     *
     * @return an empty {@code WireletList}
     */
    public static WireletList of() {
        return EMPTY;
    }

    /**
     * Returns a wirelet list containing one element.
     * <p>
     * If the specified wirelet is a WireletList this method will cast and return it as a {@code WireletList}.
     * 
     * @param wirelet
     *            the single wirelet
     * @return a {@code WireletList} containing the specified wirelet
     */
    public static WireletList of(Wirelet wirelet) {
        requireNonNull(wirelet, "wirelet is null");
        if (wirelet instanceof WireletList) {
            return (WireletList) wirelet;
        }
        return new WireletList(wirelet); // we might provide optimized versions in the future
    }

    public static WireletList of(Wirelet... wirelets) {
        requireNonNull(wirelets, "wirelets is null");
        if (wirelets.length == 0) {
            return EMPTY;
        }
        return new WireletList(wirelets);
    }
}