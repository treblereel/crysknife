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

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import io.crysknife.client.BeanManager;
import io.crysknife.client.ManagedInstance;
import io.crysknife.demo.client.llop.BeanOne;
import io.crysknife.demo.client.llop.BeanTwo;
import io.crysknife.demo.client.ul.Starter;
import io.crysknife.demo.client.ul.UlElement;
import io.crysknife.ui.navigation.client.local.Navigation;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.elemento.IsElement;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/12/20
 */
@Singleton
@Templated(value = "main.html")
public class Main implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    private HTMLDivElement root, container;
    @Inject
    private Navigation navigation;

    @Inject
    BeanOne beanOne;

    @Inject
    BeanTwo beanTwo;

    @Inject
    public Main(BeanManager beanManager) {

    }

    @PostConstruct
    protected void init() {
        navigation.setNavigationContainer(container);

        beanTwo.say();
    }

    @Override
    public HTMLDivElement element() {
        return root;
    }
}
