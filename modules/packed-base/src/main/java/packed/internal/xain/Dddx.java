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
package packed.internal.xain;

import java.nio.file.Path;

/**
 *
 */
public class Dddx {

    @ScheduleAtFixedRate(10000)
    static final Runnable r = () -> System.out.println("Hello");

    @ScheduleAtFixedRate(10000)
    public void hello() {
        System.out.println("Hello");
    }

    public static void main(String[] args) {
        System.out.println(Path.of("/").iterator().hasNext());
    }
}

@interface ScheduleAtFixedRate {
    long value();
}