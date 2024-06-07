package org.mach.source.controller;

import com.commercetools.api.models.cart.Cart;
import org.mach.source.model.cart.ItemToCart;
import org.mach.source.service.CartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/carts")
public class CartsController {
    @Autowired
    private CartService cartService;
    
    @PostMapping("/addAnonCart")
    public CompletableFuture<Cart> addItemToCartAnonymous(@RequestBody ItemToCart itemToCart) throws JsonProcessingException {
        return cartService.addItemToCartAnonymous(itemToCart);
    }

    @PostMapping("/addToCustomerCart")
    public CompletableFuture<Cart> addItemToCustomerCart(@RequestBody ItemToCart itemToCart, @RequestParam String customerid) throws JsonProcessingException {
        return cartService.addItemToCustomerCart(itemToCart, customerid);
    }
}
