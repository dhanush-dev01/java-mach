package org.mach.source.controller;

import com.commercetools.api.models.product.Product;
import org.mach.source.dto.ProductCreateDTO;
import org.mach.source.service.ProductSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductSelectionService productSelectionService;


    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody ProductCreateDTO productCreateDTO) throws ExecutionException, InterruptedException {
        return productSelectionService.addProduct(productCreateDTO);
    }
}
