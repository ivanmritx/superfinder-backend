package com.supermarket.finder.service.finders.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.supermarket.finder.dto.Market;
import com.supermarket.finder.dto.Product;
import com.supermarket.finder.service.finders.AbstractFinder;
import com.supermarket.finder.service.finders.Finder;

@Service
@Order(OrderFinders.ALDI)
public class AldiFinder extends AbstractFinder implements Finder {
	/** The logger. */
	// private final Logger logger = LoggerFactory.getLogger(MercadonaFinder.class);

	private final String marketUri = "https://l9knu74io7-dsn.algolia.net/1/indexes/*/queries?X-Algolia-Api-Key=19b0e28f08344395447c7bdeea32da58&X-Algolia-Application-Id=L9KNU74IO7";

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
	protected BodyPublisher getBodyPost(String term) {
		return HttpRequest.BodyPublishers.ofString(
				"{\"requests\":[{\"indexName\":\"prod_es_es_es_assortment\",\"params\":\"clickAnalytics=true&facets=%5B%5D&highlightPostTag=%3C%2Fais-highlight-0000000000%3E&highlightPreTag=%3Cais-highlight-0000000000%3E&hitsPerPage=12&page=0&query="+term+"&tagFilters=\"}]}");
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
	protected List<Product> getProductList(JsonObject responseJsonObj) {

		List<Product> productList = new ArrayList<Product>();

		final JsonArray resultList = (JsonArray) responseJsonObj.get("results");
		for (JsonElement result : resultList) {

			final JsonArray productsJsonList = (JsonArray) ((JsonObject) result).get("hits");

			if (productsJsonList != null) {
				for (JsonElement productJson : productsJsonList) {
					JsonObject productObj = (JsonObject) productJson;
					Product product = new Product();
					product.setMarket(Market.ALDI);
					product.setBrand("-");
					if (productObj.get("salesPrice") != null) {

						product.setPrice(((JsonPrimitive) productObj.get("salesPrice")).getAsFloat());
						product.setName(productObj.get("productName").getAsString());

						final String imagePath = productObj.get("productPicture").getAsString();
						if (!StringUtils.isBlank(imagePath)) {
							product.setImage(imagePath);
						}

						productList.add(product);
					}
				}

			}
		}

		return productList;
	}
}
