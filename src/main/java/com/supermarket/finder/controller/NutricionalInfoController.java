package com.supermarket.finder.controller;

import com.supermarket.finder.dto.openfoodfact.OpendFoodSearchDto;
import com.supermarket.finder.service.nutricionalInfo.NutricionalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * The Class FindController.
 */
@RestController
@RequestMapping("/nutricional-info")
public class NutricionalInfoController {

    @Autowired
    private NutricionalInfoService nutricionalInfoService;

    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping
    public OpendFoodSearchDto findByName(@RequestParam(required = true) final String term) {
        return this.nutricionalInfoService.getNutricionalInfo(term);
    }
}
