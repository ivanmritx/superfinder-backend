package com.supermarket.finder.service.finders.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.util.ArrayList;
import java.util.List;

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

@Service
@Order(OrderFinders.MERCADONA)
public class MercadonaFinder extends AbstractFinder implements Finder {

  /**
   * The logger.
   */
  private final Logger logger = LoggerFactory.getLogger(MercadonaFinder.class);

  private final String marketUri =
      "https://7uzjkl1dj0-dsn.algolia.net/1/indexes/products_prod_4315_es/query?x-algolia-application-id=7UZJKL1DJ0&x-algolia-api-key"
          + "=9d8f2e39e90df472b4f2e559a116fe17";

  @Override
  public Market getMarket() {
    return Market.MERCADONA;
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
   * Gets the request body.
   *
   * @return the post request body
   */
  protected BodyPublisher getBodyPost(final String term) {
    return HttpRequest.BodyPublishers.ofString(
        "{\"params\":\"query=" + term + "&clickAnalytics=true&analyticsTags=%5B%22web%22%5D&getRankingInfo=true\"}");
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
   * Gets the product list.
   *
   * @param responseJsonObj the response json obj
   * @return the product list
   */
  protected List<Product> getProductList(final JsonObject responseJsonObj) {

    final List<Product> productList = new ArrayList<Product>();

    final JsonArray productsJsonList = responseJsonObj.get("hits").getAsJsonArray();

    if (productsJsonList != null) {
      for (final JsonElement productJson : productsJsonList) {
        final JsonObject productObj = productJson.getAsJsonObject();
        final Product product = new Product();
        product.setMarket(Market.MERCADONA);
        product.setBrand("-");

        final JsonObject priceObj = productObj.get("price_instructions").getAsJsonObject();

        product.setPrice((priceObj).get("unit_price").getAsFloat());

        try {
          if (priceObj.get("reference_price") != null && priceObj.get("reference_format") != null) {
            final String price = priceObj.get("reference_price").getAsString().replace(".", ",");
            final String unit = priceObj.get("reference_format").getAsString();
            product.setPriceUnitOrKg(StringUtils.join(price, " €/", unit));
          }
        } catch (final Exception e) {
          this.logger.error("Mercadona get product unitPrice error", e);
        }

        product.setName(productObj.get("display_name").getAsString());

        final String imagePath = productObj.get("thumbnail").getAsString();
        if (!StringUtils.isBlank(imagePath)) {
          product.setImage(imagePath);
        }

        productList.add(product);
      }
    }

    return productList;
  }
}
