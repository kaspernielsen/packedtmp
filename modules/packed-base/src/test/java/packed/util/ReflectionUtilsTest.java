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
package packed.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import packed.util.ClassUtil;

/**
 *
 */
public class ReflectionUtilsTest {
    @Test
    public void boxClass() {
        assertEquals(Boolean.class, ClassUtil.boxClass(Boolean.TYPE));
        assertEquals(Byte.class, ClassUtil.boxClass(Byte.TYPE));
        assertEquals(Character.class, ClassUtil.boxClass(Character.TYPE));
        assertEquals(Double.class, ClassUtil.boxClass(Double.TYPE));
        assertEquals(Float.class, ClassUtil.boxClass(Float.TYPE));
        assertEquals(Integer.class, ClassUtil.boxClass(Integer.TYPE));
        assertEquals(Long.class, ClassUtil.boxClass(Long.TYPE));
        assertEquals(Short.class, ClassUtil.boxClass(Short.TYPE));
        assertEquals(Short.class, ClassUtil.boxClass(Short.class));
    }

}
