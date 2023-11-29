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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * The Class CarrefourFinder.
 */
@Service
@Order(OrderFinders.CARREFOUR)
public class CarrefourFinder extends AbstractFinder implements Finder {

  /**
   * The logger.
   */
  // private final Logger logger = LoggerFactory.getLogger(CarrefourFinder.class);

  private final String marketUri =
      "https://www.carrefour.es/search-api/query/v1/search?query=%s&scope=desktop&lang=es&rows=24&start=0&origin=default&f.op=OR";

  @Override
  public Market getMarket() {
    return Market.CARREFOUR;
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

    final JsonArray productsJsonList = responseJsonObj.get("content").getAsJsonObject().get("docs").getAsJsonArray();

    if (productsJsonList != null) {
      for (final JsonElement productJson : productsJsonList) {
        final JsonObject productObj = productJson.getAsJsonObject();
        final Product product = new Product();
        product.setMarket(Market.CARREFOUR);
        product.setBrand(productObj.get("brand") == null ? "-" : productObj.get("brand").getAsString());
        product.setPrice(productObj.get("active_price").getAsFloat());

        if (productObj.get("price_per_unit_text") != null) {
          product.setPriceUnitOrKg(productObj.get("price_per_unit_text").getAsString());
        }

        product.setName(productObj.get("display_name").getAsString());
        product.setImage(productObj.get("image_path") != null ? productObj.get("image_path").getAsString() : StringUtils.EMPTY);

        productList.add(product);
      }
    }

    return productList;
  }
}
