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

package org.treblereel;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.treblereel.config.TestRestConfig;
import org.treblereel.gwt.rest.client.RestResponse;
import org.treblereel.gwt.rest.client.proxy.UrlBuilder;
import org.treblereel.model.Item;
import org.treblereel.server.EmbeddedItemServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RestCallerIntegrationTest {

    private static EmbeddedItemServer server;
    private App app;

    @BeforeClass
    public static void startServer() throws Exception {
        UrlBuilder.setEncoder(value -> {
            try {
                return URLEncoder.encode(value, StandardCharsets.UTF_8);
            } catch (Exception e) {
                return value;
            }
        });
        server = new EmbeddedItemServer();
        server.start();
        TestRestConfig.BASE_URL = server.getBaseUrl();
    }

    @Before
    public void setUp() {
        app = new App();
        new AppBootstrap(app).initialize();
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testCallerIsInjected() {
        assertNotNull(app.itemServiceCaller);
    }

    @Test
    public void testGetItem() {
        Item[] result = new Item[1];
        app.itemServiceCaller.call(r -> result[0] = (Item) r).getItem(1);

        assertNotNull(result[0]);
        assertEquals(1, result[0].getId());
        assertEquals("item-1", result[0].getName());
    }

    @Test
    public void testListItems() {
        List<Item>[] result = new List[1];
        app.itemServiceCaller.call(r -> result[0] = (List<Item>) r).listItems();

        assertNotNull(result[0]);
        assertEquals(2, result[0].size());
        assertEquals(1, result[0].get(0).getId());
        assertEquals("item-1", result[0].get(0).getName());
        assertEquals(2, result[0].get(1).getId());
        assertEquals("item-2", result[0].get(1).getName());
    }

    @Test
    public void testCreateItem() {
        Item newItem = new Item(99, "new-item");
        Item[] result = new Item[1];
        app.itemServiceCaller.call(r -> result[0] = (Item) r).createItem(newItem);

        assertNotNull(result[0]);
        assertEquals(99, result[0].getId());
        assertEquals("new-item", result[0].getName());
    }

    // --- PUT ---

    @Test
    public void testUpdateItem() {
        Item updated = new Item(0, "updated-name");
        Item[] result = new Item[1];
        app.itemServiceCaller.call(r -> result[0] = (Item) r).updateItem(5, updated);

        assertNotNull(result[0]);
        assertEquals(5, result[0].getId());
        assertEquals("updated-name", result[0].getName());
    }

    // --- DELETE ---

    @Test
    public void testDeleteItem() {
        Item[] result = new Item[1];
        app.itemServiceCaller.call(r -> result[0] = (Item) r).deleteItem(3);

        assertNotNull(result[0]);
        assertEquals(3, result[0].getId());
        assertEquals("deleted-3", result[0].getName());
    }

    // --- PATCH ---

    @Test
    public void testPatchItem() {
        Item patch = new Item(7, "patched-name");
        Item[] result = new Item[1];
        app.itemServiceCaller.call(r -> result[0] = (Item) r).patchItem(7, patch);

        assertNotNull(result[0]);
        assertEquals(7, result[0].getId());
        assertEquals("patched-name", result[0].getName());
    }

    // --- void return ---

    @Test
    public void testDeleteItemVoid() {
        boolean[] called = {false};
        app.itemServiceCaller.call(r -> called[0] = true).deleteItemVoid(10);

        assertTrue(called[0]);
    }

    // --- @QueryParam ---

    @Test
    public void testSearchItems() {
        List<Item>[] result = new List[1];
        app.itemServiceCaller.call(r -> result[0] = (List<Item>) r).searchItems("widget");

        assertNotNull(result[0]);
        assertEquals(1, result[0].size());
        assertEquals("widget", result[0].get(0).getName());
    }

    // --- @QueryParam + @DefaultValue ---

    @Test
    public void testSearchItemsDefaultPage() {
        List<Item>[] result = new List[1];
        app.itemServiceCaller.call(r -> result[0] = (List<Item>) r)
                .searchItemsDefaultPage(null);

        assertNotNull(result[0]);
        assertEquals(1, result[0].size());
        assertEquals("page-0", result[0].get(0).getName());
    }

    // --- @HeaderParam ---

    @Test
    public void testGetItemWithHeader() {
        Item[] result = new Item[1];
        app.itemServiceCaller.call(r -> result[0] = (Item) r)
                .getItemWithHeader(1, "my-header-value");

        assertNotNull(result[0]);
        assertEquals(1, result[0].getId());
        assertEquals("my-header-value", result[0].getName());
    }

    // --- @CookieParam ---

    @Test
    public void testGetItemWithCookie() {
        Item[] result = new Item[1];
        app.itemServiceCaller.call(r -> result[0] = (Item) r)
                .getItemWithCookie(2, "abc123");

        assertNotNull(result[0]);
        assertEquals(2, result[0].getId());
        assertEquals("abc123", result[0].getName());
    }

    // --- @FormParam ---

    @Test
    public void testCreateItemFromForm() {
        Item[] result = new Item[1];
        app.itemServiceCaller.call(r -> result[0] = (Item) r).createItemFromForm(42, "form-item");

        assertNotNull(result[0]);
        assertEquals(42, result[0].getId());
        assertEquals("form-item", result[0].getName());
    }

    // --- Multiple @PathParam ---

    @Test
    public void testMultiplePathParams() {
        Item[] result = new Item[1];
        app.itemServiceCaller.call(r -> result[0] = (Item) r).getRelatedItem(3, 7);

        assertNotNull(result[0]);
        assertEquals(10, result[0].getId());
        assertEquals("related-3-7", result[0].getName());
    }

    // --- FullResponseCallback ---

    @Test
    public void testFullResponseCallback() {
        Item[] bodyResult = new Item[1];
        RestResponse[] responseResult = new RestResponse[1];
        app.itemServiceCaller
                .call((body, response) -> {
                    bodyResult[0] = (Item) body;
                    responseResult[0] = response;
                })
                .getItem(1);

        assertNotNull(bodyResult[0]);
        assertEquals(1, bodyResult[0].getId());
        assertNotNull(responseResult[0]);
        assertEquals(200, responseResult[0].getStatusCode());
    }

    // --- ErrorCallback 404 ---

    @Test
    public void testErrorCallback404() {
        RestResponse[] errorResponse = new RestResponse[1];
        Item[] successResult = new Item[1];
        app.itemServiceCaller
                .onError((response, throwable) -> errorResponse[0] = response)
                .call(r -> successResult[0] = (Item) r)
                .getError404();

        assertNotNull(errorResponse[0]);
        assertEquals(404, errorResponse[0].getStatusCode());
        assertNull(successResult[0]);
    }

    // --- ErrorCallback 500 ---

    @Test
    public void testErrorCallback500() {
        RestResponse[] errorResponse = new RestResponse[1];
        Item[] successResult = new Item[1];
        app.itemServiceCaller
                .onError((response, throwable) -> errorResponse[0] = response)
                .call(r -> successResult[0] = (Item) r)
                .getError500();

        assertNotNull(errorResponse[0]);
        assertEquals(500, errorResponse[0].getStatusCode());
        assertNull(successResult[0]);
    }
}
