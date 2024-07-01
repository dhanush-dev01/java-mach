package org.mach.source.service;

import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.custom_object.CustomObjectDraft;
import com.commercetools.api.models.custom_object.CustomObjectDraftBuilder;
import com.commercetools.api.models.custom_object.CustomObjectPagedQueryResponse;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.customer_group.CustomerGroup;
import com.commercetools.api.models.customer_group.CustomerGroupPagedQueryResponse;
import com.commercetools.api.models.customer_group.CustomerGroupReference;
import com.commercetools.api.models.customer_group.CustomerGroupResourceIdentifierBuilder;
import com.commercetools.api.models.graph_ql.GraphQLRequest;
import com.commercetools.api.models.graph_ql.GraphQLRequestBuilder;
import com.commercetools.api.models.type.*;
import com.fasterxml.jackson.databind.JsonNode;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.mach.source.dto.CustomerDTO;
import org.mach.source.model.customObj.CustomObjectModel;
import org.mach.source.model.customer.CustomerDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private ByProjectKeyRequestBuilder byProjectKeyRequestBuilder;
    @Autowired
    private UtilityService utilityService;

    public CompletableFuture<CustomerSignInResult> addCustomer(CustomerDTO customerDTO) throws ExecutionException, InterruptedException {

        CustomFieldsDraft customFieldsDraft = null;
        List<CustomObjectModel> customerObjectValues = utilityService.getCustomerObjectValues();
        List<String> communityNames = new ArrayList<>();
        for (CustomObjectModel customerObjectModel : customerObjectValues) {
            communityNames.add(customerObjectModel.getName());
        }

        if (communityNames.contains(customerDTO.getCommunity())) {
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
        if (Objects.nonNull(customerDTO.getCustomerType()) && customerDTO.getCustomerType().equalsIgnoreCase("leader")) {
            customerDraft.setCustomerGroup(CustomerGroupResourceIdentifierBuilder.of().key("cust-leader").build());
        } else {
            customerDraft.setCustomerGroup(CustomerGroupResourceIdentifierBuilder.of().key("cust-normal").build());
        }
        return byProjectKeyRequestBuilder.customers()
                .post(customerDraft)
                .execute().thenApply(ApiHttpResponse::getBody);
    }

    public CompletableFuture<String> addToCommunity(String community, String customerid) throws ExecutionException, InterruptedException {

        List<CustomObjectModel> customerObjectValues = utilityService.getCustomerObjectValues();
        List<String> communityNames = new ArrayList<>();
        for (CustomObjectModel customerObjectModel : customerObjectValues) {
            communityNames.add(customerObjectModel.getName());
        }

        CompletableFuture<Customer> customerCF = byProjectKeyRequestBuilder.customers()
                .withId(customerid).get()
                .execute().thenApply(ApiHttpResponse::getBody);

        if (communityNames.contains(community)) {

            Customer customerGet = customerCF.get();
            if(Objects.isNull(customerGet.getCustom())){
                CustomerSetCustomTypeAction customerSetCustomTypeAction = CustomerSetCustomTypeActionBuilder.of()
                        .type(TypeResourceIdentifierBuilder.of().key("type-customer").build()).build();

                customerCF = byProjectKeyRequestBuilder.customers()
                        .withId(customerGet.getId())
                        .post(CustomerUpdateBuilder.of()
                                .version(customerGet.getVersion())
                                .plusActions(customerSetCustomTypeAction).build())
                        .execute().thenApply(ApiHttpResponse::getBody);
            }

            CustomerSetCustomFieldAction custSetCustomField = CustomerSetCustomFieldActionBuilder.of()
                    .name("community").value(community).build();

            return customerCF.thenCompose(e -> {
                byProjectKeyRequestBuilder.customers()
                        .withId(e.getId())
                        .post(CustomerUpdateBuilder.of()
                                .version(e.getVersion())
                                .plusActions(custSetCustomField).build()).execute().thenApply(ApiHttpResponse::getBody);
                return CompletableFuture.completedFuture("Customer " + e.getEmail() + " added/updated to community - " + community);
            });

        }
        return CompletableFuture.completedFuture(community + " is not a valid community available in Joggerhub. Available communities are " + communityNames);

    }

    public CompletableFuture<CustomerDTO> getCustomer(CustomerDTO customerDTO) {
        CustomerDTO customerDTOOp = new CustomerDTO();
        CustomerSignin customerSignin = CustomerSigninBuilder.of()
                .email(customerDTO.getEmail())
                .password(customerDTO.getPassword())
                .build();

        CompletableFuture<CustomerSignInResult> customerSignInResultCF = byProjectKeyRequestBuilder.login()
                .post(customerSignin)
                .execute().thenApply(ApiHttpResponse::getBody);

       // CompletableFuture<CustomFields> customFieldsCompletableFuture = customerSignInResultCF.thenApply(e -> e.getCustomer().getCustom());
        /*return byProjectKeyRequestBuilder.login()
                .post(customerSignin)
                .execute().thenApply(ApiHttpResponse::getBody);*/
        return customerSignInResultCF.thenApply(f -> {
            String custGrpId = f.getCustomer().getCustomerGroup().getId();
            String custGrpKey = byProjectKeyRequestBuilder.customerGroups()
                    .withId(custGrpId).get().executeBlocking().getBody().getKey();

            customerDTOOp.setFirstName(f.getCustomer().getFirstName());
            customerDTOOp.setLastName(f.getCustomer().getLastName());
            customerDTOOp.setEmail(f.getCustomer().getEmail());
            customerDTOOp.setCustomerType(custGrpKey);
            customerDTOOp.setCustomerId(f.getCustomer().getId());
            return customerDTOOp;
        });
    }

    public CompletableFuture<String> getCommunity(String customerid) {
        CompletableFuture<Customer> customerCF = byProjectKeyRequestBuilder.customers()
                .withId(customerid).get()
                .execute().thenApply(ApiHttpResponse::getBody);

        return customerCF.thenApply(f -> {
            CustomFields customFields = f.getCustom();
            if (customFields != null) {
                FieldContainer fieldContainer = customFields.getFields();
                if (fieldContainer != null) {
                    Map<String, Object> values = fieldContainer.values();
                    String community = (String) values.get("community");
                    return (community != null ? community : "Community not found");
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
        if (customObjectPagedQueryResponseResults != null && !customObjectPagedQueryResponseResults.isEmpty()) {
            Object object = customObjectPagedQueryResponseResults.get(0).getValue();
            List<String> records = (List) object;
            //List<String> records = (List) lmap.get("records");
            boolean exists = false;
            for (String record : records) {
                String dateSubstring = record.substring(0, 10);
                if (dateSubstring.equalsIgnoreCase(date)) {
                    records.remove(record);
                    records.add(dateSubstring + "_" + recordVar);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                records.add(date + "_" + recordVar);
            }
            CustomObjectDraft customObjectDraft = CustomObjectDraftBuilder.of()
                    .container(customerid + "-container")
                    .key(customerid + "-key")
                    .value(records)
                    .build();
            byProjectKeyRequestBuilder.customObjects()
                    .post(customObjectDraft).executeBlocking();
            return CompletableFuture.completedFuture("Custom objects updated");


        }

        if (customObjectPagedQueryResponseResults != null && customObjectPagedQueryResponseResults.isEmpty()) {
            //Add records
            List<String> records = new ArrayList<>();
            records.add(date + "_" + recordVar);
            CustomObjectDraft customObjectDraft = CustomObjectDraftBuilder.of()
                    .container(customerid + "-container")
                    .key(customerid + "-key")
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
            List<String> replacedRecords = new ArrayList<>();
            List<String> records = (List) object;
            for (String record : records) {
                String temp = record.replace("_", " --> ");
                ;
                replacedRecords.add(temp);
            }
            return CompletableFuture.completedFuture(replacedRecords);
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    public CompletableFuture<Customer> resetPassword(CustomerDTO customerDTO) {
        CustomerToken customerToken = byProjectKeyRequestBuilder.customers()
                .passwordToken().post(CustomerCreatePasswordResetTokenBuilder.of()
                        .email(customerDTO.getEmail()).build())
                .executeBlocking().getBody();
        CompletableFuture<Customer> customerCF = byProjectKeyRequestBuilder.customers().passwordReset()
                .post(CustomerResetPasswordBuilder.of()
                        .tokenValue(customerToken.getValue())
                        .newPassword(customerDTO.getNewPassword()).build())
                .execute().thenApply(ApiHttpResponse::getBody);
        return customerCF;
    }

    public CompletableFuture<Customer> resetMyPassword(CustomerDTO customerDTO) {
        CustomerToken myToken = byProjectKeyRequestBuilder.customers()
                .passwordToken().post(CustomerCreatePasswordResetTokenBuilder.of()
                        .email(customerDTO.getEmail()).build())
                .executeBlocking().getBody();
        return byProjectKeyRequestBuilder.me().password().reset()
                .post(MyCustomerResetPasswordBuilder.of()
                        .tokenValue(myToken.getValue())
                        .newPassword(customerDTO.getNewPassword()).build())
                .execute().thenApply(ApiHttpResponse::getBody);
    }

    public CompletableFuture<Customer> queryCustomerWithId(String customerid) {
        return byProjectKeyRequestBuilder.customers().withId(customerid).get().execute().thenApply(ApiHttpResponse::getBody);

    }

    public JsonNode queryCustFNameGql(String name) {
        GraphQLRequest gqlRequest = GraphQLRequestBuilder.of()
                .query("query ReturnASingleCustomerSearch($customerFilter: String) {\n" +
                        "  customers(where: $customerFilter) {\n" +
                        "    results{\n" +
                        "      firstName\n" +
                        "      lastName\n" +
                        "      middleName\n" +
                        "      email\n" +
                        "      id\n" +
                        "    }    \n" +
                        "  }\n" +
                        "}")
                .variables(b -> b.addValue("customerFilter", "firstName=\"" + name + "\""))
                .build();

        ApiHttpResponse<JsonNode> jsonNodeApiHttpResponse = byProjectKeyRequestBuilder.graphql().post(gqlRequest).executeBlocking(JsonNode.class);
        return jsonNodeApiHttpResponse.getBody().at("/data/customers/results");
    }

    public List<CustomerDTO> getCustomersByCommunity(String communityName) {
        CustomerPagedQueryResponse customerPagedQueryResponse = byProjectKeyRequestBuilder.customers().get()
                .addWhere("custom(fields(community = :community))")
                .addPredicateVar("community", communityName)
                .executeBlocking().getBody();
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        if (customerPagedQueryResponse.getResults() != null && customerPagedQueryResponse.getResults().size() > 0) {
            List<Customer> queryResponseResults = customerPagedQueryResponse.getResults();
            for (Customer customer : queryResponseResults) {
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setEmail(customer.getEmail());
                customerDTO.setFirstName(customer.getFirstName());
                customerDTO.setLastName(customer.getLastName());

                customerDTOList.add(customerDTO);
            }
        }

        return customerDTOList;
    }

    public CompletableFuture<String> appendRecords(String customerid, String date, String recordVar) {
        CustomObjectPagedQueryResponse customObjectPagedQueryResponse = byProjectKeyRequestBuilder.customObjects()
                .withContainer(customerid + "-container").get()
                .executeBlocking().getBody();
        List<CustomObject> customObjectPagedQueryResponseResults = customObjectPagedQueryResponse.getResults();
        if (customObjectPagedQueryResponseResults != null && !customObjectPagedQueryResponseResults.isEmpty()) {
            Object object = customObjectPagedQueryResponseResults.get(0).getValue();
            List<String> records = (List) object;

            records.add(date + "_" + recordVar);
            CustomObjectDraft customObjectDraft = CustomObjectDraftBuilder.of()
                    .container(customerid + "-container")
                    .key(customerid + "-key")
                    .value(records)
                    .build();
            byProjectKeyRequestBuilder.customObjects()
                    .post(customObjectDraft).executeBlocking();
            return CompletableFuture.completedFuture("Custom objects updated");


        }

        if (customObjectPagedQueryResponseResults != null && customObjectPagedQueryResponseResults.isEmpty()) {
            //Add records
            List<String> records = new ArrayList<>();
            records.add(date + "_" + recordVar);
            CustomObjectDraft customObjectDraft = CustomObjectDraftBuilder.of()
                    .container(customerid + "-container")
                    .key(customerid + "-key")
                    .value(records)
                    .build();
            byProjectKeyRequestBuilder.customObjects()
                    .post(customObjectDraft).executeBlocking();
            return CompletableFuture.completedFuture("Custom objects created");

        }

        return CompletableFuture.completedFuture("Finished");
    }

    public CompletableFuture<Customer> updateCustomerDetails(CustomerDetails customerDetails, String customerid) throws ExecutionException, InterruptedException {

        List<CustomerSetCustomFieldAction> actions = new ArrayList<>();
        CompletableFuture<Customer> customerCF = byProjectKeyRequestBuilder.customers()
                .withId(customerid).get()
                .execute().thenApply(ApiHttpResponse::getBody);
        Customer customerGet = customerCF.get();
        if(Objects.isNull(customerGet.getCustom())){
            CustomerSetCustomTypeAction customerSetCustomTypeAction = CustomerSetCustomTypeActionBuilder.of()
                        .type(TypeResourceIdentifierBuilder.of().key("type-customer").build()).build();

            customerCF = byProjectKeyRequestBuilder.customers()
                    .withId(customerGet.getId())
                    .post(CustomerUpdateBuilder.of()
                            .version(customerGet.getVersion())
                            .plusActions(customerSetCustomTypeAction).build())
                    .execute().thenApply(ApiHttpResponse::getBody);
        }

        actions.add(CustomerSetCustomFieldActionBuilder.of()
                .name("age").value(customerDetails.getAge()).build());
        actions.add(CustomerSetCustomFieldActionBuilder.of()
                .name("weight").value(customerDetails.getWeight()).build());
        actions.add(CustomerSetCustomFieldActionBuilder.of()
                .name("height").value(customerDetails.getHeight()).build());
        actions.add(CustomerSetCustomFieldActionBuilder.of()
                .name("gender").value(customerDetails.getGender()).build());

        CustomerSetCustomFieldAction[] customerSetCustomFieldActionArray = new CustomerSetCustomFieldAction[actions.size()];

            return customerCF.thenCompose(e -> {
                return byProjectKeyRequestBuilder.customers()
                        .withId(e.getId())
                        .post(CustomerUpdateBuilder.of()
                                .version(e.getVersion())
                                .plusActions(actions.toArray(customerSetCustomFieldActionArray)).build()).execute().thenApply(ApiHttpResponse::getBody);
            });

    }

    public CustomerDTO getLeaderOfCommunity(String communityName) {
        CustomerPagedQueryResponse customerPagedQueryResponse = byProjectKeyRequestBuilder.customers().get()
                .addWhere("custom(fields(community = :community))")
                .addPredicateVar("community", communityName)
                .executeBlocking().getBody();
        CustomerDTO customerDTO = new CustomerDTO();


        if(Objects.nonNull(customerPagedQueryResponse.getResults()) && !customerPagedQueryResponse.getResults().isEmpty()) {
            List<Customer> queryResponseResults = customerPagedQueryResponse.getResults();


            CustomerGroupPagedQueryResponse customerGroupPagedQueryResponse = byProjectKeyRequestBuilder.customerGroups().get()
                    .executeBlocking().getBody();
            List<CustomerGroup> collect = customerGroupPagedQueryResponse.getResults().stream().filter(e -> e.getKey().equalsIgnoreCase("cust-leader")).collect(Collectors.toList());

            for(Customer customer : queryResponseResults){
                CustomerGroupReference customerGroup = customer.getCustomerGroup();
                if(Objects.nonNull(customerGroup) && customerGroup.getId().equalsIgnoreCase(collect.get(0).getId())){
                    customerDTO.setEmail(customer.getEmail());
                    customerDTO.setFirstName(customer.getFirstName());
                    customerDTO.setLastName(customer.getLastName());
                    return customerDTO;
                }
            }
        }
        return customerDTO;
    }
}