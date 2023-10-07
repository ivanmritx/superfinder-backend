package com.supermarket.finder.service.finders.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.supermarket.finder.dto.Market;
import com.supermarket.finder.dto.Product;
import com.supermarket.finder.service.finders.AbstractFinder;
import com.supermarket.finder.service.finders.Finder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class AlimerkaFinder.
 */
@Service
@Order(OrderFinders.ALIMERKA)
public class AlimerkaFinder extends AbstractFinder implements Finder {
    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(AlimerkaFinder.class);

    private final String marketUri = "https://www.alimerkaonline.es/ali-ws/tienda/busqueda/%s/false/1/1000/0";

    private final String tokenUri = "https://www.alimerkaonline.es/ali-ws/acceso/cp/33402";

    private final String imageHost = "https://storage.googleapis.com/storage.alimerka.es/recursos";

    private String iauthtoken = null;

    private LocalDateTime lastTokenDate = LocalDateTime.now();


    @Override
    public Market getMarket() {
        return Market.ALIMERKA;
    }

    /**
     * Gets the market uri.
     *
     * @return the market uri
     */
    protected String getMarketUri() {
        return this.marketUri + "?iauthtoken=" + this.getToken();
    }

    /**
     * Gets the token.
     *
     * @return the token
     */
    private String getToken() {

        final LocalDateTime now = LocalDateTime.now();
        final Duration duration = Duration.between(now, lastTokenDate);
        final long diff = Math.abs(duration.toMinutes());

        if (diff > 5 || iauthtoken == null) {

            try {

                final HttpRequest request = HttpRequest.newBuilder().uri(new URI(this.tokenUri))
                        .timeout(Duration.ofSeconds(10)).POST(BodyPublishers.noBody()).build();

                final HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
                        BodyHandlers.ofString());

                final JsonObject responseJsonObj = new Gson().fromJson(response.body(), JsonObject.class);

                this.iauthtoken = responseJsonObj.get("iauthtoken").getAsString();
                this.lastTokenDate = LocalDateTime.now();

            } catch (URISyntaxException | IOException | InterruptedException e) {
                logger.error("Alimerka get token error", e);
            }
        }

        return this.iauthtoken;
    }

    /**
     * Gets the product list.
     *
     * @param responseJsonObj the response json obj
     * @return the product list
     */
    protected List<Product> getProductList(JsonObject responseJsonObj) {

        List<Product> productList = new ArrayList<Product>();

        final JsonArray productsJsonList = (JsonArray) responseJsonObj.get("listPage");

        int count = 0;

        if (productsJsonList != null) {
            for (JsonElement productJson : productsJsonList) {
                JsonObject productObj = (JsonObject) productJson;
                Product product = new Product();
                product.setMarket(Market.ALIMERKA);
                product.setBrand("-");
                product.setPrice(Float.valueOf(productObj.get("pvpnormal").getAsString().replace(",", ".")));
                try {
                    JsonElement unidad = productObj.get("unidad");
                    if (unidad != null) {
                        if ("GR.".equals(unidad.getAsString())) {
                            String price = productObj.get("pvp").getAsString();
                            product.setPriceUnitOrKg(StringUtils.join(price, " €/Kg"));
                        } else if ("Unidad".equals(unidad.getAsString()) && productObj.get("textounidad") != null) {
                            String unitPriceStr = productObj.get("textounidad").getAsString();
                            String unitPrice = unitPriceStr.split(StringUtils.SPACE)[3].replace(",", ".");
                            if (NumberUtils.isCreatable(unitPrice) && !product.getPrice().equals(Float.valueOf(unitPrice))) {
                                product.setPriceUnitOrKg(StringUtils.join(unitPriceStr.split(StringUtils.SPACE)[3], " €/Unidad"));
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Alimerka get product unitPrice error", e);
                }

                product.setName(productObj.get("descripcion").getAsString());

                final String imagePath = productObj.get("urlimagen") != null ? productObj.get("urlimagen").getAsString() : StringUtils.EMPTY;
                if (!StringUtils.isBlank(imagePath)) {
                    product.setImage(StringUtils.join(imageHost, imagePath));
                }

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
     * @param responseString the response string
     * @return the string
     */
    protected String preProcessResponse(String responseString) {
        String response = responseString.replace("fn(", "");
        response = response.substring(0, response.length() - 1);

        return response;
    }

}
