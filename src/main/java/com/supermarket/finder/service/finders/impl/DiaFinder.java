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
@Order(OrderFinders.DIA)
public class DiaFinder extends AbstractFinder implements Finder {

  /**
   * The logger.
   */
  // private final Logger logger = LoggerFactory.getLogger(DiaFinder.class);

  private final String marketUri = "https://www.dia.es/api/v1/search-back/search/reduced?q=%s&page=1";

  private final String imageHost = "https://www.dia.es";

  @Override
  public Market getMarket() {
    return Market.DIA;
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

    final JsonArray productsJsonList = responseJsonObj.get("search_items").getAsJsonArray();

    if (productsJsonList != null) {
      for (final JsonElement productJson : productsJsonList) {
        final JsonObject productObj = productJson.getAsJsonObject();
        final Product product = new Product();
        product.setMarket(Market.DIA);
        product.setBrand("-");
        final JsonObject pricesObj = productObj.get("prices").getAsJsonObject();
        product.setPrice(pricesObj.get("price").getAsFloat());

        if (pricesObj.get("price_per_unit") != null) {
          product.setPriceUnitOrKg(StringUtils.join(pricesObj.get("price_per_unit").getAsString().replace(".", ","), " €/",
              pricesObj.get("measure_unit").getAsString()));
        }

        product.setName(productObj.get("display_name").getAsString());

        final String imagePath = productObj.get("image").getAsString();
        if (!StringUtils.isBlank(imagePath)) {
          product.setImage(StringUtils.join(this.imageHost, imagePath));
        }

        productList.add(product);
      }
    }

    return productList;
  }
}
