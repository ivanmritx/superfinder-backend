package com.supermarket.finder.service.finders.impl;

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

/**
 * The Class HipercorFinder.
 */
@Service
@Order(OrderFinders.HIPERCOR)
public class HipercorFinder extends AbstractFinder implements Finder {

  /**
   * The logger.
   */
  private final Logger logger = LoggerFactory.getLogger(HipercorFinder.class);

  private final String marketUri =
      "https://www.hipercor.es/alimentacion/api/catalog/supermercado/type_ahead/?question=%s&scope=supermarket&center=010MOH&results=10";

  private final String imageHost = "https:";

  @Override
  public Market getMarket() {
    return Market.HIPERCOR;
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
   * Gets the product list.
   *
   * @param responseJsonObj the response json obj
   * @return the product list
   */
  protected List<Product> getProductList(final JsonObject responseJsonObj) {

    final List<Product> productList = new ArrayList<Product>();

    final JsonArray productsJsonList = responseJsonObj
        .get("catalog_result").getAsJsonObject().get("products_list").getAsJsonObject().get("items").getAsJsonArray();

    if (productsJsonList != null) {
      for (final JsonElement productJson : productsJsonList) {
        final JsonObject productObj = productJson.getAsJsonObject().get("product").getAsJsonObject();
        final Product product = new Product();
        product.setMarket(Market.HIPERCOR);
        product.setBrand("-");

        final JsonObject priceObj = productObj.get("price").getAsJsonObject();

        product.setPrice(
            priceObj.get("seo_price").getAsFloat());
        try {
          if (priceObj.get("pum_price_only") != null) {
            product.setPriceUnitOrKg(
                priceObj.get("pum_price_only").getAsString().replace("&euro; ", "€"));
          } else if (priceObj.get("pum_price") != null) {
            product.setPriceUnitOrKg(
                priceObj.get("pum_price").getAsString().replace("&euro; ", "€"));

          }
        } catch (final Exception e) {
          this.logger.error("Hipercor get product unitPrice error", e);
        }

        product.setName(productObj.get("name").getAsString());

        String imagePath = productObj.get("media").getAsJsonObject().get("thumbnail_url").getAsString();
        if (!StringUtils.isBlank(imagePath)) {
          imagePath = imagePath.replace("40x40", "325x325");
          product.setImage(StringUtils.join(this.imageHost, imagePath));
        }

        productList.add(product);
      }
    }

    return productList;
  }

}
