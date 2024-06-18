package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.custom_object.CustomObjectDraftBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CustomObjectService {
    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;
    @Autowired
    private UtilityService utilityService;


    public String addCommunity(String communityName) throws ExecutionException, InterruptedException {
        List<String> customerObjectValues = utilityService.getCustomerObjectValues();
        if(!customerObjectValues.contains(communityName)){
            customerObjectValues.add(communityName);
            byProjectKeyRequestBuilder.customObjects()
                    .post(CustomObjectDraftBuilder.of()
                            .container("community-container")
                            .key("community-key")
                            .value(customerObjectValues)
                            .build())
                    .executeBlocking().getBody();
            return communityName+ " add to community list";
        }

        return communityName+ " is already present in community list";
    }
}
