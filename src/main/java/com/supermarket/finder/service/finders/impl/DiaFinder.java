package com.supermarket.finder.service.finders.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.supermarket.finder.dto.Market;
import com.supermarket.finder.dto.Product;
import com.supermarket.finder.service.finders.AbstractFinder;
import com.supermarket.finder.service.finders.Finder;

@Service
@Order(OrderFinders.DIA)
public class DiaFinder extends AbstractFinder implements Finder {
    /** The logger. */
    // private final Logger logger = LoggerFactory.getLogger(DiaFinder.class);

    private final String marketUri = "https://www.dia.es/compra-online/search/autocompleteSecure?term=%s&maxResults=10";

    /**
     * Gets the market uri.
     *
     * @return the market uri
     */
    protected String getMarketUri() {
        return this.marketUri;
    }

    /**
     * Gets the product list.
     *
     * @param responseJsonObj
     *            the response json obj
     * @return the product list
     */
    protected List<Product> getProductList(JsonObject responseJsonObj) {

        List<Product> productList = new ArrayList<Product>();

        final JsonArray productsJsonList = ((JsonArray) responseJsonObj.get("lightProducts"));

        if (productsJsonList != null) {
            for (JsonElement productJson : productsJsonList) {
                JsonObject productObj = (JsonObject) productJson;
                Product product = new Product();
                product.setMarket(Market.DIA);
                product.setBrand("-");
                product.setPrice(((JsonPrimitive) ((JsonObject) productObj.get("price")).get("value")).getAsFloat());
                product.setName(productObj.get("name").getAsString());

                productList.add(product);
            }
        }

        return productList;
    }
}
