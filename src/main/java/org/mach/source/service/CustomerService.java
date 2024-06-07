package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.custom_object.CustomObjectDraft;
import com.commercetools.api.models.custom_object.CustomObjectDraftBuilder;
import com.commercetools.api.models.custom_object.CustomObjectPagedQueryResponse;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.customer_group.CustomerGroupResourceIdentifierBuilder;
import com.commercetools.api.models.type.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.mach.source.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CustomerService {

    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;
    @Autowired
    private UtilityService utilityService;

    public CompletableFuture<CustomerSignInResult> addCustomer(String customerType, CustomerDTO customerDTO) throws ExecutionException, InterruptedException {

       /* CustomFieldsBuilder community = CustomFieldsBuilder.of()
                .type(TypeReferenceBuilder.of().id("6bddce2f-a21d-4f6e-93bb-181f16ad3488").build())
                .fields(FieldContainerBuilder.of().addValue("community", customerDTO.getCommunity()).build());*/
        CustomFieldsDraft customFieldsDraft = null;
                List<String> customerObjectValues = utilityService.getCustomerObjectValues();
        if(customerObjectValues.contains(customerDTO.getCommunity())){
            customFieldsDraft = CustomFieldsDraftBuilder.of()
                    .type(TypeResourceIdentifierBuilder.of().key("type-customer").build())
                    .fields(FieldContainerBuilder.of().addValue("community", customerDTO.getCommunity()).build())
                    .build();
        }

        CustomerDraft customerDraft = CustomerDraftBuilder.of()
                //.customerGroup(CustomerGroupResourceIdentifierBuilder.of().key("cust-normal").build())
                .firstName(customerDTO.getFirstName())
                .lastName(customerDTO.getLastName())
                .email(customerDTO.getEmail())
                .password(customerDTO.getPassword())
                .custom(customFieldsDraft)
                .build();
        if(customerType.equalsIgnoreCase("leader")){
            customerDraft.setCustomerGroup(CustomerGroupResourceIdentifierBuilder.of().key("cust-leader").build());
        } else {
            customerDraft.setCustomerGroup(CustomerGroupResourceIdentifierBuilder.of().key("cust-normal").build());
        }
        return byProjectKeyRequestBuilder.customers()
                .post(customerDraft)
                .execute().thenApply(ApiHttpResponse::getBody);
    }

    public CompletableFuture<String> addToCommunity(String community, String customerid) throws ExecutionException, InterruptedException {

        List<String> communities = utilityService.getCustomerObjectValues();

        CompletableFuture<Customer> customerCF = byProjectKeyRequestBuilder.customers()
                .withId(customerid).get()
                .execute().thenApply(ApiHttpResponse::getBody);

        if(communities.contains(community)) {
            /*CompletableFuture<Customer> customerCF = byProjectKeyRequestBuilder.customers()
                    .withId(customerid).get()
                    .execute().thenApply(ApiHttpResponse::getBody);*/

            /*CustomFields customFields = CustomFieldsBuilder.of()
                    .type(TypeReferenceBuilder.of().id("dhbc").build())
                    .fields(FieldContainerBuilder.of().addValue("community", community).build())
                    .build();*/

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

    public CompletableFuture<CustomerSignInResult> getCustomer(CustomerDTO customerDTO) {
        CustomerSignin customerSignin = CustomerSigninBuilder.of()
                .email(customerDTO.getEmail())
                .password(customerDTO.getPassword())
                .build();

        CompletableFuture<CustomerSignInResult> customerSignInResultCF = byProjectKeyRequestBuilder.login()
                .post(customerSignin)
                .execute().thenApply(ApiHttpResponse::getBody);

        CompletableFuture<CustomFields> customFieldsCompletableFuture = customerSignInResultCF.thenApply(e -> e.getCustomer().getCustom());
        return byProjectKeyRequestBuilder.login()
                .post(customerSignin)
                .execute().thenApply(ApiHttpResponse::getBody);
    }

    public CompletableFuture<String> getCommunity(String customerid) {
        CompletableFuture<Customer> customerCF = byProjectKeyRequestBuilder.customers()
                .withId(customerid).get()
                .execute().thenApply(ApiHttpResponse::getBody);

        return customerCF.thenApply(f -> {
            CustomFields customFields = f.getCustom();
            if(customFields != null) {
                FieldContainer fieldContainer = customFields.getFields();
                if(fieldContainer != null) {
                    Map<String, Object> values = fieldContainer.values();
                    String community = (String) values.get("community");
                    return (community!=null ? community:"Community not found");
                }
            }
            return "Community not found";
        });
    }

    public CompletableFuture<String> updateRecords(String customerid, String date, int recordVar) {
        CustomObjectPagedQueryResponse customObjectPagedQueryResponse = byProjectKeyRequestBuilder.customObjects()
                .withContainer(customerid + "-container").get()
                .executeBlocking().getBody();
        List<CustomObject> customObjectPagedQueryResponseResults = customObjectPagedQueryResponse.getResults();
        if(customObjectPagedQueryResponseResults != null && !customObjectPagedQueryResponseResults.isEmpty()) {
            Object object = customObjectPagedQueryResponseResults.get(0).getValue();
            List<String> records = (List) object;
            //List<String> records = (List) lmap.get("records");
            boolean exists = false;
            for (String record:records){
                String dateSubstring = record.substring(0, 10);
                if(dateSubstring.equalsIgnoreCase(date)){
                    records.remove(record);
                    records.add(dateSubstring+"_"+recordVar);
                    exists = true;
                    break;
                }
            }
            if(!exists){
                records.add(date+"_"+recordVar);
            }
            CustomObjectDraft customObjectDraft = CustomObjectDraftBuilder.of()
                    .container(customerid + "-container")
                    .key(customerid+"-key")
                    .value(records)
                    .build();
            byProjectKeyRequestBuilder.customObjects()
                    .post(customObjectDraft).executeBlocking();
            return CompletableFuture.completedFuture("Custom objects updated");


        }

        if(customObjectPagedQueryResponseResults != null && customObjectPagedQueryResponseResults.isEmpty()) {
            //Add records
            List<String> records = new ArrayList<>();
            records.add(date+"_"+recordVar);
            CustomObjectDraft customObjectDraft = CustomObjectDraftBuilder.of()
                    .container(customerid + "-container")
                    .key(customerid+"-key")
                    .value(records)
                    .build();
            byProjectKeyRequestBuilder.customObjects()
                    .post(customObjectDraft).executeBlocking();
            return CompletableFuture.completedFuture("Custom objects created");

        }

        return CompletableFuture.completedFuture("Finished");
    }

    public CompletableFuture<List<String>> getRecords(String customerid) {
        CustomObjectPagedQueryResponse customObjectPagedQueryResponse = byProjectKeyRequestBuilder.customObjects()
                .withContainer(customerid + "-container")
                .get().executeBlocking().getBody();
        List<CustomObject> customObjectPagedQueryResponseResults = customObjectPagedQueryResponse.getResults();
        if (customObjectPagedQueryResponseResults != null && customObjectPagedQueryResponseResults.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        if (customObjectPagedQueryResponseResults != null && !customObjectPagedQueryResponseResults.isEmpty()) {
            Object object = customObjectPagedQueryResponseResults.get(0).getValue();
            List<String> records = (List) object;
            return CompletableFuture.completedFuture(records);
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}