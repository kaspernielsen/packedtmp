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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.module.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import app.packed.inject.Injector;
import app.packed.inject.InjectorConfigurator;
import app.packed.inject.ServiceWirelets;
import packed.internal.container.AppPackedBundleSupport;
import packed.internal.container.DefaultContainerConfiguration;
import packed.internal.container.WireletList;

// Wire vs link....

// Interface -> kan man let implementere, uden at fucke et nedarvnings hiraki op
// Klasse -> Vi kan have protected metoder
/**
 * A wiring operation is a piece of glue code that wire bundles and/or runtimes together, through operations such as
 * {@link InjectorConfigurator#provideAll(Injector, Wirelet...)} or
 * <p>
 * As a rule of thumb wirelets are evaluated in order. For example, ContainerWirelets.name("ffff"),
 * ContainerWirelets.name("sdsdsd"). Will first the change the name to ffff, and then change it to sdsds. Maybe an
 * example with.noStart + start_await it better.
 * 
 * <p>
 * A typical usage for wiring operations is for rebinding services under another key when wiring an injector into
 * another injector.
 * 
 * start with peek, then import with peek around a service Available as {@code X} in one injector available under a key
 * {@code Y}
 * 
 * Show example where we rebind X to Y, and Y to X, maybe with a peek inbetween
 * 
 * Operations is order Example with rebind
 * 
 * 
 * Pipeline
 */
// https://martinfowler.com/articles/collection-pipeline/
// A bundling always has a bundle (source) and a target???

// WiringOptions
/// kunne have en bundle.disableConfigurationSite()... <- Saa har man den kun paa toplevel app'en....
//// NoConfigurationSite, ForceConfigurationSite

//// ExportTransient -> Meaning everything is exported out again from the bundle
//// exportTransient(Filter) <-Kunne ogsaa vaere paa WiredBundle

// Push wirelets down to sub containers...
//// Should not be supported because it would break encapsulation....
//// Would be nice with debugging options... For example, enable TOMCAT high debug mode
// This is implementation detail...
//// Basically propagation of support options...

// Hierachical composition

// Wiring methods
// Linked: refering to the process of linking multiple bundles together. Linkages is final
// Hosted: referers to wiring multiple apps together
// Detached:
public abstract class Wirelet {

    static {
        AppPackedBundleSupport.Helper.init(new AppPackedBundleSupport.Helper() {

            @Override
            public List<Wirelet> extractWiringOperations(Wirelet[] operations, Class<?> type) {
                throw new UnsupportedOperationException();
                // return WireletList.operationsExtract(operations, type);
            }

            /** {@inheritDoc} */
            @Override
            public void finishWireOperation(Wirelet operation) {
                // operation.onFinish();
            }

            @Override
            public Lookup lookupFromWireOperation(Wirelet operation) {
                throw new UnsupportedOperationException();
            }

            // /** {@inheritDoc} */
            // @Override
            // public void process(Wirelet operation, BundleLink link) {
            // operation.process(link);
            // }

            /** {@inheritDoc} */
            @Override
            public void startWireOperation(Wirelet operation) {
                // operation.process(null);
            }
        });
    }

    /** Invoked by subclasses. */
    protected Wirelet() {}

    // For bedre error messages. This operation can only be used if the parent or child bundle
    // has installed the XXX extension (As an alternative, annotated the key with
    // @RequiresExtension(JMXExtension.class)....)
    // Or even better the actual WiringOperation.....
    // protected void useAttachment(Key...., Class<?> requiredExtension);

    // Bootstrap classes... Classes that are only available for injection.... (Not even initialized....)
    // bundleLink.bootstrapWith(StringArgs.of("sdsdsd");
    // bundleLink.bootstrapWith(Configuration.read("c:/sdasdasd\'");
    // run(new XBundle(), Configuration.read("c:/sdad"));

    /**
     * Returns a composed {@code WiringOperation} that performs, in sequence, this operation followed by the {@code after}
     * operation. If performing either operation throws an exception, it is relayed to the caller of the composed operation.
     * If performing this operation throws an exception, the {@code after} operation will not be performed.
     *
     * @param after
     *            the operation to perform after this operation
     * @return a composed {@code WiringOperation} that performs in sequence this operation followed by the {@code after}
     *         operation
     */
    public final Wirelet andThen(Wirelet after) {
        // Maaske bare compose...
        return compose(this, requireNonNull(after, "after is null"));
    }

    public final Wirelet andThen(Wirelet... nextOperations) {
        ArrayList<Wirelet> l = new ArrayList<>();
        l.add(this);
        l.addAll(List.of(nextOperations));
        return compose(l.toArray(i -> new Wirelet[i]));
    }

    protected void check() {
        // checkApp() for Wirelet.appTimeToLive for example...
    }

    // force start, initialize, await start...
    protected final void checkApp() {

    }

    /**
     * Creates an {@link MainArgs} from the specified arguments. And returns a wirelet that provides it, via
     * {@link ServiceWirelets#provide(Object)}, to the linked container.
     * 
     * @param args
     *            the arguments to inject
     * @return a wirelet that provides the specified arguments to the linked container
     */
    public static Wirelet appMain(String... args) {
        return ServiceWirelets.provide(MainArgs.of(args));
    }

    /**
     * Sets a maximum time for the container to run. When the deadline podpodf the app is shutdown.
     * 
     * @param timeout
     *            the timeout
     * @param unit
     *            the timeunit
     * @return this option object
     */
    // These can only be used with a TopContainer with lifecycle...
    // Container will be shutdown normally after the specified timeout

    // Vi vil gerne have en version, vi kan refreshe ogsaa???
    // Maaske vi bare ikke skal supportered det direkte.

    // Teknisk set er det vel en app wirelet
    public static Wirelet appTimeToLive(long timeout, TimeUnit unit) {
        // Shuts down container normally
        throw new UnsupportedOperationException();
    }

    public static Wirelet appTimeToLive(long timeout, TimeUnit unit, Supplier<Throwable> supplier) {
        appTimeToLive(10, TimeUnit.SECONDS, () -> new CancellationException());
        // Alternativ, kan man wrappe dem i f.eks. WiringOperation.requireExecutionMode();
        return new Wirelet() {

            // @Override
            // protected void process(BundleLink link) {
            // link.mode().checkExecute();
            // }
        };
    }

    /**
     * Creates a wiring operation by composing a sequence of zero or more wiring operations.
     * 
     * @param operations
     *            the operations to combine
     * @return a new combined operation
     * @see #andThen(Wirelet)
     * @see #andThen(Wirelet...)
     */
    public static Wirelet compose(Wirelet... operations) {
        return WireletList.of(operations);
    }

    public static Wirelet configure(Configuration c) {
        // This is for App, but why not for Injector also...
        // we need config(String) for wire()..... configOptional() also maybe...
        // Would be nice.. if config extends WiringOperations
        // alternative c.wire();
        // c.get("/sdfsdf").wire();

        // Maaske skal nogle klasser bare implementere WiringOperation...
        throw new UnsupportedOperationException();
    }

    // wire(new XBundle(), Wirelet.configure(Configuration.read("dddd");
    // mapConfiguration, childConfiguration()
    // ConfigurationWirelets.provide(Configuration c)
    // ConfigurationWirelets.map(ConfigurationTransformer transformer)
    // ConfigurationWirelets.mapChild(String childName) //calls map
    // ConfigurationWirelets.mapChild(Configuration c, ConfigurationTransformer)
    public static Wirelet configure(String childName) {
        // Extracts the child named 'childName' for a configuration in the current context
        // configure(c, "child")
        throw new UnsupportedOperationException();
    }

    public static Wirelet configure(Configuration c, String child) {
        // configure(c, "child")
        throw new UnsupportedOperationException();
    }

    // protected void validate(); Validates that the operation can be used

    static Wirelet disableConfigSet() {
        // Man skal vel ogsaa kunne enable den igen....
        throw new UnsupportedOperationException();
    }

    static Wirelet lookup(MethodHandles.Lookup lookup) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a wirelet that will set name of a container once wired, overriding any existing name it may have.
     * 
     * @param name
     *            the name of the container
     * @return a wirelet that will set name of a container once wired
     */
    public static Wirelet name(String name) {
        return new DefaultContainerConfiguration.OverrideNameWiringOption(name);
    }
}

// Ved ikke om det er noget vi kommer til at bruge...
// public static Wirelet of(Consumer<BundleLink> consumer) {
// requireNonNull(consumer, "consumer is null");
// return new Wirelet() {
//
// @Override
// protected void process(BundleLink link) {
// consumer.accept(link);
// }
// };
// }
// Kunne vaere rart, hvis man f.eks. kunne
// wire(SomeBundle.class, JaxRSSpec.v2_1.wireStrict());
// wire(SomeBundle.class, JettySpec.v23_1.wireStrict());

// Wirelet.implements)_

/// Ideen er at JettySpec.23_1 kan vaere + JaxRSSpec.2_1
// ComponentInstanceHook
// AnnotatedComponentTypeHook
// AnnotatedComponentMethodHook

/// activeInstance(Startable.class)
/// activeAnnotatedMethod(onStart.class)...
//// De skal vaere en del af specifikationen
// Activators
// InstanceOfActivator (Component+<T>)
// AnnotatedTypeActivator
// AnnotatedMethodActivator
// ServiceLoader.enabledInvocationOnComponentMethodsAnnotatedWith(xxx.class, ...);
// Bundling -> import pipeline and an export pipeline where each element of the exposed api is processed through

// protected Configuration onConfiguration(@Nullable Configuration<?> configuration) {} or
// protected void onConfiguration(ConfigurationBuilder configuration) {} and then we check for overrides.
// ImportExportStage configuration.extractChild("jetty"); installContainer(XBundle.class, ConfigurationTransformer<>
// Kan ogsaa vaere en alm service, og saa naa den er der saa blive annoteringerne aktiveret....
/// Men ihvertfal conf bliver skubbet op fra roden... Man kan aldrig exportere en Configuration.. Eller det kan man vel
// godt.
// Optional Configuration-> You may provide a configuration if you want, mandatory you must provide a Confgiuration.
// A configuration is always bound with singleton scope, not lazy, not prototype

// Fordi vi ikke har en ServiceConfiguration
// Make public... Vil helst have den i Lifecycle maaske. Saa kan vi ogsaa registere den paa
// onStart(String name)... skal kunne saette navnet, saa maaske
// onStart(CompletionStage).setName(); eller
// onStart(String startingPointName, CompletionStage)
// men hvis ogsaa skulle kunne vente a.la. onStart("cacheLoader").thenRun(sysout(yeah"));