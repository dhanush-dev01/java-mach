package org.mach.source.controller;

import com.commercetools.api.models.product_selection.ProductSelection;
import org.mach.source.dto.ProductDTO;
import org.mach.source.service.ProductSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/productselection")
public class ProductSelectionController {

    @Autowired
    private ProductSelectionService productSelectionService;


    @GetMapping("/getProductSelectionProducts")
    public List<ProductDTO> getProductSelectionProducts(@RequestParam String community) throws ExecutionException, InterruptedException {
        return productSelectionService.getProductSelectionProducts(community);
    }

    @PostMapping("/addProductToCommunity")
    public ProductSelection addProductToCommunity(@RequestParam String community, @RequestParam String sku) throws ExecutionException, InterruptedException {
        return productSelectionService.addProductToCommunity(community, sku);
    }
}
