package com.supermarket.finder.service.finders;

import java.util.List;

import com.supermarket.finder.dto.Market;
import com.supermarket.finder.dto.Product;

/**
 * The Interface Finder.
 */
public interface Finder {

    /**
     * Find products by term.
     *
     * @param term
     *            the term
     * @return the list
     */
    public List<Product> findProductsByTerm(String term);
    
    /**
     * Get the finder market.
     *
     * @return the market
     */
    public Market getMarket();

}
