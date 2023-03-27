package com.supermarket.finder.service.finders.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.supermarket.finder.dto.Market;
import com.supermarket.finder.dto.Product;
import com.supermarket.finder.service.finders.AbstractFinder;
import com.supermarket.finder.service.finders.Finder;

//@Service
//@Order(OrderFinders.EROSKI)
public class EroskiFinder extends AbstractFinder implements Finder {
    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(EroskiFinder.class);

    private final String marketUri = "https://supermercado.eroski.es/es/search/results/?q=%s&suggestionsFilter=false";

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

        final JsonArray productsJsonList = (JsonArray) responseJsonObj.get("listPage");

        int count = 0;

        if (productsJsonList != null) {
            for (JsonElement productJson : productsJsonList) {
                JsonObject productObj = (JsonObject) ((JsonObject) productJson);
                Product product = new Product();
                product.setMarket(Market.ALIMERKA);
                product.setBrand("-");
                product.setPrice(Float.valueOf(productObj.get("pvpnormal").getAsString().replace(",", ".")));
                product.setName(productObj.get("descripcion").getAsString());

                productList.add(product);

                count++;

                if (count > 20) {
                    break;
                }
            }
        }

        return productList;
    }

    /**
     * Pre process response.
     *
     * @param responseString
     *            the response string
     * @return the string
     */
    protected String preProcessResponse(String responseString) {

        String responseStr = responseString;

        Pattern p = Pattern.compile("product-title\"><a.*>(?s)(.*)<\\/a>");
        Matcher m = p.matcher(responseStr);
        while (m.find()) {
            String tag = m.group(1);
            System.out.println(tag);
        }

        return "";
    }

}
