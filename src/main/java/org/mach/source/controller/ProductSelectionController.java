package org.mach.source.controller;

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
}
