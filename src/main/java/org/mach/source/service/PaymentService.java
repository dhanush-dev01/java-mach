package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.common.MoneyBuilder;
import com.commercetools.api.models.common.TypedMoney;
import com.commercetools.api.models.payment.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {
    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;

    public CompletableFuture<Payment> addPayment(String paymentMethod, String anonymousId, TypedMoney totalGross, String interfaceId) {
        return byProjectKeyRequestBuilder.payments().post(createPaymentDraft(paymentMethod, totalGross, interfaceId))
                .execute()
                .thenApply(ApiHttpResponse::getBody);
    }

    private PaymentDraft createPaymentDraft(String paymentMethod, TypedMoney money, String interfaceId) {
        return PaymentDraftBuilder.of()
                .amountPlanned(MoneyBuilder.of()
                        .centAmount(money.getCentAmount())
                        .currencyCode(money.getCurrencyCode())
                        .build())
                .paymentMethodInfo(PaymentMethodInfoBuilder.of()
                        .method(paymentMethod)
                        //.paymentInterface(paymentMethod.getProvider().getDisplayName())
                        .build())
                .interfaceId(interfaceId)
                .transactions(TransactionDraftBuilder.of()
                        .amount(MoneyBuilder.of()
                                .centAmount(money.getCentAmount())
                                .currencyCode(money.getCurrencyCode())
                                .build())
                        .state(TransactionState.INITIAL)
                        .type(TransactionType.CHARGE)
                        .build())
                .build();
    }
}
