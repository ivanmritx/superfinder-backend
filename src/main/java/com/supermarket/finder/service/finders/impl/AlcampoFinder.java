package com.supermarket.finder.service.finders.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.supermarket.finder.dto.Market;
import com.supermarket.finder.dto.Product;
import com.supermarket.finder.service.finders.AbstractFinder;
import com.supermarket.finder.service.finders.Finder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(OrderFinders.ALCAMPO)
public class AlcampoFinder extends AbstractFinder implements Finder {

  /**
   * The logger.
   */
  // private final Logger logger = LoggerFactory.getLogger(AlcampoFinder.class);

  private final String marketUri =
      "https://www.compraonline.alcampo.es/api/v5/products/search?limit=50&offset=0&sort=price&term=%s";

  private final String imageHost = "https://www.dia.es";

  @Override
  public Market getMarket() {
    return Market.ALCAMPO;
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

    final JsonObject productsJsonList = responseJsonObj.get("entities").getAsJsonObject().get("product").getAsJsonObject();

    if (productsJsonList != null) {
      for (final String productKey : productsJsonList.keySet()) {
        final JsonObject productObj = productsJsonList.get(productKey).getAsJsonObject();
        final Product product = new Product();
        product.setMarket(Market.ALCAMPO);
        product.setBrand(productObj.get("brand") != null ? productObj.get("brand").getAsString() : "");
        product.setName(productObj.get("name").getAsString());

        final JsonObject price = productObj.getAsJsonObject().get("price").getAsJsonObject();

        product.setPrice(price.getAsJsonObject().get("current").getAsJsonObject().get("amount").getAsFloat());

        if (price.get("unit") != null) {
          String label = price.get("unit").getAsJsonObject().get("label").getAsString();
          label = label.substring(label.lastIndexOf(".") + 1);

          label = StringUtils.join(
              price.get("unit").getAsJsonObject().get("current").getAsJsonObject().get("amount").getAsString().replace(".", ","), " €/",
              label);
          label = label.replace("litre", "Litro").replace("each", "unidad");
          product.setPriceUnitOrKg(label);
        }

        final JsonArray media = productObj.get("imagePaths").getAsJsonArray();
        if (media != null && !media.isEmpty()) {
          product.setImage(StringUtils.join(media.get(0).getAsString(), "/300x300.jpg"));
        }

        productList.add(product);
      }
    }

    return productList;
  }
}
