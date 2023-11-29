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

@Service
@Order(OrderFinders.CONSUM)
public class ConsumFinder extends AbstractFinder implements Finder {

  /**
   * The logger.
   */
  // private final Logger logger = LoggerFactory.getLogger(ConsumFinder.class);

  private final String marketUri =
      "https://tienda.consum.es/api/rest/V1.0/catalog/searcher/products?q=%s&limit=20&showRecommendations=false";

  @Override
  public Market getMarket() {
    return Market.CONSUM;
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

    final JsonArray productsJsonList = ((JsonArray) ((JsonObject) responseJsonObj.get("catalog")).get("products"));

    if (productsJsonList != null) {
      for (final JsonElement productJson : productsJsonList) {
        final JsonObject productObj = productJson.getAsJsonObject();
        final JsonObject productData = productObj.get("productData").getAsJsonObject();
        final JsonObject productPrice = productObj.get("priceData").getAsJsonObject();
        final Product product = new Product();
        product.setMarket(Market.CONSUM);
        product.setBrand("-");
        final JsonObject pricesObj = productPrice.get("prices").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsJsonObject();
        product.setPrice(pricesObj.get("centAmount").getAsFloat());

        if (pricesObj.get("centUnitAmount") != null) {
          product.setPriceUnitOrKg(StringUtils.join(pricesObj.get("centUnitAmount").getAsString().replace(".", ","), " €/",
              productPrice.get("unitPriceUnitType").getAsString()));
        }

        product.setName(productData.get("description").getAsString());

        final JsonArray media = productObj.get("media").getAsJsonArray();
        if (media != null && !media.isEmpty()) {
          product.setImage(media.get(0).getAsJsonObject().get("url").getAsString());
        }

        productList.add(product);
      }
    }

    return productList;
  }
}
