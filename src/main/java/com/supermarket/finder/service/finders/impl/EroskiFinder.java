package com.supermarket.finder.service.finders.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.supermarket.finder.dto.Market;
import com.supermarket.finder.dto.Product;
import com.supermarket.finder.service.finders.AbstractFinder;
import com.supermarket.finder.service.finders.Finder;

@Service
@Order(OrderFinders.EROSKI)
public class EroskiFinder extends AbstractFinder implements Finder {
    /** The logger. */
    // private final Logger logger = LoggerFactory.getLogger(EroskiFinder.class);

    private final String marketUri = "https://supermercado.eroski.es/es/search/results/?q=%s&suggestionsFilter=false";
    
    private final String imageHost =  "https://supermercado.eroski.es/images/";

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

        if (responseJsonObj != null) {

            final JsonArray productsJsonList = (JsonArray) responseJsonObj.get("list");

            int count = 0;

            if (productsJsonList != null) {
                for (JsonElement productJson : productsJsonList) {
                    JsonObject productObj = (JsonObject) ((JsonObject) productJson);
                    Product product = new Product();
                    product.setMarket(Market.EROSKI);
                    product.setBrand(productObj.get("brand").getAsString());
                    product.setPrice(productObj.get("price").getAsFloat());
                    product.setName(productObj.get("name").getAsString());
                    
                    final String id = productObj.get("id")!=null?productObj.get("id").getAsString():StringUtils.EMPTY;
                    if(!StringUtils.isBlank(id)) {
                    	product.setImage(StringUtils.join(imageHost,id,".jpg"));	
                    }

                    productList.add(product);

                    count++;

                    if (count > 20) {
                        break;
                    }
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

        // String responseStr = responseString;
        // responseStr = responseStr.replace("\n", "").replace("\r", "");
        //
        // Pattern p = Pattern.compile("(((product-title\\\"><a)[^>]*>)([^>]*)<)|((price-product\">)([^<]*)<)");
        // Matcher m = p.matcher(responseStr);
        // while (m.find()) {
        // String tag = m.group(3);
        // System.out.println(tag);
        // }
        //
        // return "";

        String responseStr = responseString;
        int startJsonPos = responseString.indexOf("impressions");
        if (startJsonPos < 0) {
            return "";
        }
        responseStr = responseString.substring("impressions".length() + 3);
        responseStr = responseStr.substring(startJsonPos);
        int endJsonPos = responseStr.indexOf("]");
        responseStr = responseStr.substring(0, endJsonPos + 1);
        responseStr = responseStr.replace("\\", "");

        return "{\"list\":" + responseStr + "}";

    }

}
