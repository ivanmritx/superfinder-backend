package com.supermarket.finder.service.nutricionalInfo.client;

import com.supermarket.finder.dto.openfoodfact.OpendFoodSearchDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "opendFoodFactsClient", url = "https://es.openfoodfacts.org/cgi")
public interface OpendFoodFactClient {
    @RequestMapping(method = RequestMethod.GET, value = "/search.pl?search_terms={productName}&search_simple=1&json=1", consumes = "application/json", produces = "application/json")
    OpendFoodSearchDto getOpendFoodProduct(@PathVariable("productName") String productName);
}
