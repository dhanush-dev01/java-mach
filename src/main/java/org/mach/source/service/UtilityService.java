package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.custom_object.CustomObjectPagedQueryResponse;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UtilityService {
    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;


    public List<String> getCustomerObjectValues() throws ExecutionException, InterruptedException {
        Object obj = getCustomObjects().get();
        LinkedHashMap lmap = (LinkedHashMap) obj;
        List communities = (List) lmap.get("communities");
        return communities;
    }

    private CompletableFuture<Object> getCustomObjects() {
        CompletableFuture<CustomObjectPagedQueryResponse> customObjectPagedQueryResponseCF = byProjectKeyRequestBuilder.customObjects()
                .withContainer("community-container").get().execute().thenApply(ApiHttpResponse::getBody);

        return customObjectPagedQueryResponseCF.thenApply(f -> f.getResults().get(0).getValue());
    }
}
