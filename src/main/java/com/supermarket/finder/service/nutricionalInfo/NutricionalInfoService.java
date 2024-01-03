package com.supermarket.finder.service.nutricionalInfo;

import com.supermarket.finder.dto.openfoodfact.OpendFoodSearchDto;
import com.supermarket.finder.service.nutricionalInfo.client.OpendFoodFactClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class NutricionalInfoService.
 */
@Service
public class NutricionalInfoService {
    @Autowired
    private OpendFoodFactClient opendFoodFactClient;

    /**
     * Gets the nutricional info.
     *
     * @param productName the product name
     * @return the nutricional info
     */
    public OpendFoodSearchDto getNutricionalInfo(final String productName) {
        final OpendFoodSearchDto productDtoList = this.opendFoodFactClient.getOpendFoodProduct(productName);
        return productDtoList;
        //return !productDtoList.getProducts().isEmpty() ? productDtoList.getProducts().get(0) : null;
    }
}
