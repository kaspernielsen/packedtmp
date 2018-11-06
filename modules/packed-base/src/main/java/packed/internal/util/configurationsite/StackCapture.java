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

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.Optional;

import app.packed.util.ConfigurationSite;

/**
 *
 */
class StackCapture {

    public enum CaptureType {

        /** */
        INJECTOR_OF("Injector.of"),

        /** */
        INJECTOR_BIND("Injector.bind"),

        /** */
        INJECTOR_IMPORT_FROM("Injector.importFrom");

        CaptureType(String f) {
            this.f = requireNonNull(f);
        }

        final String f;

        @Override
        public String toString() {
            return f;
        }
    }

    public static ConfigurationSite create(CaptureType t) {
        Optional<StackFrame> sf = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
                .walk(e -> e.filter(f -> !f.getClassName().contains("cake")).findFirst());
        return new ARegistrationPoint(null, sf, t);
    }

    public static ConfigurationSite create(ConfigurationSite p, CaptureType t) {
        Optional<StackFrame> sf = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
                .walk(e -> e.filter(f -> !f.getClassName().contains("cake")).findFirst());
        return new ARegistrationPoint((ARegistrationPoint) p, sf, t);
    }

    static class ARegistrationPoint implements ConfigurationSite {

        Optional<StackFrame> caller;

        ARegistrationPoint parent;
        CaptureType captureType;

        ARegistrationPoint(ARegistrationPoint parent, Optional<StackFrame> caller, CaptureType captureType) {
            this.parent = parent;
            this.caller = requireNonNull(caller);
            this.captureType = requireNonNull(captureType);
        }

        /** {@inheritDoc} */
        @Override
        public Optional<ConfigurationSite> parent() {
            return Optional.of(parent);
        }

        @Override
        public String toString() {
            if (!caller.isPresent()) {
                return "<No Info>";
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            ARegistrationPoint a = this;
            while (a != null) {
                for (int j = 0; j < i; j++) {
                    sb.append("  ");
                }
                sb.append(a.captureType.toString()).append(" -> ");
                // sb.append(a.getCaller().get());
                a = a.parent;
                sb.append("\n");
            }
            return sb.toString();
        }

        /** {@inheritDoc} */
        @Override
        public String operation() {
            return null;
        }

    }
}