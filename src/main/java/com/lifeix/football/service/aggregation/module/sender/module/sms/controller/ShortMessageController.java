package com.lifeix.football.service.aggregation.module.sender.module.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lifeix.football.service.aggregation.module.sender.model.ShortMessage;
import com.lifeix.football.service.aggregation.module.sender.module.sms.service.ShortMessageService;

@RestController
@RequestMapping(value = "/sms")
public class ShortMessageController {

    @Autowired
    private ShortMessageService shortMessageService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void sendShortMessages(@RequestBody(required = true) List<ShortMessage> shortMessages) {
	shortMessageService.sendShortMessages(shortMessages);
    }

}
