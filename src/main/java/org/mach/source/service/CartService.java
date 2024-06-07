package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.payment.Payment;
import com.commercetools.api.models.payment.PaymentResourceIdentifierBuilder;
import org.mach.source.model.cart.ItemToCart;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class CartService {
    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;


    public CompletableFuture<Cart> addItemToCartAnonymous(ItemToCart itemToCart) {
        UUID uuid = UUID.randomUUID();
        LineItemDraft lineItemDraft = LineItemDraftBuilder.of()
                .sku(itemToCart.getSku())
                .quantity(itemToCart.getQuantity())
                .build();
        CartDraft cartDraft = CartDraftBuilder.of()
                .lineItems(lineItemDraft)
                .currency("INR")
                .country("IN")
                .anonymousId(uuid.toString())
               // .customerId(id)
                .build();

        return byProjectKeyRequestBuilder.carts()
                .post(cartDraft)
                .execute().thenApply(ApiHttpResponse::getBody);
    }

    public CompletableFuture<Optional<Cart>> getCartForAnonUser(String id) throws JsonProcessingException {

        return byProjectKeyRequestBuilder.carts()
                .get()
                .withWhere("anonymousId = \"" + id + "\"" + "and cartState = \"" + CartState.CartStateEnum.ACTIVE + "\"")
                .withLimit(1)
                .execute().thenApply(ApiHttpResponse::getBody).thenApply(e -> e.getResults().stream().findFirst());
    }

    public CompletableFuture<Optional<Cart>> getCartForUser(String id) throws JsonProcessingException {

        return byProjectKeyRequestBuilder.carts()
                .get()
                .withWhere("customerId = \"" + id + "\"" + "and cartState = \"" + CartState.CartStateEnum.ACTIVE + "\"")
                .withLimit(1)
                .execute().thenApply(ApiHttpResponse::getBody).thenApply(e -> e.getResults().stream().findFirst());
    }

    public CompletableFuture<Cart> setPayment(Cart cart, Payment payment) {
        final CartAddPaymentAction cartUpdateAction = CartAddPaymentActionBuilder.of()
                .payment(PaymentResourceIdentifierBuilder.of()
                        .id(payment.getId()).build())
                .build();

        CartUpdate cartUpdate = CartUpdateBuilder.of()
                .version(cart.getVersion())
                .actions(cartUpdateAction)
                .build();

        return byProjectKeyRequestBuilder.carts()
                .withId(cart.getId())
                .post(cartUpdate)
                .execute().thenApply(ApiHttpResponse::getBody);
    }

    public CompletableFuture<Cart> addItemToCustomerCart(ItemToCart itemToCart, String customerid) {
        LineItemDraft lineItemDraft = LineItemDraftBuilder.of()
                .sku(itemToCart.getSku())
                .quantity(itemToCart.getQuantity())
                .build();
        CartDraft cartDraft = CartDraftBuilder.of()
                .lineItems(lineItemDraft)
                .currency("INR")
                .country("IN")
                .customerId(customerid)
                .build();

        return byProjectKeyRequestBuilder.carts()
                .post(cartDraft)
                .execute().thenApply(ApiHttpResponse::getBody);
    }
}
