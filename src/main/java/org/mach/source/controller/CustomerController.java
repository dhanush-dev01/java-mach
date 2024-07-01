package org.mach.source.controller;

import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerSignInResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.mach.source.dto.CustomerDTO;
import org.mach.source.model.customer.CustomerDetails;
import org.mach.source.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/getCustomerById")
    public CompletableFuture<Customer> queryCustomerWithId(@RequestParam String id) {
        return customerService.queryCustomerWithId(id);
    }

    @GetMapping("/getCustomerBasicWithFname")
    public JsonNode queryCustomerByFname(@RequestParam String name) {
        return customerService.queryCustFNameGql(name);
    }

    @PostMapping("/signUpCustomer")
    public CompletableFuture<CustomerSignInResult> addCustomer(@RequestBody CustomerDTO customerDTO) throws ExecutionException, InterruptedException {
        return customerService.addCustomer(customerDTO);
    }

    @PostMapping("/addToCommunity")
    public CompletableFuture<String> addToCommunity(@RequestParam String community, @RequestParam String customerid) throws ExecutionException, InterruptedException {
        return customerService.addToCommunity(community, customerid);
    }

    @PostMapping("/signInCustomer")
    public CompletableFuture<CustomerSignInResult> signInCustomer(@RequestBody CustomerDTO customerDTO) throws ExecutionException, InterruptedException {
        return customerService.getCustomer(customerDTO);
    }

    @GetMapping("/getCommunity")
    public CompletableFuture<String> getCommunity(@RequestParam String customerid) throws ExecutionException, InterruptedException {
        return customerService.getCommunity(customerid);
    }

    /*@PostMapping("/updateRecords")
    public CompletableFuture<String> updateRecords(@RequestParam String customerid, @RequestParam String date, @RequestParam int record) throws ExecutionException, InterruptedException {
        return customerService.updateRecords(customerid, date, record);
    }*/

    @PostMapping("/appendRecords")
    public CompletableFuture<String> appendRecords(@RequestParam String customerid, @RequestParam String date, @RequestParam String record) throws ExecutionException, InterruptedException {
        return customerService.appendRecords(customerid, date, record);
    }

    /*@PostMapping("/appendGoals")
    public CompletableFuture<String> appendOrUpdateGoals(@RequestParam String customerid, @RequestBody GoalObj goalObj) throws ExecutionException, InterruptedException {
        return customerService.appendOrUpdateGoals(customerid, goalObj);
    }*/

    @GetMapping("/getRecords")
    public CompletableFuture<List<String>> getRecords(@RequestParam String customerid) throws ExecutionException, InterruptedException {
        return customerService.getRecords(customerid);
    }

    /*@GetMapping("/getGoals")
    public CompletableFuture<List<String>> getGoals(@RequestParam String customerid) throws ExecutionException, InterruptedException {
        return customerService.getGoals(customerid);
    }*/

    @PostMapping("/resetPassword")
    public CompletableFuture<Customer> resetPassword(@RequestBody CustomerDTO customerDTO) throws ExecutionException, InterruptedException {
        return customerService.resetPassword(customerDTO);
    }

    @PostMapping("/resetMyPassword")
    public CompletableFuture<Customer> resetMyPassword(@RequestBody CustomerDTO customerDTO) throws ExecutionException, InterruptedException {
        return customerService.resetMyPassword(customerDTO);
    }

    @GetMapping("/getCustomersByCommunity")
    public List<CustomerDTO> getCustomersByCommunity(@RequestParam String communityName) throws ExecutionException, InterruptedException {
        return customerService.getCustomersByCommunity(communityName);
    }

    @PostMapping("/updateCustomerDetails")
    public CompletableFuture<Customer> updateCustomerDetails(@RequestParam String customerid, @RequestBody CustomerDetails customerDetails) throws ExecutionException, InterruptedException {
        return customerService.updateCustomerDetails(customerDetails, customerid);
    }

    @GetMapping("/getLeaderOfCommunity")
    public CustomerDTO getLeaderOfCommunity(@RequestParam String communityName) throws ExecutionException, InterruptedException {
        return customerService.getLeaderOfCommunity(communityName);
    }

}
