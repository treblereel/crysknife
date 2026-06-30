/*
 * Copyright © 2026 Treblereel
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

package io.crysknife.tests.rest.client;

import java.util.List;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import io.crysknife.client.IsElement;
import io.crysknife.tests.rest.shared.model.Item;
import io.crysknife.tests.rest.shared.service.ItemService;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.treblereel.gwt.rest.client.Caller;

@Singleton
@Templated("resttestpanel.html")
public class RestTestPanel implements IsElement<HTMLDivElement> {

    @Inject
    Caller<ItemService> itemServiceCaller;

    @Inject
    @DataField
    HTMLDivElement root;

    @Inject
    @DataField
    HTMLButtonElement getItemBtn;

    @Inject
    @DataField
    HTMLDivElement getItemResult;

    @Inject
    @DataField
    HTMLButtonElement listItemsBtn;

    @Inject
    @DataField
    HTMLDivElement listItemsResult;

    @Inject
    @DataField
    HTMLButtonElement createItemBtn;

    @Inject
    @DataField
    HTMLDivElement createItemResult;

    @EventHandler("getItemBtn")
    public void onGetItem(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                getItemResult.textContent = "ERROR: " + (throwable != null ? throwable.getMessage() : response.getStatusText());
            })
            .call(r -> {
                Item item = (Item) r;
                getItemResult.textContent = item.getId() + ":" + item.getName();
            })
            .getItem(1);
    }

    @EventHandler("listItemsBtn")
    @SuppressWarnings("unchecked")
    public void onListItems(@ForEvent("click") MouseEvent e) {
        itemServiceCaller
            .onError((response, throwable) -> {
                listItemsResult.textContent = "ERROR: " + (throwable != null ? throwable.getMessage() : response.getStatusText());
            })
            .call(r -> {
                List<Item> list = (List<Item>) r;
                listItemsResult.textContent = String.valueOf(list.size());
            })
            .listItems();
    }

    @EventHandler("createItemBtn")
    public void onCreateItem(@ForEvent("click") MouseEvent e) {
        Item newItem = new Item();
        newItem.setId(99);
        newItem.setName("new-item");
        itemServiceCaller
            .onError((response, throwable) -> {
                createItemResult.textContent = "ERROR: " + (throwable != null ? throwable.getMessage() : response.getStatusText());
            })
            .call(r -> {
                Item created = (Item) r;
                createItemResult.textContent = created.getId() + ":" + created.getName();
            })
            .createItem(newItem);
    }

    @Override
    public HTMLDivElement getElement() {
        return root;
    }
}
