package com.supermarket.finder.service.finders.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

/**
 * The Class HipercorFinder.
 */
@Service
@Order(OrderFinders.HIPERCOR)
public class HipercorFinder extends AbstractFinder implements Finder {
    /** The logger. */
    // private final Logger logger = LoggerFactory.getLogger(HipercorFinder.class);

    private final String marketUri = "https://www.hipercor.es/alimentacion/api/catalog/supermercado/type_ahead/?question=%s&scope=supermarket&center=010MOH&results=10";
    private final String imageHost = "https:";
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

        final JsonArray productsJsonList = ((JsonArray) ((JsonObject) ((JsonObject) responseJsonObj
                .get("catalog_result")).get("products_list")).get("items"));

        if (productsJsonList != null) {
            for (JsonElement productJson : productsJsonList) {
                JsonObject productObj = (JsonObject) ((JsonObject) productJson).get("product");
                Product product = new Product();
                product.setMarket(Market.HIPERCOR);
                product.setBrand("-");
                product.setPrice(
                        ((JsonPrimitive) ((JsonObject) productObj.get("price")).get("seo_price")).getAsFloat());
                product.setName(productObj.get("name").getAsString());
                
                String imagePath = ((JsonPrimitive) ((JsonObject) productObj.get("media")).get("thumbnail_url")).getAsString();
                if(!StringUtils.isBlank(imagePath)) {
                	imagePath = imagePath.replace("40x40", "325x325");
                	product.setImage(StringUtils.join(imageHost,imagePath));	
                }

                productList.add(product);
            }
        }

        return productList;
    }

}
