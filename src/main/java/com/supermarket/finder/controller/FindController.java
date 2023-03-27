package com.supermarket.finder.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        Collections.sort(productList);

        return productList;
    }
}
