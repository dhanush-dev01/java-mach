package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.custom_object.CustomObjectPagedQueryResponse;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.mach.source.model.customObj.CustomObjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UtilityService {
    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;


    public List<String> getCustomerObjectValues1() throws ExecutionException, InterruptedException {
        Object obj = getCustomObjects1().get();
        List<String> communities = (List) obj;
        //LinkedHashMap lmap = (LinkedHashMap) obj;
        //List communities = (List) lmap.get("communities");
        return communities;
    }

    public List<CustomObjectModel> getCustomerObjectValues() throws ExecutionException, InterruptedException {
        Object obj = getCustomObjects().get();
        List<CustomObjectModel> customObjectModelArrayList = new ArrayList<>();
        List<LinkedHashMap<String, String>> communities = (List) obj;
        for (LinkedHashMap<String, String> community : communities) {
            CustomObjectModel customObjectModel = new CustomObjectModel();
            customObjectModel.setName(community.get("name"));
            customObjectModel.setAddress(community.get("address"));
            customObjectModel.setLocation(community.get("location"));
            customObjectModel.setAgenda(community.get("agenda"));
            customObjectModel.setIconUrl(community.get("iconUrl"));

            customObjectModelArrayList.add(customObjectModel);

        }
        return customObjectModelArrayList;
    }

    private CompletableFuture<Object> getCustomObjects1() {
        CompletableFuture<CustomObjectPagedQueryResponse> customObjectPagedQueryResponseCF = byProjectKeyRequestBuilder.customObjects()
                .withContainer("community-container").get().execute().thenApply(ApiHttpResponse::getBody);

        return customObjectPagedQueryResponseCF.thenApply(f -> f.getResults().get(0).getValue());
    }

    private CompletableFuture<Object> getCustomObjects() {
        CompletableFuture<CustomObjectPagedQueryResponse> customObjectPagedQueryResponseCF = byProjectKeyRequestBuilder.customObjects()
                .withContainer("community-container-upd").get().execute().thenApply(ApiHttpResponse::getBody);

        return customObjectPagedQueryResponseCF.thenApply(f -> f.getResults().get(0).getValue());
    }
}
