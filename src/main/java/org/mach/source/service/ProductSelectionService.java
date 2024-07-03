package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.category.CategoryResourceIdentifierBuilder;
import com.commercetools.api.models.common.*;
import com.commercetools.api.models.product.*;
import com.commercetools.api.models.product_selection.*;
import com.commercetools.api.models.product_type.ProductTypeResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.mach.source.dto.ProductCreateDTO;
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

    public Product addProduct(ProductCreateDTO productCreateDTO) {
        String sku = RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        ProductDraftBuilder productDraftBuilder = ProductDraftBuilder.of()
                .productType(ProductTypeResourceIdentifierBuilder.of()
                        .key(productCreateDTO.getProducttypekey()).build())
                .categories(CategoryResourceIdentifierBuilder.of()
                        .key(productCreateDTO.getCategorykey()).build())
                .name(LocalizedStringBuilder.of()
                        .addValue("en-GB", productCreateDTO.getName()).build())
                .description(LocalizedStringBuilder.of()
                        .addValue("en-GB", productCreateDTO.getDescription()).build())
                .slug(LocalizedStringBuilder.of()
                        .addValue("en-GB", productCreateDTO.getSlug()).build())
                .masterVariant(ProductVariantDraftBuilder.of()
                        .key(UUID.randomUUID().toString() + "key").sku(sku)
                        .prices(PriceDraftBuilder.of()
                                .country("IN").value(productCreateDTO.getPrice()).build())
                        .images(ImageBuilder.of().url(productCreateDTO.getImageUrl()).dimensions(ImageDimensionsBuilder.of().w(2).h(3).build()).build())
                        .build());


        return byProjectKeyRequestBuilder.products()
                .post(productDraftBuilder.build()).executeBlocking().getBody();

    }

    public ProductSelection addProductToCommunity(String community, String sku) {
        ProductPagedQueryResponse productPagedQueryResponse = byProjectKeyRequestBuilder.products()
                .get().addWhere("masterData(staged(masterVariant(sku in :sku))) or masterData(staged(variants(sku in :sku)))")
                .addPredicateVar("sku", sku)
                .executeBlocking().getBody();
        //String key = productPagedQueryResponse.getResults().get(0).getKey();
        String id = productPagedQueryResponse.getResults().get(0).getId();
        ProductSelectionAddProductAction productSelectionAddProductAction = ProductSelectionAddProductActionBuilder.of()
                .product(ProductResourceIdentifierBuilder.of().id(id).build())
                //.variantSelection(ProductVariantSelectionBuilder.of().inclusionBuilder().skus(sku).build())
                .build();

        ProductSelection productSelection = byProjectKeyRequestBuilder.productSelections().withKey(community + "-key")
                .get().executeBlocking().getBody();

        return byProjectKeyRequestBuilder.productSelections().withKey(community+"-key")
                .post(ProductSelectionUpdateBuilder.of()
                        .actions(productSelectionAddProductAction).version(productSelection.getVersion()).build()).executeBlocking().getBody();

    }

}
