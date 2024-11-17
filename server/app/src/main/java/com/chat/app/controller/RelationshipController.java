package com.chat.app.controller;

import com.chat.app.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(/api/relationship)
public class RelationshipController {

    @Autowired
    private RelationshipService relationshipService;
}
