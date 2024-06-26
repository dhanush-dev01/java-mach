package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.custom_object.CustomObjectDraftBuilder;
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
}
