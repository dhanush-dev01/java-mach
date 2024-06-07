package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.order.OrderFromCartDraft;
import com.commercetools.api.models.order.OrderFromCartDraftBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;

    public CompletableFuture<Order> placeorder(Cart cart){

        OrderFromCartDraft orderFromCartDraft = OrderFromCartDraftBuilder.of()
                .cart(CartResourceIdentifierBuilder.of().id(cart.getId()).build())
                .version(cart.getVersion())
                .build();

        return byProjectKeyRequestBuilder.orders()
                .post(orderFromCartDraft)
                .execute().thenApply(ApiHttpResponse::getBody);


    }

}
