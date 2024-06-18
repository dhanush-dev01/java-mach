package org.mach.source.controller;

import org.mach.source.service.CustomObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/customobj")
public class CustomObjectsController {
    @Autowired
    private CustomObjectService customObjectService;

    @PostMapping("/addCommunity")
    public String addCommunity(@RequestParam String communityName) throws ExecutionException, InterruptedException {
        return customObjectService.addCommunity(communityName);
    }


}
