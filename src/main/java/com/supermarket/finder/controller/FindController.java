package com.supermarket.finder.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supermarket.finder.dto.Product;
import com.supermarket.finder.service.finders.Finder;

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
     * @param term
     *            the term
     * @return the list
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping
    public List<Product> findByTerm(@RequestParam(required = true) String term) {

        final List<Product> productList = new ArrayList<Product>();

        for (Finder finder : marketFinderList) {
            productList.addAll(finder.findProductsByTerm(term));
        }
        List<Product> finalList = new ArrayList<Product>();
        term = term.trim();
        term = StringUtils.stripAccents(term);
        String[] termSplit = term.toLowerCase().split(" ");
        for (Product p : productList) {
            if (Arrays.stream(termSplit).allMatch(StringUtils.stripAccents(p.getName().toLowerCase())::contains)) {
                finalList.add(p);
            }
        }

        Collections.sort(finalList);

        return finalList;
    }
}
