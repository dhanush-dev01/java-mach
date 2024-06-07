package org.mach.source.controller;

import com.commercetools.api.models.cart.Cart;
import org.mach.source.service.CartService;
import org.mach.source.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;


    @PostMapping("/placeorderAnon")
    public CompletableFuture<CompletableFuture<?>> makeOrderAnonymous(@RequestParam String anonymousId) throws JsonProcessingException {
        CompletableFuture<Optional<Cart>> cartForUser = cartService.getCartForAnonUser(anonymousId);
        return cartForUser.thenApply(c -> {
            if (c.isPresent()) {
                return orderService.placeorder(c.get());
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    @PostMapping("/placeorder")
    public CompletableFuture<CompletableFuture<?>> makeOrder(@RequestParam String customerid) throws JsonProcessingException {
        CompletableFuture<Optional<Cart>> cartForUser = cartService.getCartForUser(customerid);
        return cartForUser.thenApply(c -> {
            if (c.isPresent()) {
                return orderService.placeorder(c.get());
            }
            return CompletableFuture.completedFuture(null);
        });
    }
}
