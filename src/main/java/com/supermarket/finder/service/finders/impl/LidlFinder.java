package com.supermarket.finder.service.finders.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
@Order(OrderFinders.LIDL)
public class LidlFinder extends AbstractFinder implements Finder {

  /**
   * The logger.
   */
  // private final Logger logger = LoggerFactory.getLogger(MasymasFinder.class);

  private final String marketUri =
      "https://www.lidl.es/es/search?q=%s&assortment=ES&locale=es_ES&version=v2.0.0&variant=default&idsOnly=true";

  private final String imageHost = "https://masymas-services.supermasymas.com/fotos/";

  @Override
  public Market getMarket() {
    return Market.LIDL;
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

    if (responseJsonObj != null) {

      final JsonArray productsJsonList = (JsonArray) responseJsonObj.get("list");

      int count = 0;

      if (productsJsonList != null) {
        for (final JsonElement productJson : productsJsonList) {
          final JsonObject productObj = (JsonObject) productJson;
          final Product product = new Product();
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
   * @param responseString the response string
   * @return the string
   */
  protected String preProcessResponse(final String responseString) {

    String responseStr = responseString;
    final int startJsonPos = responseStr.indexOf("grid-item-container");
    if (startJsonPos < 0) {
      return "";
    }

    responseStr = responseStr.substring(startJsonPos);
    final int endJsonPos = responseStr.indexOf("space p-t p-lr");
    responseStr = responseStr.substring(0, endJsonPos);
    responseStr = responseStr.replace("\n", "").replace("\r", "");

    return responseStr;

  }

  /**
   * Post process response.
   *
   * @param responseStr the response str
   * @return the list
   */
  @Override
  protected List<Product> postProcessResponse(final String responseStr) {
    System.out.println(responseStr);

    final List<Product> productList = new ArrayList<Product>();
    final Pattern p = Pattern.compile(
        "src=\"(.*?)\"|plp-product-grid-box-tile__title.*<strong>(.*?)<\\/strong>|plp-product-grid-box-tile__price.*<b>(.*?)<sup>(.*?)" 
            + "<\\/sup>|baseprice\">(.*?)<\\/small>");
    final Matcher m = p.matcher(responseStr);

    while (m.find()) {
      final Product product = new Product();

      String name = m.group(0);
      int majorSimbol = name.indexOf('>');
      int minorSimbol = name.indexOf('<');

      final String id = name.substring(name.indexOf('_') + 1, majorSimbol - 1);

      product.setImage(StringUtils.join(this.imageHost, id, ".jpg"));

      name = name.substring(majorSimbol + 1, minorSimbol);
      product.setName(name);

      m.find();

      String price = m.group(0);
      majorSimbol = price.indexOf('>');
      minorSimbol = price.indexOf('<');

      price = price.substring(majorSimbol + 1, minorSimbol);
      product.setPrice(Float.valueOf(price));

      product.setMarket(Market.LIDL);

      productList.add(product);
    }

    return productList;
  }

}
