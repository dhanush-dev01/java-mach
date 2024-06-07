package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.AddressBuilder;
import org.mach.source.model.customer.CustomerAddress;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ShippingAddressService {
    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;

    public CompletableFuture<Cart> setShippingAddress(Cart cart, CustomerAddress address){
        CartSetShippingAddressAction cartSetShippingAddressAction = CartSetShippingAddressActionBuilder.of()
                .address(AddressBuilder.of()
                        .country(address.getCountry())
                        .city(address.getCity())
                        .apartment(address.getBuilding()).build()).build();
        CartUpdate cartUpdate = CartUpdateBuilder.of()
                .actions(cartSetShippingAddressAction)
                .version(cart.getVersion()).build();
        CompletableFuture<Cart> updatedCart = byProjectKeyRequestBuilder.carts()
                .withId(cart.getId())
                .post(cartUpdate).execute().thenApply(ApiHttpResponse::getBody);

        return updatedCart;


    }
}
