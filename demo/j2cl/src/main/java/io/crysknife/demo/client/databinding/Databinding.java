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

package io.crysknife.demo.client.databinding;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLBRElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import io.crysknife.demo.client.databinding.listcomponent.KeyValueRow;
import io.crysknife.demo.client.databinding.listcomponent.RolesEditorWidgetView;
import io.crysknife.ui.databinding.client.api.AutoBound;
import io.crysknife.ui.databinding.client.api.Bound;
import io.crysknife.ui.databinding.client.api.DataBinder;
import io.crysknife.ui.databinding.client.api.StateSync;
import io.crysknife.ui.databinding.client.api.handler.property.PropertyChangeHandler;
import io.crysknife.ui.navigation.client.local.Page;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.CheckBox;
import org.gwtproject.user.client.ui.TextBox;
import org.jboss.elemento.IsElement;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/18/19
 */
@Singleton
@Page
@Templated(value = "databinding.html")
public class Databinding implements IsElement<HTMLDivElement> {

    @Inject
    @DataField("rolesEditorWidgetView")
    RolesEditorWidgetView rolesEditorWidgetView;
    @DataField
    private HTMLDivElement root = (HTMLDivElement) DomGlobal.document.createElement("div");
    @Inject
    @AutoBound
    private DataBinder<Customer> dataBinder;
    @DataField
    @Inject
    @Bound(property = "name")
    private TextBox nameBox;
    @DataField
    @Inject
    @Bound
    private CheckBox active;
    @DataField
    @Inject
    @Bound(property = "address.city")
    private MyTextBox cityBox;
    @Inject
    @DataField
    @Bound(property = "address.street.name")
    private HTMLInputElement streetBox;
    @Inject
    @DataField
    @Bound
    private HTMLInputElement age;
    @Inject
    @DataField
    private HTMLInputElement resultBox;
    @Inject
    @DataField
    private HTMLButtonElement modelBtn;
    @Inject
    @DataField
    private HTMLButtonElement workingModelBtn;
    @Inject
    @DataField("pauseBtn")
    private HTMLButtonElement pause;
    @Inject
    @DataField("resumeBtn")
    private HTMLButtonElement resume;
    private Customer customer;

    @PostConstruct
    protected void init() {

        Customer customer = new Customer();
        customer.setName("AAAAAAAAA");
        dataBinder.setModel(customer);

/*        customer = dataBinder
                .bind(nameBox, "name")
                .bind(cityBox, "address.city")
                .bind(streetBox, "address.street.name")
                .bind(age, "age")
                .getModel();*/

        dataBinder.addPropertyChangeHandler((PropertyChangeHandler<Customer>) event -> {
            DomGlobal.console.log("new value " + event.toString());
            onPropertyChange(event.toString());
        });

        List<KeyValueRow> list = new ArrayList<>();


        list.add(new KeyValueRow("AAA", "AAA"));
        list.add(new KeyValueRow("BBB", "BBB"));
        list.add(new KeyValueRow("CCC", "CCC"));
        list.add(new KeyValueRow("DDD", "DDD"));

        HTMLBRElement br = (HTMLBRElement) DomGlobal.document.createElement("br");

        rolesEditorWidgetView.setRows(list);

    }

    private void onPropertyChange(String state) {
        resultBox.value = state;
    }

    @Override
    public HTMLDivElement element() {
        return root;
    }

    @EventHandler("cityBox")
    public void onCityBox(ClickEvent handler) {
        DomGlobal.console.log("handlerZZZ " + handler.getClientX() + " " + (dataBinder == null));
        onPropertyChange(dataBinder.getModel().toString());
    }

    @EventHandler("modelBtn")
    protected void getModel(@ForEvent("click") final MouseEvent e) {
        onPropertyChange(dataBinder.getModel().toString());
    }

    @EventHandler("workingModelBtn")
    void getWorkingModel(@ForEvent("click") final MouseEvent e) {
        onPropertyChange(dataBinder.getWorkingModel().toString());
    }

    @EventHandler("pauseBtn")
    void onPause(@ForEvent("click") final MouseEvent e) {
        dataBinder.pause();
    }

    @EventHandler("resumeBtn")
    void onResume(@ForEvent("click") final MouseEvent e) {
        dataBinder.resume(StateSync.FROM_UI);
    }
}
