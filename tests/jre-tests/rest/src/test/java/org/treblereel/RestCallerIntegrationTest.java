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
import org.treblereel.model.Item;
import org.treblereel.server.EmbeddedItemServer;
import org.treblereel.gwt.rest.client.proxy.UrlBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
}
