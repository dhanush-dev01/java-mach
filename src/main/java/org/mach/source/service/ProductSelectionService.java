package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.common.*;
import com.commercetools.api.models.product.Product;
import com.commercetools.api.models.product.ProductReference;
import com.commercetools.api.models.product_selection.AssignedProductReference;
import com.commercetools.api.models.product_selection.ProductSelectionProductPagedQueryResponse;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.mach.source.dto.ProductDTO;
import org.mach.source.model.customObj.CustomObjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProductSelectionService {

    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;
    @Autowired
    private UtilityService utilityService;

    public List<ProductDTO> getProductSelectionProducts(String community) throws ExecutionException, InterruptedException {

        //List<String> customerObjectValues = utilityService.getCustomerObjectValues1();
        List<String> communityNames = new ArrayList<>();
        List<ProductDTO> productDTOS = new ArrayList<>();
        List<CustomObjectModel> customerObjectValues = utilityService.getCustomerObjectValues();
        for(CustomObjectModel customerObjectModel : customerObjectValues){
            communityNames.add(customerObjectModel.getName());
            // System.out.println("test");
        }
        List<String> productIds = new ArrayList<>();

        if(communityNames.contains(community)){
            CompletableFuture<ProductSelectionProductPagedQueryResponse> productSelectionProductPagedQueryResponseCF = byProjectKeyRequestBuilder.productSelections()
                    .withKey(community + "-key").products()
                    .get().execute().thenApply(ApiHttpResponse::getBody);


            CompletableFuture<List<AssignedProductReference>> listCF = productSelectionProductPagedQueryResponseCF.thenApply(f -> f.getResults());
            List<AssignedProductReference> assignedProductReferences = listCF.get();

            for (AssignedProductReference assignedProductReference : assignedProductReferences) {
                ProductDTO productDTO = new ProductDTO();
                ProductReference product = assignedProductReference.getProduct();
                Product productFull = byProjectKeyRequestBuilder.products().withId(product.getId())
                        .get().executeBlocking().getBody();
                Map<String, String> nameValues = productFull.getMasterData().getCurrent().getName().values();
                if(Objects.nonNull(nameValues.get("en-GB"))){
                    productDTO.setName(nameValues.get("en-GB"));
                } else {
                    productDTO.setName(nameValues.get("en-US"));
                }
                List<Price> prices = productFull.getMasterData().getCurrent().getMasterVariant().getPrices();
                for(Price price : prices){
                    if(Objects.nonNull(price) && Objects.nonNull(price.getCountry()) && price.getCountry().equalsIgnoreCase("IN")){
                      //  TypedMoney value = price.getValue();
                        productDTO.setPrice(price.getValue());
                        break;
                    }
                }
                if(Objects.isNull(productDTO.getPrice())){
                    CentPrecisionMoney sampleMoney = TypedMoneyBuilder.of()
                            .centPrecisionBuilder()
                            .centAmount(10000l)
                            .fractionDigits(2)
                            .currencyCode("INR")
                            .build();
                    productDTO.setPrice(sampleMoney);
                }

                List<Image> images = productFull.getMasterData().getCurrent().getMasterVariant().getImages();
                if(Objects.nonNull(images) && images.size() > 0){
                    productDTO.setImageUrl(images.get(0).getUrl());
                }
                //productIds.add(product.getId());
                productDTOS.add(productDTO);
            }
            return productDTOS;
        }
        //productIds.add(community + " is not a valid community");
        return null;
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
