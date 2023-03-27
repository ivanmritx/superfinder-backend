package com.supermarket.finder.dto;

/**
 * The Class Product.
 */
public class Product implements Comparable<Product> {

    /** The market. */
    private Market market;

    /** The brand. */
    private String brand;

    /** The name. */
    private String name;

    /** The price. */
    private float price;

    /**
     * @return the market
     */
    public Market getMarket() {
        return market;
    }

    /**
     * @param market
     *            the market to set
     */
    public void setMarket(Market market) {
        this.market = market;
    }

    /**
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand
     *            the brand to set
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the price
     */
    public Float getPrice() {
        return price;
    }

    /**
     * @param price
     *            the price to set
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * Gets the product price.
     *
     * @return the product price
     */
    public String getProductPrice() {
        return this.price + " €";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Product p) {
        if (this.getPrice() == null || p.getPrice() == null) {
            return 0;
        }
        return this.getPrice().compareTo(p.getPrice());
    }
}
