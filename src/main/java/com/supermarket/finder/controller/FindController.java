package com.supermarket.finder.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.supermarket.finder.dto.Market;
import com.supermarket.finder.dto.Product;
import com.supermarket.finder.service.finders.Finder;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class FindController.
 */
@RestController
@RequestMapping("/find")
public class FindController {

  @Autowired
  private List<Finder> marketFinderList;

  /**
   * Find by id.
   *
   * @param term the term
   * @return the list
   */
  @Tag(name = "Find by term", description = "find supermarkets products by term")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation"),
      @ApiResponse(responseCode = "500", description = "Unexpected server error")
  })
  @CrossOrigin(origins = "*", maxAge = 3600)
  @GetMapping
  public List<Product> findByTerm(@RequestParam(required = true) String term, final Market[] markets) {

    final List<Product> productList = new ArrayList<Product>();

    for (final Finder finder : this.marketFinderList) {
      if (markets == null || Arrays.stream(markets).anyMatch(finder.getMarket()::equals)) {
        productList.addAll(finder.findProductsByTerm(term));
      }
    }
    final List<Product> finalList = new ArrayList<Product>();
    term = term.trim();
    term = StringUtils.stripAccents(term);
    final String[] termSplit = term.toLowerCase().split(" ");
    for (final Product p : productList) {
      if (Arrays.stream(termSplit).allMatch(StringUtils.stripAccents(p.getName().toLowerCase())::contains)) {
        finalList.add(p);
      }
    }

    Collections.sort(finalList);

    return finalList;
  }
}
