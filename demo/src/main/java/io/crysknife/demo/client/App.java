/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.demo.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import io.crysknife.client.Reflect;
import io.crysknife.demo.client.about.About;
import io.crysknife.demo.client.events.Address;
import io.crysknife.demo.client.events.User;
import io.crysknife.demo.client.named.NamedBeanConstructorInjectionPanel;
import io.crysknife.annotation.Application;
import io.crysknife.annotation.ComponentScan;
import io.crysknife.ui.navigation.client.local.DefaultPage;
import io.crysknife.ui.navigation.client.local.Navigation;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;
import jsinterop.base.JsForEachCallbackFn;

@Application
@ComponentScan("io.crysknife.demo.client")
public class App {

    @Inject
    private HTMLDivElement toast;

    @Inject
    private NamedBeanConstructorInjectionPanel namedBeanConstructorInjectionPanel;

    @Inject
    private Main main;

    @Inject
    private Navigation navigation;

    @Inject
    public Test about;

    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init() {
        DomGlobal.document.body.appendChild(main.element());
        initToast();
        navigation.goToWithRole(DefaultPage.class);

            about.init();
            String field = Reflect.objectProperty("f_test__io_crysknife_demo_client_Test", about);

            Js.asPropertyMap(about).set(field, "TEST");

            DomGlobal.console.log("field ? " + about.test);

            String method = Reflect.objectProperty("m_init__io_crysknife_demo_client_Test", about);

        //DomGlobal.console.log("typeof ? " + Js.typeof(Js.asPropertyMap(about).get(method)));


        ((Fn) Js.uncheckedCast(Js.asPropertyMap(about).get(method))).invoke();

        DomGlobal.console.log(">>> " + Global.JSON.stringify(about));

        Js.asPropertyMap(about).forEach(new JsForEachCallbackFn() {
            @Override
            public void onKey(String key) {
                DomGlobal.console.log("KEY " + key + " -> " + Js.asPropertyMap(about).get(key));
            }
        });

        DomGlobal.console.log("about ? ");


            //((Runnable)Js.uncheckedCast(Js.asPropertyMap(this).get(method))).run();


    }

    @FunctionalInterface
    @JsFunction
    private interface Fn {

        void invoke();

    }

    private void initToast() {
        toast.id = "snackbar";
        toast.textContent = "LuckyMe";

        DomGlobal.document.body.appendChild(toast);
    }

    void onUserEvent(@Observes User user) {
        toast.className = "show";
        toast.textContent = "App : onEvent " + user.toString();

        DomGlobal.setTimeout(p0 -> toast.className = toast.className.replace("show", ""), 3000);
    }

    void onAddressEvent(@Observes Address address) {
        toast.className = "show";
        toast.textContent = "App : onEvent " + address.toString();

        DomGlobal.setTimeout(p0 -> toast.className = toast.className.replace("show", ""), 3000);
    }
}
