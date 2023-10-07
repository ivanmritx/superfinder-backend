package com.supermarket.finder.service.finders.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.supermarket.finder.dto.Market;
import com.supermarket.finder.dto.Product;
import com.supermarket.finder.service.finders.AbstractFinder;
import com.supermarket.finder.service.finders.Finder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Order(OrderFinders.GADIS)
public class GadisFinder extends AbstractFinder implements Finder {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(GadisFinder.class);

    private final String marketUri = "https://www.gadisline.com/content/themes/gadislineth/view/functions/ajax_apiCinfo.php";
    private final String imageUri = "https://www.gadisline.com";

    private final String sessionUri = "https://www.gadisline.com";

    private String iSessionId = null;

    private LocalDateTime lastSessionDate = LocalDateTime.now();


    @Override
    public Market getMarket() {
        return Market.GADIS;
    }

    /**
     * Gets the market uri.
     *
     * @return the market uri
     */
    protected String getMarketUri() {
        return this.marketUri;
    }

    /**
     * Gets the token.
     *
     * @return the token
     */
    private void getSession() {

        final LocalDateTime now = LocalDateTime.now();
        final Duration duration = Duration.between(now, lastSessionDate);
        final long diff = Math.abs(duration.toMinutes());

        if (diff > 30 || iSessionId == null) {

            try {

                BodyPublisher body = HttpRequest.BodyPublishers.ofString("resource=postalCode&cl_lang=es&cl_postal_code=15001");

                final HttpRequest request = HttpRequest.newBuilder().uri(new URI(this.marketUri))
                        .timeout(Duration.ofSeconds(10)).POST(body).header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8").build();

                final HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
                        BodyHandlers.ofString());

                this.iSessionId = response.headers().firstValue("Set-Cookie").get();
                this.lastSessionDate = LocalDateTime.now();

            } catch (URISyntaxException | IOException | InterruptedException e) {
                logger.error("Gadis get sessionId error", e);
            }
        }
    }

    /**
     * Gets the request body.
     *
     * @return the post request body
     */
    protected BodyPublisher getBodyPost(String term) {
        return HttpRequest.BodyPublishers.ofString(
                "resource=productsListInfiniteScroll&lang=es&currentPostalCode=15010&currentUserId=&checksBrandsFilter=&checksPropertiesFilters=&productsListFilterSearch=&productsPage=0&orderProducts=&templateName=&isSearch=true&searchData=" + URLEncoder.encode(term, StandardCharsets.UTF_8));
    }

    /**
     * Gets the http method.
     *
     * @return the http method
     */
    protected HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    /**
     * Add the request headers
     *
     * @return
     */
    protected Builder addHeaders(Builder requetsBuilder) {
        this.getSession();
        requetsBuilder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        requetsBuilder.setHeader("Cookie", this.iSessionId + "; melindres_options={%22required%22:true%2C%22analytics%22:true}; _gid=GA1.2.1207255210.1695080623; _dc_gtm_UA-68572192-1=1; _dc_gtm_UA-68572192-2=1; _ga_FN6H4R59BJ=GS1.1.1695080622.1.1.1695080715.0.0.0; _ga_X30YGGL58X=GS1.2.1695080623.1.1.1695080715.60.0.0; _ga=GA1.2.844044833.1695080623; _gat_UA-68572192-2=1; _ga_D36JQJ7Q0R=GS1.2.1695080623.1.1.1695080715.0.0.0");
        //requetsBuilder.setHeader("Cookie","PHPSESSID=9ll7micjao18c94bo1aff582oj; melindres_options={%22required%22:true%2C%22analytics%22:true}; _ga_FN6H4R59BJ=GS1.1.1694968677.1.1.1694968710.0.0.0; _ga=GA1.1.1593199813.1694968678");

        return requetsBuilder;
    }

    /**
     * Gets the product list.
     *
     * @param responseJsonObj the response json obj
     * @return the product list
     */
    protected List<Product> getProductList(JsonObject responseJsonObj) {

        List<Product> productList = new ArrayList<Product>();

        final JsonArray productsJsonList = (JsonArray) ((JsonObject) responseJsonObj
                .get("dato")).get("productos");

        if (productsJsonList != null) {
            for (JsonElement productJson : productsJsonList) {
                JsonObject productObj = (JsonObject) productJson;
                Product product = new Product();
                product.setMarket(Market.GADIS);
                product.setBrand(productObj.get("marca").getAsString());
                product.setPrice(productObj.get("precio").getAsFloat());

                if (productObj.get("precioUnidad") != null && productObj.get("medidaTxt") != null && StringUtils.isNotBlank(productObj.get("medidaTxt").getAsString())) {
                    String price = productObj.get("precioUnidad").getAsString().replace(".", ",");
                    String unit = productObj.get("medidaTxt").getAsString();
                    product.setPriceUnitOrKg(StringUtils.join(price, " €/", unit));
                }
                product.setName(productObj.get("descripcionLarga").getAsString());

                JsonArray imagesList = ((JsonArray) productObj.get("imagenes"));

                if (imagesList != null && !imagesList.isEmpty()) {
                    String imagePath = ((JsonObject) imagesList.get(0)).get("path").getAsString();
                    if (!StringUtils.isBlank(imagePath)) {
                        imagePath = imagePath.replace("/var/www/html", this.imageUri);
                        product.setImage(imagePath);
                    }
                }

                productList.add(product);
            }
        }

        return productList;
    }
}
