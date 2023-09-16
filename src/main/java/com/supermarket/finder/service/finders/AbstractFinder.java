package com.supermarket.finder.service.finders;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.supermarket.finder.dto.Product;

/**
 * The Class AbstractFinder.
 */
public abstract class AbstractFinder implements Finder {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(AbstractFinder.class);

	@Override
	public List<Product> findProductsByTerm(String term) {

		List<Product> productList = new ArrayList<Product>();
		try {
			String uriTerm;

			uriTerm = URLEncoder.encode(term, StandardCharsets.UTF_8.toString());
			final HttpRequest request;
			if (HttpMethod.GET.equals(getHttpMethod())) {

				request = HttpRequest.newBuilder().uri(new URI(String.format(this.getMarketUri(), uriTerm)))
						.timeout(Duration.ofSeconds(10)).GET().build();
			} else {
				
				final BodyPublisher body = this.getBodyPost(term);
				
				request = HttpRequest.newBuilder().uri(new URI(String.format(this.getMarketUri(), uriTerm)))
						.timeout(Duration.ofSeconds(10)).POST(body).build();
			}

			final HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
					BodyHandlers.ofString());

			String responseStr = preProcessResponse(response.body());

			productList = postProcessResponse(responseStr);
		} catch (Exception e) {
			logger.error("Market get Product exception", e);
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
		return responseString;
	}

	/**
	 * Post process response.
	 *
	 * @param responseStr the response str
	 * @return the list
	 */
	protected List<Product> postProcessResponse(String responseStr) {
		final JsonObject responseJsonObj = new Gson().fromJson(responseStr, JsonObject.class);
		return this.getProductList(responseJsonObj);
	}

	/**
	 * Gets the market uri.
	 *
	 * @return the market uri
	 */
	abstract protected String getMarketUri();

	/**
	 * Gets the product list.
	 *
	 * @param responseJsonObj the response json obj
	 * @return the product list
	 */
	abstract protected List<Product> getProductList(JsonObject responseJsonObj);

	/**
	 * Gets the http method.
	 *
	 * @return the http method
	 */
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}
	
	/**
	 * Gets the request body.
	 *
	 * @param responseJsonObj the term
	 * @return the post request body
	 */
	protected BodyPublisher getBodyPost(String term) {
		return HttpRequest.BodyPublishers.noBody();
	}

	public enum HttpMethod {
		POST, GET
	}

}
