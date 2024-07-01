package org.mach.source.controller;

import com.commercetools.api.models.custom_object.CustomObject;
import org.mach.source.model.customObj.CustomObjectModel;
import org.mach.source.service.CustomObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/customobj")
public class CustomObjectsController {
    @Autowired
    private CustomObjectService customObjectService;

    /*@PostMapping("/addCommunityOld")
    public String addCommunityOld(@RequestParam String communityName) throws ExecutionException, InterruptedException {
        return customObjectService.addCommunity(communityName);
    }*/

    @PostMapping("/addCommunity")
    public String addCommunity(@RequestBody CustomObjectModel customObjectModel) throws ExecutionException, InterruptedException {
        return customObjectService.addCommunity(customObjectModel);
    }

    @DeleteMapping("/removeCommunity")
    public CustomObject removeCommunity(@RequestParam String community) throws ExecutionException, InterruptedException {
        return customObjectService.removeCommunity(community);
    }


}
