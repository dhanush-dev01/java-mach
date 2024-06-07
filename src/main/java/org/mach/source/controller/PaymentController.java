package org.mach.source.controller;

import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.payment.Payment;
import org.mach.source.model.stripe.PaymentModelRoot;
import org.mach.source.service.CartService;
import org.mach.source.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CartService cartService;

    @PostMapping("/codAnon")
    public CompletableFuture<?> startCODpaymentAnon(@RequestParam String anonymousId) throws JsonProcessingException {

        //final PaymentMethod cod = PaymentMethod.CASHONDELIVERY;
        CompletableFuture<Optional<Cart>> cartForAnonUser = cartService.getCartForAnonUser(anonymousId);
        return cartForAnonUser.thenApply(c -> {
            if (c.isPresent()) {
                return paymentService.addPayment("COD", anonymousId, c.get().getTaxedPrice().getTotalGross(), UUID.randomUUID().toString());
                //   .thenCompose(payment -> cartService.setPayment(c.get(), payment));
            }
            return CompletableFuture.completedFuture(null);
        }).thenCompose(m -> m);
    }

    @PostMapping("/cod")
    public CompletableFuture<?> startCODpayment(@RequestParam String customerid) throws JsonProcessingException {

        //final PaymentMethod cod = PaymentMethod.CASHONDELIVERY;
        CompletableFuture<Optional<Cart>> cartForUser = cartService.getCartForUser(customerid);
        return cartForUser.thenApply(c -> {
            if (c.isPresent()) {
                return paymentService.addPayment("COD",customerid, c.get().getTaxedPrice().getTotalGross(), UUID.randomUUID().toString());
                    //    .thenCompose(payment -> cartService.setPayment(c.get(), payment));
            }
            return CompletableFuture.completedFuture(null);
        }).thenCompose(e -> e);
    }

    @PostMapping("/stripe")
    public CompletableFuture<Payment> startStripePayment(@RequestBody PaymentModelRoot paymentModel) throws JsonProcessingException {
        System.out.println("***********************************************");
        System.out.println("Testing stripeeeeeeeee");
        System.out.println("###############################################");
        //CompletableFuture<Optional<Cart>> cartForUser = cartService.getCartForUserUsingPayment(customerid);

        CompletableFuture<Payment> paymentCF = paymentService.captureStripePayment(paymentModel);
        return paymentCF;

    }

}
