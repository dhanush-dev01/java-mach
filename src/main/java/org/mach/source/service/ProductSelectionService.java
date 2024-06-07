package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.custom_object.CustomObjectPagedQueryResponse;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.customer_group.CustomerGroupResourceIdentifierBuilder;
import com.commercetools.api.models.product.ProductReference;
import com.commercetools.api.models.product_selection.AssignedProductReference;
import com.commercetools.api.models.product_selection.ProductSelection;
import com.commercetools.api.models.product_selection.ProductSelectionProductPagedQueryResponse;
import com.commercetools.api.models.type.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import kotlin.collections.ArrayDeque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProductSelectionService {

    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;
    @Autowired
    private UtilityService utilityService;

    public List<String> getProductSelectionProducts(String community) throws ExecutionException, InterruptedException {

        List<String> customerObjectValues = utilityService.getCustomerObjectValues();
        List<String> productIds = new ArrayList<>();

        if(customerObjectValues.contains(community)){
            CompletableFuture<ProductSelectionProductPagedQueryResponse> productSelectionProductPagedQueryResponseCF = byProjectKeyRequestBuilder.productSelections()
                    .withKey(community + "-key").products()
                    .get().execute().thenApply(ApiHttpResponse::getBody);


            CompletableFuture<List<AssignedProductReference>> listCF = productSelectionProductPagedQueryResponseCF.thenApply(f -> f.getResults());
            List<AssignedProductReference> assignedProductReferences = listCF.get();

            for (AssignedProductReference assignedProductReference : assignedProductReferences) {
                ProductReference product = assignedProductReference.getProduct();
                productIds.add(product.getId());
            }
            return productIds;
        }
        productIds.add(community + " is not a valid community");
        return productIds;
    }

    /*public CompletableFuture<String> addCommunity(String community, String customerid) throws ExecutionException, InterruptedException {

        Object obj = getCustomObjects().get();
        LinkedHashMap lmap = (LinkedHashMap) obj;
        List communities = (List) lmap.get("communities");

        CompletableFuture<Customer> customerCF = byProjectKeyRequestBuilder.customers()
                .withId(customerid).get()
                .execute().thenApply(ApiHttpResponse::getBody);

        if(communities.contains(community)) {
            *//*CompletableFuture<Customer> customerCF = byProjectKeyRequestBuilder.customers()
                    .withId(customerid).get()
                    .execute().thenApply(ApiHttpResponse::getBody);*//*

            CustomFields customFields = CustomFieldsBuilder.of()
                    .type(TypeReferenceBuilder.of().id("dhbc").build())
                    .fields(FieldContainerBuilder.of().addValue("community", community).build())
                    .build();

            CustomerSetCustomTypeAction customerSetCustomTypeAction = CustomerSetCustomTypeActionBuilder.of()
                    .type(TypeResourceIdentifierBuilder.of().key("type-customer").build()).build();

            CompletableFuture<Customer> customerCFUp = customerCF.thenApply(f -> {
                return byProjectKeyRequestBuilder.customers()
                        .withId(f.getId())
                        .post(CustomerUpdateBuilder.of()
                                .version(f.getVersion())
                                .plusActions(customerSetCustomTypeAction).build())
                        .execute().thenApply(ApiHttpResponse::getBody);
            }).thenCompose(m -> m);

            CustomerSetCustomFieldAction custSetCustomField = CustomerSetCustomFieldActionBuilder.of()
                    .name("community").value(community).build();
       *//* CustomerUpdate customerUpdate = CustomerUpdateBuilder.of()
                .version()
                .plusActions(custSetCustomField).build();*//*
            return customerCFUp.thenCompose(e -> {
                byProjectKeyRequestBuilder.customers()
                        .withId(e.getId())
                        .post(CustomerUpdateBuilder.of()
                                .version(e.getVersion())
                                .plusActions(custSetCustomField).build()).execute().thenApply(ApiHttpResponse::getBody);
                return CompletableFuture.completedFuture("Customer " + e.getEmail() +" added/updated to community - "+community);
            });

        }
        return CompletableFuture.completedFuture(community +" is not a valid community available in Joggerhub. Available communities are "+ communities);

    }

    private CompletableFuture<Object> getCustomObjects() {
        CompletableFuture<CustomObjectPagedQueryResponse> customObjectPagedQueryResponseCF = byProjectKeyRequestBuilder.customObjects()
                .get().execute().thenApply(ApiHttpResponse::getBody);

        return customObjectPagedQueryResponseCF.thenApply(f -> f.getResults().get(0).getValue());
        




    }*/
}
