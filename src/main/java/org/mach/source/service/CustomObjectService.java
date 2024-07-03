package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.custom_object.CustomObjectDraftBuilder;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.product_selection.ProductSelection;
import org.mach.source.model.customObj.CustomObjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class CustomObjectService {
    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;
    @Autowired
    private UtilityService utilityService;


    /*public String addCommunity(String communityName) throws ExecutionException, InterruptedException {
        List<String> customerObjectValues = utilityService.getCustomerObjectValues1();
        if(!customerObjectValues.contains(communityName)){
            customerObjectValues.add(communityName);
            byProjectKeyRequestBuilder.customObjects()
                    .post(CustomObjectDraftBuilder.of()
                            .container("community-container")
                            .key("community-key")
                            .value(customerObjectValues)
                            .build())
                    .executeBlocking().getBody();
            return communityName+ " added to community list";
        }

        return communityName+ " is already present in community list";
    }*/

    public String addCommunity(CustomObjectModel customObjectModel) throws ExecutionException, InterruptedException {
        List<String> communityNames = new ArrayList<>();
        List<CustomObjectModel> customerObjectValues = utilityService.getCustomerObjectValues();
        for(CustomObjectModel customerObjectModel : customerObjectValues){
            communityNames.add(customerObjectModel.getName());
           // System.out.println("test");
        }
        if(!communityNames.contains(customObjectModel.getName())){
            customerObjectValues.add(customObjectModel);
            byProjectKeyRequestBuilder.customObjects()
                    .post(CustomObjectDraftBuilder.of()
                            .container("community-container-upd")
                            .key("community-key-upd")
                            .value(customerObjectValues)
                            .build())
                    .executeBlocking().getBody();
            return customObjectModel.getName()+ " added to community list";
        }

        return customObjectModel.getName()+ " is already present in community list";

    }

    public CustomObject removeCommunity(String community) throws ExecutionException, InterruptedException {
        List<CustomObjectModel> customerObjectValues = utilityService.getCustomerObjectValues();
        CustomObjectModel tempCustomObjectModel = new CustomObjectModel();
        for(CustomObjectModel customerObjectModel : customerObjectValues){
            if(customerObjectModel.getName().equals(community)){
                tempCustomObjectModel = customerObjectModel;
            }
        }

        customerObjectValues.remove(tempCustomObjectModel);

        removeCustomersFromCommunity(community);
        removeProductSelections(community);


        return byProjectKeyRequestBuilder.customObjects()
                .post(CustomObjectDraftBuilder.of().container("community-container-upd")
                        .key("community-key-upd")
                        .value(customerObjectValues).build())
                .executeBlocking().getBody();
    }

    private ProductSelection removeProductSelections(String community) {
        ProductSelection productSelection = byProjectKeyRequestBuilder.productSelections().withKey(community + "-key")
                .get().executeBlocking().getBody();
        return byProjectKeyRequestBuilder.productSelections().withKey(community+"-key")
                .delete().addVersion(productSelection.getVersion()).executeBlocking().getBody();
    }

    private Customer removeCustomersFromCommunity(String community) {
        CustomerPagedQueryResponse customerPagedQueryResponse = byProjectKeyRequestBuilder.customers().get()
                .addWhere("custom(fields(community = :community))")
                .addPredicateVar("community", community)
                .executeBlocking().getBody();

        if (customerPagedQueryResponse.getResults() != null && customerPagedQueryResponse.getResults().size() > 0) {
            List<Customer> queryResponseResults = customerPagedQueryResponse.getResults();
            for(Customer customer : queryResponseResults){
                //CustomFields customerCustom = customer.getCustom();
                CustomerSetCustomFieldAction customerSetCustomFieldAction = CustomerSetCustomFieldActionBuilder.of()
                        .name("community").value(null).build();

                return byProjectKeyRequestBuilder.customers().withId(customer.getId())
                        .post(CustomerUpdateBuilder.of()
                                .version(customer.getVersion())
                                .actions(customerSetCustomFieldAction).build())
                        .executeBlocking().getBody();

            }
        }
        return null;
    }

    public List<CustomObjectModel> getCommunity() throws ExecutionException, InterruptedException {
        return utilityService.getCustomerObjectValues();
    }
}
