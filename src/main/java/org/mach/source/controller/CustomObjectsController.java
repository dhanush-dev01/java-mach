package org.mach.source.controller;

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


}
