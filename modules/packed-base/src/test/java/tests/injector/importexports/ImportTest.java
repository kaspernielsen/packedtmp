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
package tests.injector.importexports;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import app.packed.bundle.InjectorBundle;
import app.packed.bundle.InjectorImportStage;
import app.packed.inject.Factory1;
import app.packed.inject.Injector;
import app.packed.inject.Key;
import app.packed.inject.Qualifier;

/**
 *
 */
public class ImportTest {

    // The import at (Xxxx) and (Yyyy) both defines are service with Key<ZoneId>
    public static void main(String[] args) {

        Injector i = Injector.of(c -> {
            c.injectorBind(London.class, InjectorImportStage.rebind(Key.of(ZonedDateTime.class), new Key<@ZoneAnno("London") ZonedDateTime>() {}));
            c.injectorBind(London.class, InjectorImportStage.rebind(Key.of(ZonedDateTime.class), new Key<@ZoneAnno("Berlin") ZonedDateTime>() {}));
        });

        i.services().forEach(e -> System.out.println(e.getKey().toStringSimple()));
    }

    public static final class I extends InjectorBundle {

        /** {@inheritDoc} */
        @Override
        protected void configure() {
            expose(bind(ZoneId.systemDefault()).as(ZoneId.class));
            expose(bindPrototype(new Factory1<ZoneId, ZonedDateTime>(ZonedDateTime::now) {}));
        }
    }

    public static final class London extends InjectorBundle {

        /** {@inheritDoc} */
        @Override
        protected void configure() {
            bind(ZoneId.of("Europe/London")).as(ZoneId.class);
            expose(bindPrototype(new Factory1<ZoneId, ZonedDateTime>(ZonedDateTime::now) {}));
        }
    }

    public static final class Berlin extends InjectorBundle {

        /** {@inheritDoc} */
        @Override
        protected void configure() {
            bind(ZoneId.of("Europe/Berlin")).as(ZoneId.class);
            expose(bindPrototype(new Factory1<ZoneId, ZonedDateTime>(ZonedDateTime::now) {}));
        }
    }

    @Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface ZoneAnno {
        String value();
    }
}