package org.mach.source.controller;

import com.commercetools.api.models.cart.Cart;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mach.source.service.CartService;
import org.mach.source.service.OrderService;
import org.mach.source.service.ProductSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/productselection")
public class ProductSelectionController {

    @Autowired
    private ProductSelectionService productSelectionService;


    @GetMapping("/getProductSelectionProducts")
    public List<String> getProductSelectionProducts(@RequestParam String community) throws ExecutionException, InterruptedException {
        return productSelectionService.getProductSelectionProducts(community);
    }
}
