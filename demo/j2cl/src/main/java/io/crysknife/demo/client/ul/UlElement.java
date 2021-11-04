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

package io.crysknife.demo.client.ul;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Random;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 10/28/21
 */
@Dependent
@Templated
public class UlElement implements IsElement {

    private static int _step = 0;
    //@Inject
    @DataField
    public HTMLButtonElement categoryIcon;
    private String name;
    private int step;
    @Inject
    @DataField
    private HTMLLIElement listGroupItem;

    @Inject
    public UlElement(HTMLButtonElement categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    @PostConstruct
    public void doInit() {
        step = _step++;

        DomGlobal.console.log("STEP INIT " + step);


        categoryIcon.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                DomGlobal.console.log("ZZZZ " + name);

            }
        });
    }

    public void init(String name) {
        categoryIcon.id = "_" + name;
        categoryIcon.textContent = name;
        this.name = name;
    }


    @EventHandler("categoryIcon")
    public void onMouseDown(@ForEvent("mousedown") MouseEvent event) {
        DomGlobal.console.log("mousedown : " + name + " " + new Random().nextInt());
        DomGlobal.console.log("step  : " + step);
        DomGlobal.console.log("inst  : " + this);
        DomGlobal.console.log("btn  : " + categoryIcon.id);
    }

}
