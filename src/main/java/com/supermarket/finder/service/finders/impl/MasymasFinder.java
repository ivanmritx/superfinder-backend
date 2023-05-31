package com.supermarket.finder.service.finders.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
@Order(OrderFinders.MASYMAS)
public class MasymasFinder extends AbstractFinder implements Finder {
    /** The logger. */
    // private final Logger logger = LoggerFactory.getLogger(MasymasFinder.class);

    private final String marketUri = "https://www.supermasymasonline.com/listado_PDO.php?buscar=%s";
    
    private final String imageHost ="https://masymas-services.supermasymas.com/fotos/";

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
                    product.setMarket(Market.MASYMAS);
                    product.setBrand(productObj.get("brand").getAsString());
                    product.setPrice(productObj.get("price").getAsFloat());
                    product.setName(productObj.get("name").getAsString());

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

        String responseStr = responseString;
        int startJsonPos = responseStr.indexOf("listado_products");
        if (startJsonPos < 0) {
            return "";
        }

        responseStr = responseStr.substring(startJsonPos);
        int endJsonPos = responseStr.indexOf("filter_secciones_menu_desktop");
        responseStr = responseStr.substring(0, endJsonPos);
        // responseStr = responseStr.replace("\n", "").replace("\r", "");

        return responseStr;

    }

    /**
     * Post process response.
     *
     * @param responseStr
     *            the response str
     * @return the list
     */
    @Override
    protected List<Product> postProcessResponse(String responseStr) {

        List<Product> productList = new ArrayList<Product>();
        Pattern p = Pattern.compile("((nombre_.*\">).*(<\\/s))|((item_price\">))([^<]*)<");
        Matcher m = p.matcher(responseStr);

        while (m.find()) {
            Product product = new Product();

            String name = m.group(0);
            int majorSimbol = name.indexOf('>');
            int minorSimbol = name.indexOf('<');

            String id = name.substring(name.indexOf('_')+1,majorSimbol-1);
            
            product.setImage(StringUtils.join(imageHost,id,".jpg"));
            
            name = name.substring(majorSimbol + 1, minorSimbol);
            product.setName(name);
            

            m.find();

            String price = m.group(0);
            majorSimbol = price.indexOf('>');
            minorSimbol = price.indexOf('<');

            price = price.substring(majorSimbol + 1, minorSimbol);
            product.setPrice(Float.valueOf(price));

            product.setMarket(Market.MASYMAS);

            productList.add(product);
        }

        return productList;
    }

}
