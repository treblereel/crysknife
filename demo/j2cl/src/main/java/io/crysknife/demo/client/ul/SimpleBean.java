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
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import java.util.Random;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 10/28/21
 */
@Dependent
@Templated
public class SimpleBean implements IsElement {

    @DataField
    private final HTMLElement root = (HTMLElement) DomGlobal.document.createElement("div");

    @DataField
    HTMLButtonElement btn = (HTMLButtonElement) DomGlobal.document.createElement("button");

    private String name;

    @PostConstruct
    public void init() {

    }

    public void init(String name) {
        this.name = name;
    }

    public HTMLElement getElement() {
        return root;
    }

    @EventHandler("btn")
    public void onMouseDown(@ForEvent("mousedown") MouseEvent event) {
        DomGlobal.console.log("SimpleBean : " + name);
    }
}
